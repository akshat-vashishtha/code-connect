package com.codeconnect.collab;

import com.codeconnect.collab.infrastructure.config.properties.CollabProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot Application for CodeConnect Collaboration & WebSocket Service.
 */
@SpringBootApplication
@EnableConfigurationProperties(CollabProperties.class)
public class CollabServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollabServiceApplication.class, args);
    }
}
