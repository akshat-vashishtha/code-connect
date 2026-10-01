package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.application.service.AdminMentorService;
import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.security.filter.InternalAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HexFormat;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternalSecurityProperties securityProperties;

    @MockBean
    private AdminMentorService adminMentorService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("Direct unauthenticated request without HMAC headers should be rejected with 401 Unauthorized JSON")
    void shouldRejectDirectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/mentors/pending"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Authentication required")));
    }

    @Test
    @DisplayName("Request with forged HMAC signature should be rejected with 401 Unauthorized JSON")
    void shouldRejectForgedHmacRequest() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        mockMvc.perform(get("/api/v1/admin/mentors/pending")
                .header(InternalAuthenticationFilter.HEADER_USER_ID, "hacker-user")
                .header(InternalAuthenticationFilter.HEADER_USER_ROLE, "ROLE_ADMIN")
                .header(InternalAuthenticationFilter.HEADER_USER_EMAIL, "hacker@evil.com")
                .header(InternalAuthenticationFilter.HEADER_TIMESTAMP, timestamp)
                .header(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, "invalid-forged-hmac-signature"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid request signature")));
    }

    @Test
    @DisplayName("Request with valid HMAC signature but non-admin role (ROLE_STUDENT) should be rejected with 403 Forbidden JSON")
    void shouldRejectNonAdminRoleWithForbidden() throws Exception {
        String userId = "student-user-1";
        String role = "ROLE_STUDENT";
        String email = "student@codeconnect.dev";
        String timestamp = String.valueOf(System.currentTimeMillis());

        String signature = calculateHmac("GET", "/api/v1/admin/mentors/pending", userId, role, email, timestamp, securityProperties.internalSecret());

        mockMvc.perform(get("/api/v1/admin/mentors/pending")
                .header(InternalAuthenticationFilter.HEADER_USER_ID, userId)
                .header(InternalAuthenticationFilter.HEADER_USER_ROLE, role)
                .header(InternalAuthenticationFilter.HEADER_USER_EMAIL, email)
                .header(InternalAuthenticationFilter.HEADER_TIMESTAMP, timestamp)
                .header(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, signature))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Access denied")));
    }

    @Test
    @DisplayName("Request with valid HMAC signature and admin role should succeed with 200 OK")
    void shouldAcceptValidSignedHmacRequest() throws Exception {
        String userId = "admin-user-1";
        String role = "ROLE_ADMIN";
        String email = "admin@codeconnect.dev";
        String timestamp = String.valueOf(System.currentTimeMillis());

        when(adminMentorService.getPendingMentorApplications()).thenReturn(Collections.emptyList());

        String signature = calculateHmac("GET", "/api/v1/admin/mentors/pending", userId, role, email, timestamp, securityProperties.internalSecret());

        mockMvc.perform(get("/api/v1/admin/mentors/pending")
                .header(InternalAuthenticationFilter.HEADER_USER_ID, userId)
                .header(InternalAuthenticationFilter.HEADER_USER_ROLE, role)
                .header(InternalAuthenticationFilter.HEADER_USER_EMAIL, email)
                .header(InternalAuthenticationFilter.HEADER_TIMESTAMP, timestamp)
                .header(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, signature))
            .andExpect(status().isOk());
    }

    private String calculateHmac(String method, String path, String userId, String role, String email, String timestamp, String secret) throws Exception {
        String payload = method.toUpperCase() + ":" + path + ":" + userId + ":" + role + ":" + email + ":" + timestamp;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
