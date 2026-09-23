package com.codeconnect.user.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LanguageDetectionRequest(
    @NotBlank(message = "Message text must not be blank")
    String message
) {}
