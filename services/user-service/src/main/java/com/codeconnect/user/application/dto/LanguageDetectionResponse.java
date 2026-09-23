package com.codeconnect.user.application.dto;

import com.codeconnect.user.domain.model.LanguagePreference;

public record LanguageDetectionResponse(
    LanguagePreference detectedLanguage,
    LanguagePreference sessionPreference
) {}
