package com.codeconnect.user.application.dto.response;

import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;

import java.time.Instant;

/**
 * Immutable response projection representing public user account state.
 */
public record UserResponse(
    String id,
    String email,
    String displayName,
    UserRole role,
    UserStatus status,
    Instant createdAt
) {}
