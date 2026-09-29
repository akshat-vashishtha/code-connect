package com.codeconnect.curriculum.infrastructure.security;

import com.codeconnect.curriculum.infrastructure.config.InternalSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Dedicated collaborator validating HMAC-SHA256 zero-trust security headers
 * and establishing Spring SecurityContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSecurityValidator {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_INTERNAL_SIGNATURE = "X-Internal-Signature";

    private final InternalSecurityProperties securityProperties;

    public void validateAndAuthenticate(HttpServletRequest request) {
        String timestampStr = request.getHeader(HEADER_TIMESTAMP);
        String signature = request.getHeader(HEADER_INTERNAL_SIGNATURE);

        if (timestampStr == null || signature == null) {
            return;
        }

        if (!isValidTimestamp(timestampStr, request.getRequestURI())) {
            SecurityContextHolder.clearContext();
            return;
        }

        verifySignatureAndAuthenticate(request, timestampStr, signature);
    }

    private boolean isValidTimestamp(String timestampStr, String requestUri) {
        try {
            long timestamp = Long.parseLong(timestampStr);
            if (isClockSkewExceeded(timestamp)) {
                log.warn("Internal security verification failed: Stale timestamp for path {}", requestUri);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            log.warn("Internal security processing error on path {}: {}", requestUri, e.getMessage());
            return false;
        }
    }

    private void verifySignatureAndAuthenticate(HttpServletRequest request, String timestampStr, String signature) {
        String userId = Objects.requireNonNullElse(request.getHeader(HEADER_USER_ID), "");
        String role = Objects.requireNonNullElse(request.getHeader(HEADER_USER_ROLE), "");
        String email = Objects.requireNonNullElse(request.getHeader(HEADER_USER_EMAIL), "");

        String expectedSignature = calculateHmac(userId, role, email, timestampStr, securityProperties.internalSecret());
        if (expectedSignature.equalsIgnoreCase(signature)) {
            establishSecurityContext(userId, role, email);
            log.debug("Internal zero-trust authentication established for userId={}, role={}", userId, role);
        } else {
            log.warn("Internal security verification failed: HMAC signature mismatch for path {}", request.getRequestURI());
            SecurityContextHolder.clearContext();
        }
    }

    private boolean isClockSkewExceeded(long timestamp) {
        long current = System.currentTimeMillis();
        long maxSkewMs = securityProperties.maxClockSkewSeconds() * 1000L;
        return Math.abs(current - timestamp) > maxSkewMs;
    }

    private void establishSecurityContext(String userId, String role, String email) {
        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        if (!role.isBlank()) {
            String roleAuthority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities = List.of(new SimpleGrantedAuthority(roleAuthority));
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userId.isBlank() ? "anonymous" : userId,
            null,
            authorities
        );
        auth.setDetails(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public String calculateHmac(String userId, String role, String email, String timestamp, String secret) {
        try {
            String payload = userId + ":" + role + ":" + email + ":" + timestamp;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC signature", e);
        }
    }
}
