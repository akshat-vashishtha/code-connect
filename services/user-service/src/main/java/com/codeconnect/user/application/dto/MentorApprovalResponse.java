package com.codeconnect.user.application.dto;

import com.codeconnect.user.domain.enums.MentorApprovalStatus;

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
    MentorApprovalStatus status,
    Instant submittedAt,
    Instant reviewedAt,
    String reviewedBy
) {}
