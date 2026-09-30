package com.codeconnect.user.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder for Kafka topic taxonomy in user-service.
 * All topic names are externalized to {@code application.yml} — zero hardcoded values.
 */
@ConfigurationProperties(prefix = "codeconnect.kafka")
public record KafkaTopicProperties(
    String env,
    Topics topics
) {
    public record Topics(
        String mentorApplicationApproved,
        String mentorApplicationRejected,
        String userRegistered
    ) {}
}
