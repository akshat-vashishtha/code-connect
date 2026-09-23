package com.codeconnect.gateway.infrastructure.config;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.session.WebSessionIdResolver;

import static org.assertj.core.api.Assertions.assertThat;

class SessionConfigTest {

    @Test
    @DisplayName("Should configure cookie resolver with custom name, max-age, path, and security flags")
    void shouldConfigureCookieResolverWithCustomProperties() {
        SessionProperties properties = new SessionProperties(
            "APP_SESSION",
            true,
            "Strict",
            Duration.ofDays(7)
        );

        SessionConfig config = new SessionConfig(properties);
        WebSessionIdResolver resolver = config.webSessionIdResolver();

        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/").build()
        );

        resolver.setSessionId(exchange, "test-session-id-12345");

        ResponseCookie cookie = exchange.getResponse().getCookies().getFirst("APP_SESSION");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("test-session-id-12345");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofDays(7));
    }
}
