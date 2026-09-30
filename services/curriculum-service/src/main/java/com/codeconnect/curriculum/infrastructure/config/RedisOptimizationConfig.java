package com.codeconnect.curriculum.infrastructure.config;

import com.codeconnect.curriculum.infrastructure.config.properties.RedisPoolProperties;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Customizer applying production-grade socket options, fail-fast command timeouts,
 * and automatic topology reconnection to the Lettuce Redis client in curriculum-service.
 */
@Configuration
public class RedisOptimizationConfig {

    private final RedisPoolProperties props;

    public RedisOptimizationConfig(RedisPoolProperties props) {
        this.props = props;
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientCustomizer() {
        return builder -> {
            SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(props.connectTimeoutMs()))
                .keepAlive(props.keepAlive())
                .tcpNoDelay(props.tcpNoDelay())
                .build();

            ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(props.commandTimeoutMs())))
                .autoReconnect(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();

            builder.clientOptions(clientOptions);
        };
    }
}
