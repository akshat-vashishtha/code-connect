package com.codeconnect.gateway.infrastructure.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for gateway session settings.
 * All default values reside in application.yml via ${ENV:default} syntax.
 * Never write business or fallback logic in this class.
 */
@ConfigurationProperties(prefix = "codeconnect.session")
public record SessionProperties(
    String cookieName,
    boolean cookieSecure,
    String cookieSameSite,
    Duration cookieMaxAge
) {}
