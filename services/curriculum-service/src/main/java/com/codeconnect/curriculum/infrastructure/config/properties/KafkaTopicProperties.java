package com.codeconnect.curriculum.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder for Kafka topic taxonomy in curriculum-service.
 * All topic names are externalized to {@code application.yml} — zero hardcoded strings.
 */
@ConfigurationProperties(prefix = "codeconnect.kafka")
public record KafkaTopicProperties(
    String env,
    Topics topics
) {
    public record Topics(
        String trackCreated,
        String lessonCreated
    ) {}
}
