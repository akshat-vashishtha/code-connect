package com.codeconnect.curriculum.domain.event;

import java.time.Instant;

/**
 * Domain event dispatched when a new curriculum lesson is created within a module.
 * Enables Event-Driven Architecture (EDA) for CQRS read-model projections and cache synchronization.
 */
public record LessonCreatedEvent(
    String lessonId,
    String moduleId,
    String title,
    String slug,
    Integer sequence,
    Instant createdAt
) {
}
