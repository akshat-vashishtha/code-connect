package com.codeconnect.curriculum.application.dto.request;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTrackRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Slug is required")
    String slug,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Estimated hours is required")
    @Positive(message = "Estimated hours must be positive")
    Integer estimatedHours,

    @NotNull(message = "Track status is required")
    TrackStatus status
) {}
