package com.codeconnect.user.infrastructure.security.chain.handler;

import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.security.chain.AbstractSecurityValidationHandler;
import com.codeconnect.user.infrastructure.security.chain.exception.SecurityValidationException;
import com.codeconnect.user.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Objects;

/**
 * CoR Handler: verifies the HMAC-SHA256 signature against the canonical payload
 * derived from the request headers. Throws SecurityValidationException on mismatch.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class SignatureValidationHandler extends AbstractSecurityValidationHandler {

    private final InternalSecurityProperties securityProperties;

    @Override
    public void validate(HttpServletRequest request) {
        String signature = request.getHeader(InternalSecurityHeaders.SIGNATURE);
        String expected  = computeExpectedSignature(request);

        if (!expected.equalsIgnoreCase(signature)) {
            log.warn("Security validation failed: HMAC signature mismatch for path={}", request.getRequestURI());
            throw new SecurityValidationException("Security verification failed: Invalid request signature");
        }
        passToNext(request);
    }

    private String computeExpectedSignature(HttpServletRequest request) {
        String method    = request.getMethod();
        String path      = request.getRequestURI();
        String userId    = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ID),    "");
        String role      = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ROLE),  "");
        String email     = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_EMAIL), "");
        String timestamp = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.TIMESTAMP),  "");
        return computeHmac(method, path, userId, role, email, timestamp);
    }

    private String computeHmac(String method, String path, String userId, String role, String email, String timestamp) {
        try {
            String safeMethod = method != null ? method.toUpperCase() : "GET";
            String safePath = path != null ? path : "";
            String payload = safeMethod + ":" + safePath + ":" + userId + ":" + role + ":" + email + ":" + timestamp;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(securityProperties.internalSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute HMAC signature", e);
        }
    }
}
