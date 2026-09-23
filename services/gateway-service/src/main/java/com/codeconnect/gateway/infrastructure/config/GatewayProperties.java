package com.codeconnect.gateway.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for Gateway Service.
 * Pure data holder — zero logic. Defaults are defined in application.yml.
 */
@ConfigurationProperties(prefix = "codeconnect.gateway")
public record GatewayProperties(
    String userServiceUri,
    String curriculumServiceUri,
    String submissionServiceUri,
    String collabServiceUri
) {}
