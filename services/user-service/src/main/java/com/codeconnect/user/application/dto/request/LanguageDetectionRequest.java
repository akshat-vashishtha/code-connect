package com.codeconnect.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload containing conversational text to evaluate for language detection.
 */
public record LanguageDetectionRequest(
    @NotBlank(message = "Message text is required for language detection")
    String message
) {}
