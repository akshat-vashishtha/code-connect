package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.config.GatewayProperties;
import com.codeconnect.gateway.infrastructure.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

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
        String userId = session.getAttribute(SessionManager.ATTR_USER_ID);
        String email = session.getAttribute(SessionManager.ATTR_USER_EMAIL);

        String safeUserId = userId != null ? userId : "";
        String safeRole = role != null ? role : "";
        String safeEmail = email != null ? email : "";
        String timestamp = String.valueOf(System.currentTimeMillis());

        String incomingCorrelationId = request.getHeaders().getFirst(HEADER_CORRELATION_ID);
        String correlationId = (incomingCorrelationId != null && !incomingCorrelationId.isBlank())
            ? incomingCorrelationId
            : "req-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        String spanId = "span-gw-" + java.util.UUID.randomUUID().toString().substring(0, 8);

        String signature = calculateHmac(safeUserId, safeRole, safeEmail, timestamp, gatewayProperties.internalSecret());

        return request.mutate()
            .header(HEADER_USER_ID, safeUserId)
            .header(HEADER_USER_ROLE, safeRole)
            .header(HEADER_USER_EMAIL, safeEmail)
            .header(HEADER_TIMESTAMP, timestamp)
            .header(HEADER_INTERNAL_SIGNATURE, signature)
            .header(HEADER_CORRELATION_ID, correlationId)
            .header(HEADER_SPAN_ID, spanId)
            .build();
    }

    public static String calculateHmac(String userId, String role, String email, String timestamp, String secret) {
        try {
            String payload = userId + ":" + role + ":" + email + ":" + timestamp;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC signature for downstream security", e);
        }
    }
}
