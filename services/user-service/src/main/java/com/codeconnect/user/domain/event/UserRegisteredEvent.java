package com.codeconnect.user.domain.event;

import com.codeconnect.user.domain.enums.UserRole;

import java.time.Instant;

/**
 * Domain event dispatched when a new user successfully registers on the platform.
 * Enables downstream Kafka consumers to send welcome emails, create onboarding records, and audit trails.
 */
public record UserRegisteredEvent(
    String userId,
    String email,
    String displayName,
    UserRole role,
    Instant registeredAt
) {
}
