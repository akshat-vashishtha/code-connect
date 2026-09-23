package com.codeconnect.user.application.dto.response;

import com.codeconnect.user.domain.enums.LanguagePreference;

/**
 * Response payload describing detected and applied language preferences.
 */
public record LanguageDetectionResponse(
    LanguagePreference detectedLanguage,
    LanguagePreference sessionPreference
) {}
