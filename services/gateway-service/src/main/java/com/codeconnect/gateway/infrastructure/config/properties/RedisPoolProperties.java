package com.codeconnect.gateway.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for Edge Gateway Redis connection pooling,
 * socket timeouts, and low-latency TCP options.
 */
@ConfigurationProperties(prefix = "codeconnect.redis")
public record RedisPoolProperties(
    int minIdle,
    int maxIdle,
    int maxActive,
    long maxWaitMs,
    long connectTimeoutMs,
    long commandTimeoutMs,
    boolean keepAlive,
    boolean tcpNoDelay
) {}
