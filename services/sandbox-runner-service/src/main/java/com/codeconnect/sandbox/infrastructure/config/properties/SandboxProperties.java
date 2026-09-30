package com.codeconnect.sandbox.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for sandbox-runner-service.
 * Zero logic or defaults in Java code; all defaults configured via application.yml.
 */
@ConfigurationProperties(prefix = "codeconnect.sandbox")
public record SandboxProperties(
    String submissionTopic,
    String resultsTopic,
    long timeoutMs,
    int maxMemoryMb
) {}
