package com.codeconnect.collab.infrastructure.config;

import com.codeconnect.collab.infrastructure.config.properties.ActiveMqStompProperties;
import com.codeconnect.collab.infrastructure.config.properties.CollabProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket STOMP message broker configuration.
 * Supports both external ActiveMQ Artemis STOMP Broker Relay and fallback In-Memory SimpleBroker.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CollabProperties properties;
    private final ActiveMqStompProperties activeMqProperties;

    public WebSocketConfig(CollabProperties properties, ActiveMqStompProperties activeMqProperties) {
        this.properties = properties;
        this.activeMqProperties = activeMqProperties;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes(properties.appPrefix());

        if (activeMqProperties != null && activeMqProperties.enabled()) {
            config.enableStompBrokerRelay(activeMqProperties.topicPrefix(), activeMqProperties.queuePrefix())
                    .setRelayHost(activeMqProperties.host())
                    .setRelayPort(activeMqProperties.port())
                    .setClientLogin(activeMqProperties.clientLogin())
                    .setClientPasscode(activeMqProperties.clientPasscode())
                    .setSystemLogin(activeMqProperties.systemLogin())
                    .setSystemPasscode(activeMqProperties.systemPasscode())
                    .setSystemHeartbeatSendInterval(activeMqProperties.systemHeartbeatSendIntervalMs())
                    .setSystemHeartbeatReceiveInterval(activeMqProperties.systemHeartbeatReceiveIntervalMs())
                    .setUserDestinationBroadcast(activeMqProperties.userDestinationBroadcast())
                    .setUserRegistryBroadcast(activeMqProperties.userRegistryBroadcast())
                    .setAutoStartup(activeMqProperties.autoStartup());
        } else {
            config.enableSimpleBroker(properties.topicPrefix());
        }
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String allowedOrigins = properties.allowedOriginPatterns() != null ? properties.allowedOriginPatterns() : "*";
        String[] origins = allowedOrigins.split(",");

        registry.addEndpoint(properties.stompEndpoint())
                .setAllowedOriginPatterns(origins)
                .withSockJS();

        registry.addEndpoint(properties.stompEndpoint())
                .setAllowedOriginPatterns(origins);
    }
}
