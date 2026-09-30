package com.codeconnect.user.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for cache TTL durations in user-service.
 */
@ConfigurationProperties(prefix = "codeconnect.cache.ttl")
public record CacheTtlProperties(
    long defaultTtlMs,
    long usersTtlMs,
    long mentorsTtlMs
) {}
