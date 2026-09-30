package com.codeconnect.submission.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for submission-service.
 * Zero logic or defaults in Java code; all defaults configured via application.yml.
 */
@ConfigurationProperties(prefix = "codeconnect.submission")
public record SubmissionProperties(
    String hmacSecret,
    long maxCodeSizeBytes
) {}
