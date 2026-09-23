package com.codeconnect.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for User Service.
 * Pure data holder — zero logic. Defaults are defined in application.yml.
 */
@ConfigurationProperties(prefix = "codeconnect.user")
public record UserProperties(
    String jwtSecret,
    long sessionTimeoutSeconds,
    String errorBaseUri
) {}
