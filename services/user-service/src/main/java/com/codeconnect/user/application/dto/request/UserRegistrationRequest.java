package com.codeconnect.user.application.dto.request;

import com.codeconnect.user.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Immutable command record for registering a student or applying as a mentor.
 */
public record UserRegistrationRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 50, message = "Display name must be between 2 and 50 characters")
    String displayName,

    @NotNull(message = "Role is required")
    UserRole role,

    String linkedInUrl,
    String bio
) {}
