package com.codeconnect.submission.application.dto.response;

import com.codeconnect.submission.domain.enums.SubmissionStatus;

import java.time.Instant;

public record SubmissionResponse(
    String submissionId,
    String studentId,
    String studentEmail,
    String footholdId,
    SubmissionStatus status,
    String code,
    String language,
    String errorMessage,
    Instant createdAt,
    Instant completedAt
) {}
