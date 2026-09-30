package com.codeconnect.submission.domain.enums;

/**
 * Lifecycle state of a Transactional Outbox event entry.
 */
public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
