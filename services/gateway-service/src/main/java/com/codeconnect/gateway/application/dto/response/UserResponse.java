package com.codeconnect.gateway.application.dto.response;

import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.enums.UserStatus;

import java.time.Instant;

/**
 * Sanitized user representation returned to presentation callers.
 * Strictly never exposes password hashes.
 */
public record UserResponse(
    String id,
    String email,
    String displayName,
    UserRole role,
    UserStatus status,
    Instant createdAt
) {}
