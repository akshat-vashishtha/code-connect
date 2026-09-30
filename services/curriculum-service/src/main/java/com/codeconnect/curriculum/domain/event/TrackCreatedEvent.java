package com.codeconnect.curriculum.domain.event;

import com.codeconnect.curriculum.domain.enums.TrackStatus;

import java.time.Instant;

/**
 * Domain event dispatched when a new curriculum track is created.
 * Enables Event-Driven Architecture (EDA) for CQRS read-model projections,
 * search indexing, and student notifications.
 */
public record TrackCreatedEvent(
    String trackId,
    String title,
    String slug,
    String description,
    Integer estimatedHours,
    TrackStatus status,
    Instant createdAt
) {
}
