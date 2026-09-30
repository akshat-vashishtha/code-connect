package com.codeconnect.collab.domain.event;

import java.time.Instant;

/**
 * Standardized metadata header for all Kafka events in CodeConnect.
 */
public record EventHeader(
    String eventId,
    String eventType,
    String correlationId,
    String sourceService,
    String schemaVersion,
    Instant timestamp,
    String environment
) {}
