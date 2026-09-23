package com.codeconnect.user.application.dto;

import com.codeconnect.user.domain.enums.LanguagePreference;

public record LanguageDetectionResponse(
    LanguagePreference detectedLanguage,
    LanguagePreference sessionPreference
) {}
