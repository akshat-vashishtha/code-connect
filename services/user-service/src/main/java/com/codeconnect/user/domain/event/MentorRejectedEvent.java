package com.codeconnect.user.domain.event;

import java.time.Instant;

/**
 * Domain event dispatched when a mentor application is rejected by an administrator.
 * Enables downstream Kafka consumers to send rejection notifications and update audit records.
 */
public record MentorRejectedEvent(
    String applicationId,
    String userId,
    String email,
    String reviewedBy,
    Instant rejectedAt
) {
}
