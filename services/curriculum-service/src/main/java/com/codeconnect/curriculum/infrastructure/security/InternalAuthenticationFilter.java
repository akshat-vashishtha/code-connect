package com.codeconnect.curriculum.infrastructure.security;

import com.codeconnect.curriculum.infrastructure.config.InternalSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;

/**
 * Intercepts incoming HTTP requests, validates HMAC-SHA256 signatures,
 * checks clock-skew staleness, and populates Spring Security SecurityContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_INTERNAL_SIGNATURE = "X-Internal-Signature";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_SPAN_ID = "X-Span-Id";

    private final InternalSecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String incomingCorrelationId = request.getHeader(HEADER_CORRELATION_ID);
        String correlationId = (incomingCorrelationId != null && !incomingCorrelationId.isBlank())
            ? incomingCorrelationId
            : "req-" + java.util.UUID.randomUUID().toString().substring(0, 8);

        String spanId = "span-curr-" + java.util.UUID.randomUUID().toString().substring(0, 8);

        org.slf4j.MDC.put("traceId", correlationId);
        org.slf4j.MDC.put("spanId", spanId);

        response.setHeader(HEADER_CORRELATION_ID, correlationId);
        response.setHeader(HEADER_SPAN_ID, spanId);

        try {
            String userId = request.getHeader(HEADER_USER_ID);
            String role = request.getHeader(HEADER_USER_ROLE);
            String email = request.getHeader(HEADER_USER_EMAIL);
            String timestampStr = request.getHeader(HEADER_TIMESTAMP);
            String signature = request.getHeader(HEADER_INTERNAL_SIGNATURE);

            if (signature != null && timestampStr != null) {
                try {
                    long timestamp = Long.parseLong(timestampStr);
                    long current = System.currentTimeMillis();
                    long maxSkewMs = securityProperties.maxClockSkewSeconds() * 1000;

                    if (Math.abs(current - timestamp) > maxSkewMs) {
                        log.warn("Internal security verification failed: Stale timestamp delta {} ms", (current - timestamp));
                        SecurityContextHolder.clearContext();
                    } else {
                        String safeUserId = userId != null ? userId : "";
                        String safeRole = role != null ? role : "";
                        String safeEmail = email != null ? email : "";

                        String expectedSignature = calculateHmac(safeUserId, safeRole, safeEmail, timestampStr, securityProperties.internalSecret());

                        if (expectedSignature.equalsIgnoreCase(signature)) {
                            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                            if (!safeRole.isBlank()) {
                                String roleAuthority = safeRole.startsWith("ROLE_") ? safeRole : "ROLE_" + safeRole;
                                authorities = List.of(new SimpleGrantedAuthority(roleAuthority));
                            }

                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                safeUserId.isBlank() ? "anonymous" : safeUserId,
                                null,
                                authorities
                            );
                            auth.setDetails(safeEmail);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.debug("Internal zero-trust authentication established for userId={}, role={}", safeUserId, safeRole);
                        } else {
                            log.warn("Internal security verification failed: HMAC signature mismatch for path {}", request.getRequestURI());
                            SecurityContextHolder.clearContext();
                        }
                    }
                } catch (Exception e) {
                    log.error("Internal security processing error on path {}: {}", request.getRequestURI(), e.getMessage());
                    SecurityContextHolder.clearContext();
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            org.slf4j.MDC.remove("traceId");
            org.slf4j.MDC.remove("spanId");
        }
    }

    private static String calculateHmac(String userId, String role, String email, String timestamp, String secret) {
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
