package com.codeconnect.user.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Pure data holder configuration properties for MongoDB connection pooling,
 * socket timeouts, fail-fast cluster selection, wire compression, and write durability.
 */
@ConfigurationProperties(prefix = "codeconnect.mongodb")
public record MongoPoolProperties(
    int minPoolSize,
    int maxPoolSize,
    long maxWaitTimeMs,
    long maxIdleTimeMs,
    long maxLifeTimeMs,
    long maintenanceFrequencyMs,
    long connectTimeoutMs,
    long readTimeoutMs,
    long serverSelectionTimeoutMs,
    String compressors,
    String writeConcern,
    String readPreference
) {}
