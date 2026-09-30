package com.codeconnect.curriculum.infrastructure.security;

import com.codeconnect.curriculum.application.service.CurriculumService;
import com.codeconnect.curriculum.infrastructure.config.properties.InternalSecurityProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CurriculumSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternalSecurityProperties securityProperties;

    @MockBean
    private CurriculumService curriculumService;

    @MockBean
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("Should reject unauthenticated direct calls to /api/v1/curriculum/** with HTTP 403 Forbidden")
    void shouldRejectUnauthenticatedDirectCalls() throws Exception {
        mockMvc.perform(get("/api/v1/curriculum/tracks")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should accept zero-trust call with valid gateway HMAC signature and timestamp")
    void shouldAcceptCallWithValidHmacSignature() throws Exception {
        String userId = "student-100";
        String role = "ROLE_STUDENT";
        String email = "student@codeconnect.dev";
        String timestamp = String.valueOf(System.currentTimeMillis());

        String signature = calculateHmac("GET", "/api/v1/curriculum/tracks", userId, role, email, timestamp, securityProperties.internalSecret());

        mockMvc.perform(get("/api/v1/curriculum/tracks")
                .header(InternalAuthenticationFilter.HEADER_USER_ID, userId)
                .header(InternalAuthenticationFilter.HEADER_USER_ROLE, role)
                .header(InternalAuthenticationFilter.HEADER_USER_EMAIL, email)
                .header(InternalAuthenticationFilter.HEADER_TIMESTAMP, timestamp)
                .header(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, signature)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject header spoofing with invalid HMAC signature with HTTP 403 Forbidden")
    void shouldRejectHeaderSpoofingWithInvalidSignature() throws Exception {
        String userId = "hacker-1";
        String role = "ROLE_ADMIN";
        String email = "hacker@evil.com";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String forgedSignature = "deadbeef1234567890abcdef";

        mockMvc.perform(get("/api/v1/admin/curriculum/tracks")
                .header(InternalAuthenticationFilter.HEADER_USER_ID, userId)
                .header(InternalAuthenticationFilter.HEADER_USER_ROLE, role)
                .header(InternalAuthenticationFilter.HEADER_USER_EMAIL, email)
                .header(InternalAuthenticationFilter.HEADER_TIMESTAMP, timestamp)
                .header(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, forgedSignature)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    private static String calculateHmac(String method, String path, String userId, String role, String email, String timestamp, String secret) {
        try {
            String payload = method.toUpperCase() + ":" + path + ":" + userId + ":" + role + ":" + email + ":" + timestamp;
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC signature", e);
        }
    }
}
