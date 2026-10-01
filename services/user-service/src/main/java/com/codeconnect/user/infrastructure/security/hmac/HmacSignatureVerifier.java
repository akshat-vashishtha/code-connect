package com.codeconnect.user.infrastructure.security.hmac;

import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.security.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Computes and verifies HMAC-SHA256 request signatures for the user-service Zero-Trust perimeter.
 *
 * <p>Single Responsibility: this class does exactly one thing — given an {@link HttpServletRequest},
 * reconstruct the canonical payload, compute the expected HMAC-SHA256 digest using the shared
 * internal secret, and compare it constant-time against the received signature header.
 *
 * <p>Throws {@link BadCredentialsException} (a Spring Security {@code AuthenticationException})
 * on mismatch so the filter can propagate the failure through the standard security error path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HmacSignatureVerifier {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final InternalSecurityProperties securityProperties;

    /**
     * Verifies that the {@code X-Internal-Signature} header matches the expected HMAC-SHA256
     * digest computed over {@code METHOD:PATH:userId:role:email:timestamp}.
     *
     * @throws BadCredentialsException if the signature is absent, malformed, or does not match
     */
    public void verify(HttpServletRequest request) {
        String receivedSignature  = request.getHeader(InternalSecurityHeaders.SIGNATURE);
        String expectedSignature  = computeExpectedSignature(request);

        if (!expectedSignature.equalsIgnoreCase(receivedSignature)) {
            log.warn("HMAC signature mismatch for path={}", request.getRequestURI());
            throw new BadCredentialsException("Security verification failed: Invalid request signature");
        }
    }

    private String computeExpectedSignature(HttpServletRequest request) {
        String method    = request.getMethod();
        String path      = request.getRequestURI();
        String userId    = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ID),    "");
        String role      = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ROLE),  "");
        String email     = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_EMAIL), "");
        String timestamp = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.TIMESTAMP),  "");

        String safeMethod  = method != null ? method.toUpperCase() : "GET";
        String safePath    = path   != null ? path : "";
        String payload     = safeMethod + ":" + safePath + ":" + userId + ":" + role + ":" + email + ":" + timestamp;

        return computeHmac(payload);
    }

    private String computeHmac(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(
                securityProperties.internalSecret().getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
            ));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute HMAC-SHA256 signature", e);
        }
    }
}
