package com.codeconnect.submission.domain.event;

import java.time.Instant;

/**
 * Standardized metadata header for all Kafka events in CodeConnect.
 * Carries distributed tracing, governance, and audit metadata.
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
