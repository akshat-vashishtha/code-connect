package com.codeconnect.collab.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for collab-service.
 */
@ConfigurationProperties(prefix = "codeconnect.collab")
public record CollabProperties(
    String stompEndpoint,
    String allowedOriginPatterns,
    String topicPrefix,
    String appPrefix,
    String internalSecret
) {}
