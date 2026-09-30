package com.codeconnect.sandbox.domain.event;

import java.time.Instant;

/**
 * Domain payload for student code execution request events consumed from Kafka.
 */
public record CodeExecutionRequestedPayload(
    String submissionId,
    String studentId,
    String studentEmail,
    String footholdId,
    String code,
    String language,
    Instant submittedAt
) {}
