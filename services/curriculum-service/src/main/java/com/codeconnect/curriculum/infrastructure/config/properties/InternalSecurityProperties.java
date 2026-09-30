package com.codeconnect.curriculum.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for internal zero-trust microservice security in curriculum-service.
 * Pure data holder record — zero logic.
 */
@ConfigurationProperties(prefix = "codeconnect.security")
public record InternalSecurityProperties(
    String internalSecret,
    long maxClockSkewSeconds
) {}

