package com.codeconnect.collab.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * Production-grade Kafka consumer configuration for collab-service.
 * Configures DefaultErrorHandler with exponential backoff.
 */
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler() {
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxAttempts(3);
        backOff.setMaxInterval(4000L);

        return new DefaultErrorHandler(backOff);
    }
}
