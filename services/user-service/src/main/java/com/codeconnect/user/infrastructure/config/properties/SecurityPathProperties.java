package com.codeconnect.user.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Immutable configuration properties for HTTP security path rules.
 * Zero logic; pure data holder bound to 'codeconnect.security.paths' properties.
 * All path patterns are environment-tunable without any code redeployment.
 */
@ConfigurationProperties(prefix = "codeconnect.security.paths")
public record SecurityPathProperties(
    String internalUsers,
    String adminBase,
    String actuatorHealth,
    String actuatorInfo
) {}
