package com.codeconnect.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Immutable configuration properties for internal microservice zero-trust security.
 * Zero logic; pure data holder bound to 'codeconnect.security' properties.
 */
@ConfigurationProperties(prefix = "codeconnect.security")
public record InternalSecurityProperties(
    String internalSecret,
    long maxClockSkewSeconds
) {}

