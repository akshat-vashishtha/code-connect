package com.codeconnect.user.domain.event;

import java.time.Instant;

/**
 * Domain event dispatched when a mentor application is approved by an administrator.
 * Enables Event-Driven Architecture (EDA) for notification, onboarding, and audit workflows.
 */
public record MentorApprovedEvent(
    String applicationId,
    String userId,
    String email,
    String reviewedBy,
    Instant approvedAt
) {
}
