package com.codeconnect.curriculum.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateModuleRequest(
    @NotBlank(message = "Track ID is required")
    String trackId,

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Slug is required")
    String slug,

    @NotNull(message = "Sequence is required")
    @Positive(message = "Sequence must be positive")
    Integer sequence,

    @NotBlank(message = "Description is required")
    String description,

    String prerequisiteModuleId
) {}
