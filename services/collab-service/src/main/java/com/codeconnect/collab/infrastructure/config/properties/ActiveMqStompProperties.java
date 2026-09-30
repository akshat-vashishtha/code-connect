package com.codeconnect.collab.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for ActiveMQ Artemis STOMP Broker Relay.
 * Zero logic or defaults in Java code; all defaults configured in application.yml.
 */
@ConfigurationProperties(prefix = "codeconnect.activemq.stomp")
public record ActiveMqStompProperties(
    boolean enabled,
    String host,
    int port,
    String systemLogin,
    String systemPasscode,
    String clientLogin,
    String clientPasscode,
    String topicPrefix,
    String queuePrefix,
    long systemHeartbeatSendIntervalMs,
    long systemHeartbeatReceiveIntervalMs,
    String userDestinationBroadcast,
    String userRegistryBroadcast,
    boolean autoStartup
) {}
