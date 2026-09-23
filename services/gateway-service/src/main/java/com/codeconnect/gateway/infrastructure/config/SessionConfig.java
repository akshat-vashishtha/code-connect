package com.codeconnect.gateway.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

/**
 * Reactive session infrastructure configuration.
 * Configures cookie session ID resolution with secure defaults.
 */
@Configuration
@EnableConfigurationProperties(SessionProperties.class)
public class SessionConfig {

    private final SessionProperties sessionProperties;

    public SessionConfig(SessionProperties sessionProperties) {
        this.sessionProperties = sessionProperties;
    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName(sessionProperties.cookieName());
        resolver.addCookieInitializer(builder -> builder
            .path("/")
            .httpOnly(true)
            .secure(sessionProperties.cookieSecure())
            .sameSite(sessionProperties.cookieSameSite())
        );
        return resolver;
    }
}
