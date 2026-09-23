package com.codeconnect.gateway.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Infrastructure configuration for CodeConnect Gateway Service.
 */
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfig {
}
