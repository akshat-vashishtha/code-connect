package com.codeconnect.sandbox.infrastructure.config;

import com.codeconnect.sandbox.infrastructure.config.properties.KafkaTopicProperties;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * Production-grade Kafka consumer configuration for sandbox-runner-service.
 * Provides DefaultErrorHandler with exponential backoff and DLQ routing to .dlq.v1 topics.
 */
@Configuration
public class KafkaConsumerConfig {

    private final KafkaTopicProperties topicProperties;

    public KafkaConsumerConfig(KafkaTopicProperties topicProperties) {
        this.topicProperties = topicProperties;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, ex) -> {
                String dlqTopic = topicProperties.topics().submissionDlq();
                return new TopicPartition(dlqTopic != null ? dlqTopic : "codeconnect.submission.code-execution.dlq.v1", record.partition());
            });

        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxAttempts(3);
        backOff.setMaxInterval(4000L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
