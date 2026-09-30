package com.codeconnect.collab.infrastructure.config;

import com.codeconnect.collab.infrastructure.config.properties.ActiveMqStompProperties;
import com.codeconnect.collab.infrastructure.config.properties.CollabProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    private MessageBrokerRegistry brokerRegistry;

    @Mock
    private StompBrokerRelayRegistration relayRegistration;

    @Mock
    private StompEndpointRegistry endpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration wsEndpointRegistration;

    @Test
    @DisplayName("Should configure ActiveMQ STOMP Broker Relay when activemq is enabled")
    void shouldConfigureActiveMqBrokerRelay() {
        CollabProperties collabProps = new CollabProperties(
            "/ws-connect", "*", "/topic", "/app", "secret"
        );
        ActiveMqStompProperties activeMqProps = new ActiveMqStompProperties(
            true, "activemq-artemis", 61613, "admin", "changeit",
            "admin", "changeit", "/topic", "/queue", 10000L, 10000L,
            "/topic/unresolved", "/topic/registry", true
        );

        when(brokerRegistry.enableStompBrokerRelay(eq("/topic"), eq("/queue"))).thenReturn(relayRegistration);
        when(relayRegistration.setRelayHost(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setRelayPort(anyInt())).thenReturn(relayRegistration);
        when(relayRegistration.setClientLogin(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setClientPasscode(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setSystemLogin(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setSystemPasscode(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setSystemHeartbeatSendInterval(anyLong())).thenReturn(relayRegistration);
        when(relayRegistration.setSystemHeartbeatReceiveInterval(anyLong())).thenReturn(relayRegistration);
        when(relayRegistration.setUserDestinationBroadcast(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setUserRegistryBroadcast(anyString())).thenReturn(relayRegistration);
        when(relayRegistration.setAutoStartup(anyBoolean())).thenReturn(relayRegistration);

        WebSocketConfig config = new WebSocketConfig(collabProps, activeMqProps);
        config.configureMessageBroker(brokerRegistry);

        verify(brokerRegistry).setApplicationDestinationPrefixes("/app");
        verify(brokerRegistry).enableStompBrokerRelay("/topic", "/queue");
        verify(relayRegistration).setRelayHost("activemq-artemis");
        verify(relayRegistration).setRelayPort(61613);
    }

    @Test
    @DisplayName("Should configure simple in-memory broker when activemq is disabled")
    void shouldConfigureSimpleBrokerWhenDisabled() {
        CollabProperties collabProps = new CollabProperties(
            "/ws-connect", "*", "/topic", "/app", "secret"
        );
        ActiveMqStompProperties activeMqProps = new ActiveMqStompProperties(
            false, "localhost", 61613, "admin", "changeit",
            "admin", "changeit", "/topic", "/queue", 10000L, 10000L,
            "/topic/unresolved", "/topic/registry", true
        );

        WebSocketConfig config = new WebSocketConfig(collabProps, activeMqProps);
        config.configureMessageBroker(brokerRegistry);

        verify(brokerRegistry).setApplicationDestinationPrefixes("/app");
        verify(brokerRegistry).enableSimpleBroker("/topic");
    }

    @Test
    @DisplayName("Should register STOMP endpoints with SockJS and native WebSocket")
    void shouldRegisterStompEndpoints() {
        CollabProperties collabProps = new CollabProperties(
            "/ws-connect", "*", "/topic", "/app", "secret"
        );
        ActiveMqStompProperties activeMqProps = new ActiveMqStompProperties(
            false, "localhost", 61613, "admin", "changeit",
            "admin", "changeit", "/topic", "/queue", 10000L, 10000L,
            "/topic/unresolved", "/topic/registry", true
        );

        when(endpointRegistry.addEndpoint("/ws-connect")).thenReturn(wsEndpointRegistration);
        when(wsEndpointRegistration.setAllowedOriginPatterns(any(String[].class))).thenReturn(wsEndpointRegistration);

        WebSocketConfig config = new WebSocketConfig(collabProps, activeMqProps);
        config.registerStompEndpoints(endpointRegistry);

        verify(endpointRegistry, org.mockito.Mockito.times(2)).addEndpoint("/ws-connect");
        verify(wsEndpointRegistration).withSockJS();
    }
}
