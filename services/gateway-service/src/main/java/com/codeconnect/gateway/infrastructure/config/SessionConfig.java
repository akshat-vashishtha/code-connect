package com.codeconnect.gateway.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

/**
 * Reactive session and cryptographic infrastructure configuration.
 * Configures cookie session ID resolution with secure defaults and BCrypt password encoder.
 */
@Configuration
@EnableConfigurationProperties(SessionProperties.class)
public class SessionConfig {

    private final SessionProperties sessionProperties;

    public SessionConfig(SessionProperties sessionProperties) {
        this.sessionProperties = sessionProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
