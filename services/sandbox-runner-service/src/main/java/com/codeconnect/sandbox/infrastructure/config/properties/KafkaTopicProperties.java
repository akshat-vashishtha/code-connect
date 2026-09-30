package com.codeconnect.sandbox.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for Kafka topic taxonomy in sandbox-runner-service.
 */
@ConfigurationProperties(prefix = "codeconnect.kafka")
public record KafkaTopicProperties(
    String env,
    Topics topics
) {
    public record Topics(
        String submissionRequested,
        String executionCompleted,
        String submissionDlq,
        String executionDlq
    ) {}
}
