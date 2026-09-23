package com.codeconnect.user.application.dto;

import java.time.Instant;

/**
 * Immutable response DTO representing mentor verification audit applications.
 */
public record MentorApprovalResponse(
    String id,
    String userId,
    String email,
    String linkedInUrl,
    String bio,
    String status,
    Instant submittedAt,
    Instant reviewedAt,
    String reviewedBy
) {}
