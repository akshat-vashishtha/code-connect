package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockWebSession;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeconnect.gateway.infrastructure.config.GatewayProperties;

class DownstreamHeaderEnricherTest {

    private final GatewayProperties gatewayProperties = new GatewayProperties(
        "http://localhost:8081",
        "http://localhost:8082",
        "http://localhost:8083",
        "http://localhost:8084",
        "/api/v1/admin/**",
        "/api/v1/mentor/**",
        "test-internal-secret-key-32bytes-min!"
    );

    private final DownstreamHeaderEnricher enricher = new DownstreamHeaderEnricher(gatewayProperties);

    @Test
    @DisplayName("Should enrich request with X-User-* headers, timestamp, and HMAC signature when session attributes are present")
    void shouldEnrichAllHeadersWhenSessionAttributesPresent() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/mentor/analytics").build();
        MockWebSession session = new MockWebSession();
        session.getAttributes().put(SessionManager.ATTR_USER_ID, "user-123");
        session.getAttributes().put(SessionManager.ATTR_USER_EMAIL, "mentor@test.com");

        ServerHttpRequest enriched = enricher.enrich(request, session, "ROLE_MENTOR");

        assertThat(enriched.getHeaders().getFirst("X-User-Id")).isEqualTo("user-123");
        assertThat(enriched.getHeaders().getFirst("X-User-Role")).isEqualTo("ROLE_MENTOR");
        assertThat(enriched.getHeaders().getFirst("X-User-Email")).isEqualTo("mentor@test.com");
        assertThat(enriched.getHeaders().getFirst("X-Timestamp")).isNotNull();
        assertThat(enriched.getHeaders().getFirst("X-Internal-Signature")).isNotNull();
        assertThat(enriched.getHeaders().getFirst("X-Correlation-ID")).startsWith("req-");
    }

    @Test
    @DisplayName("Should default to empty string headers with valid signature when session attributes are missing")
    void shouldSetEmptyStringHeadersWhenAttributesMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/test").build();
        MockWebSession session = new MockWebSession();

        ServerHttpRequest enriched = enricher.enrich(request, session, null);

        assertThat(enriched.getHeaders().getFirst("X-User-Id")).isEqualTo("");
        assertThat(enriched.getHeaders().getFirst("X-User-Role")).isEqualTo("");
        assertThat(enriched.getHeaders().getFirst("X-User-Email")).isEqualTo("");
        assertThat(enriched.getHeaders().getFirst("X-Timestamp")).isNotNull();
        assertThat(enriched.getHeaders().getFirst("X-Internal-Signature")).isNotNull();
    }
}
