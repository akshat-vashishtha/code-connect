package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.infrastructure.config.InternalSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Dedicated collaborator component verifying zero-trust HMAC signatures and anti-replay timestamps for user-service.
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

    public boolean validateAndAuthenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String timestampStr = request.getHeader(HEADER_TIMESTAMP);
        String signature = request.getHeader(HEADER_INTERNAL_SIGNATURE);

        if (timestampStr == null || signature == null) {
            return true;
        }

        if (!isValidTimestamp(timestampStr, request.getRequestURI(), response)) {
            return false;
        }

        return verifySignatureAndAuthenticate(request, timestampStr, signature, response);
    }

    private boolean isValidTimestamp(String timestampStr, String path, HttpServletResponse response) throws IOException {
        try {
            long timestamp = Long.parseLong(timestampStr);
            if (isClockSkewExceeded(timestamp)) {
                log.warn("Internal security verification failed: Clock skew exceeded for path {}", path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security verification failed: Stale request timestamp");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            log.warn("Invalid timestamp header format for path {}", path);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security verification failed: Invalid timestamp format");
            return false;
        }
    }

    private boolean verifySignatureAndAuthenticate(
            HttpServletRequest request, String timestampStr, String signature, HttpServletResponse response
    ) throws IOException {
        String userId = Objects.requireNonNullElse(request.getHeader(HEADER_USER_ID), "");
        String role = Objects.requireNonNullElse(request.getHeader(HEADER_USER_ROLE), "");
        String email = Objects.requireNonNullElse(request.getHeader(HEADER_USER_EMAIL), "");

        String expectedSignature = calculateHmac(userId, role, email, timestampStr, securityProperties.internalSecret());
        if (!expectedSignature.equalsIgnoreCase(signature)) {
            log.warn("Internal security verification failed: HMAC signature mismatch for path {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Security verification failed: Invalid request signature");
            return false;
        }

        establishSecurityContext(userId, role, email);
        return true;
    }

    private boolean isClockSkewExceeded(long timestamp) {
        long currentTimestamp = System.currentTimeMillis();
        long maxSkewMs = securityProperties.maxClockSkewSeconds() * 1000L;
        return Math.abs(currentTimestamp - timestamp) > maxSkewMs;
    }

    private void establishSecurityContext(String userId, String role, String email) {
        List<SimpleGrantedAuthority> authorities = role.isBlank()
            ? Collections.emptyList()
            : List.of(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userId, email, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
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
