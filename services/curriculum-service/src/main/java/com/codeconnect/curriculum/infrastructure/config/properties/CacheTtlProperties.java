package com.codeconnect.curriculum.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for cache TTL durations.
 */
@ConfigurationProperties(prefix = "codeconnect.cache.ttl")
public record CacheTtlProperties(
    long defaultTtlMs,
    long tracksTtlMs,
    long modulesTtlMs,
    long lessonsTtlMs,
    long progressTtlMs
) {}
