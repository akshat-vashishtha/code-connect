package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.config.properties.GatewayProperties;
import com.codeconnect.gateway.infrastructure.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Enriches downstream microservice requests with verified session identities and HMAC signatures.
 * Strictly encapsulates downstream HTTP header mutation and anti-spoofing cryptography.
 */
@Component
@RequiredArgsConstructor
public class DownstreamHeaderEnricher {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_INTERNAL_SIGNATURE = "X-Internal-Signature";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_SPAN_ID = "X-Span-Id";

    private final GatewayProperties gatewayProperties;

    public ServerHttpRequest enrich(ServerHttpRequest request, WebSession session, String role) {
        String userId = Objects.requireNonNullElse(session.getAttribute(SessionManager.ATTR_USER_ID), "");
        String email = Objects.requireNonNullElse(session.getAttribute(SessionManager.ATTR_USER_EMAIL), "");
        String safeRole = Objects.requireNonNullElse(role, "");
        String timestamp = String.valueOf(System.currentTimeMillis());

        HmacPayload payload = new HmacPayload(
            resolveMethod(request),
            request.getPath().value(),
            userId,
            safeRole,
            email,
            timestamp
        );
        String signature = calculateHmac(payload, gatewayProperties.internalSecret());

        return request.mutate()
            .header(HEADER_USER_ID, userId)
            .header(HEADER_USER_ROLE, safeRole)
            .header(HEADER_USER_EMAIL, email)
            .header(HEADER_TIMESTAMP, timestamp)
            .header(HEADER_INTERNAL_SIGNATURE, signature)
            .header(HEADER_CORRELATION_ID, resolveCorrelationId(request))
            .header(HEADER_SPAN_ID, generateSpanId())
            .build();
    }

    private String resolveMethod(ServerHttpRequest request) {
        return Optional.ofNullable(request.getMethod())
            .map(HttpMethod::name)
            .orElse("GET")
            .toUpperCase();
    }

    private String resolveCorrelationId(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(HEADER_CORRELATION_ID))
            .filter(id -> !id.isBlank())
            .orElseGet(() -> "req-" + UUID.randomUUID().toString().substring(0, 8));
    }

    private String generateSpanId() {
        return "span-gw-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String calculateHmac(HmacPayload payload, String secret) {
        try {
            String safeSecret = Objects.requireNonNull(secret, "HMAC secret must not be null");
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(safeSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(payload.toRawData().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC signature for downstream security", e);
        }
    }

    /**
     * Immutable context payload for computing downstream HMAC signatures.
     */
    public record HmacPayload(
        String method,
        String path,
        String userId,
        String role,
        String email,
        String timestamp
    ) {
        public String toRawData() {
            return String.join(":",
                Objects.requireNonNullElse(method, "GET").toUpperCase(),
                Objects.requireNonNullElse(path, ""),
                Objects.requireNonNullElse(userId, ""),
                Objects.requireNonNullElse(role, ""),
                Objects.requireNonNullElse(email, ""),
                Objects.requireNonNullElse(timestamp, "")
            );
        }
    }
}
