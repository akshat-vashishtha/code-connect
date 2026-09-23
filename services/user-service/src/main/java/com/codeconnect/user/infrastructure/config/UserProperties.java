package com.codeconnect.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for User Service.
 * Follows 15-factor app principle (externalized config) and coding standards.
 */
@ConfigurationProperties(prefix = "codeconnect.user")
public record UserProperties(
    String jwtSecret,
    long sessionTimeoutSeconds
) {
    public UserProperties {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = "codeconnect-super-secure-default-secret-key-32bytes";
        }
        if (sessionTimeoutSeconds <= 0) {
            sessionTimeoutSeconds = 86400; // 24 hours
        }
    }
}
