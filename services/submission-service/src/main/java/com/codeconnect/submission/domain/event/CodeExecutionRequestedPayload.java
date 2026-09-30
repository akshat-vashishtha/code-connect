package com.codeconnect.submission.domain.event;

import java.time.Instant;

/**
 * Domain payload for student code execution request events.
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
