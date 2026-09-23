package com.codeconnect.gateway.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for Gateway Service.
 * Follows the zero-hardcoding standard from coding-standards.md.
 */
@ConfigurationProperties(prefix = "codeconnect.gateway")
public record GatewayProperties(
    String userServiceUri,
    String curriculumServiceUri,
    String submissionServiceUri,
    String collabServiceUri
) {
    public GatewayProperties {
        if (userServiceUri == null) {
            userServiceUri = "http://localhost:8081";
        }
        if (curriculumServiceUri == null) {
            curriculumServiceUri = "http://localhost:8082";
        }
        if (submissionServiceUri == null) {
            submissionServiceUri = "http://localhost:8083";
        }
        if (collabServiceUri == null) {
            collabServiceUri = "http://localhost:8084";
        }
    }
}
