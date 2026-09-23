package com.codeconnect.gateway.application.dto.request;

import com.codeconnect.gateway.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Inbound registration command DTO.
 * Modeled as an immutable Java 21 record.
 */
public record SignupRequest(
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
