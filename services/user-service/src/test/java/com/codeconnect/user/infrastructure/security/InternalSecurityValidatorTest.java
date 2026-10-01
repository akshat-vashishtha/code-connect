package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.security.filter.InternalAuthenticationFilter;
import com.codeconnect.user.infrastructure.security.hmac.HmacSignatureVerifier;
import com.codeconnect.user.infrastructure.security.token.InternalAuthenticationTokenFactory;
import com.codeconnect.user.infrastructure.security.validator.InternalSecurityValidator;
import com.codeconnect.user.infrastructure.security.validator.RequestTimestampValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalSecurityValidatorTest {

    private static final String TEST_SECRET          = "test-secret-key-1234567890-must-be-long";
    private static final long   CLOCK_SKEW_SECONDS   = 30L;

    private final InternalSecurityProperties     properties         = new InternalSecurityProperties(TEST_SECRET, CLOCK_SKEW_SECONDS);
    private final RequestTimestampValidator       timestampValidator = new RequestTimestampValidator(properties);
    private final HmacSignatureVerifier          signatureVerifier  = new HmacSignatureVerifier(properties);
    private final InternalAuthenticationTokenFactory tokenFactory   = new InternalAuthenticationTokenFactory();
    private final InternalSecurityValidator      validator          = new InternalSecurityValidator(timestampValidator, signatureVerifier, tokenFactory);

    @Test
    @DisplayName("Should detect presence of internal security headers")
    void shouldDetectInternalSecurityHeaders() {
        MockHttpServletRequest requestWithHeaders = new MockHttpServletRequest("GET", "/api/v1/users/profile");
        requestWithHeaders.addHeader(InternalAuthenticationFilter.HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        requestWithHeaders.addHeader(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, "some-sig");

        MockHttpServletRequest requestWithoutHeaders = new MockHttpServletRequest("GET", "/api/v1/users/profile");

        assertThat(validator.hasInternalSecurityHeaders(requestWithHeaders)).isTrue();
        assertThat(validator.hasInternalSecurityHeaders(requestWithoutHeaders)).isFalse();
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid non-numeric timestamp")
    void shouldRejectInvalidTimestampFormat() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/profile");
        request.addHeader(InternalAuthenticationFilter.HEADER_TIMESTAMP, "invalid-timestamp");
        request.addHeader(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, "some-sig");

        assertThatThrownBy(() -> validator.authenticate(request))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessageContaining("Invalid timestamp format");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on stale timestamp beyond clock skew window")
    void shouldRejectStaleRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/profile");
        long staleTime = System.currentTimeMillis() - (CLOCK_SKEW_SECONDS + 10) * 1000L;
        request.addHeader(InternalAuthenticationFilter.HEADER_TIMESTAMP, String.valueOf(staleTime));
        request.addHeader(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, "some-sig");

        assertThatThrownBy(() -> validator.authenticate(request))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessageContaining("Stale request timestamp");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid HMAC signature")
    void shouldRejectInvalidSignature() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/profile");
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader(InternalAuthenticationFilter.HEADER_TIMESTAMP, timestamp);
        request.addHeader(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, "forged-or-invalid-signature");

        assertThatThrownBy(() -> validator.authenticate(request))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessageContaining("Invalid request signature");
    }

    @Test
    @DisplayName("Should successfully authenticate request with valid HMAC signature and return Authentication token")
    void shouldAuthenticateValidRequest() throws Exception {
        String method    = "GET";
        String path      = "/api/v1/users/profile";
        String userId    = "usr-42";
        String role      = "STUDENT";
        String email     = "student@codeconnect.dev";
        String timestamp = String.valueOf(System.currentTimeMillis());

        String signature = calculateHmac(method, path, userId, role, email, timestamp, TEST_SECRET);

        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.addHeader(InternalAuthenticationFilter.HEADER_USER_ID,            userId);
        request.addHeader(InternalAuthenticationFilter.HEADER_USER_ROLE,          role);
        request.addHeader(InternalAuthenticationFilter.HEADER_USER_EMAIL,         email);
        request.addHeader(InternalAuthenticationFilter.HEADER_TIMESTAMP,          timestamp);
        request.addHeader(InternalAuthenticationFilter.HEADER_INTERNAL_SIGNATURE, signature);

        Authentication auth = validator.authenticate(request);

        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("usr-42");
        assertThat(auth.getCredentials()).isEqualTo("student@codeconnect.dev");
        assertThat(auth.getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_STUDENT");
    }

    private String calculateHmac(String method, String path, String userId, String role, String email, String timestamp, String secret) throws Exception {
        String payload = method.toUpperCase() + ":" + path + ":" + userId + ":" + role + ":" + email + ":" + timestamp;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
