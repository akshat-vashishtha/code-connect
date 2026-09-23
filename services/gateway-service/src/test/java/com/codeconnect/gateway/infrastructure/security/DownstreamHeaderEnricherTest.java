package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockWebSession;

import static org.assertj.core.api.Assertions.assertThat;

class DownstreamHeaderEnricherTest {

    private final DownstreamHeaderEnricher enricher = new DownstreamHeaderEnricher();

    @Test
    @DisplayName("Should enrich request with X-User-* headers when session attributes are present")
    void shouldEnrichAllHeadersWhenSessionAttributesPresent() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/mentor/analytics").build();
        MockWebSession session = new MockWebSession();
        session.getAttributes().put(SessionManager.ATTR_USER_ID, "user-123");
        session.getAttributes().put(SessionManager.ATTR_USER_EMAIL, "mentor@test.com");

        ServerHttpRequest enriched = enricher.enrich(request, session, "ROLE_MENTOR");

        assertThat(enriched.getHeaders().getFirst("X-User-Id")).isEqualTo("user-123");
        assertThat(enriched.getHeaders().getFirst("X-User-Role")).isEqualTo("ROLE_MENTOR");
        assertThat(enriched.getHeaders().getFirst("X-User-Email")).isEqualTo("mentor@test.com");
    }

    @Test
    @DisplayName("Should default to empty string headers when session attributes are missing")
    void shouldSetEmptyStringHeadersWhenAttributesMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/test").build();
        MockWebSession session = new MockWebSession();

        ServerHttpRequest enriched = enricher.enrich(request, session, null);

        assertThat(enriched.getHeaders().getFirst("X-User-Id")).isEqualTo("");
        assertThat(enriched.getHeaders().getFirst("X-User-Role")).isEqualTo("");
        assertThat(enriched.getHeaders().getFirst("X-User-Email")).isEqualTo("");
    }
}
