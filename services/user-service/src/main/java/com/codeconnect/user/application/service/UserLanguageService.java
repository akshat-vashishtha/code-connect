package com.codeconnect.user.application.service;

import com.codeconnect.user.application.dto.request.LanguageDetectionRequest;
import com.codeconnect.user.application.dto.response.LanguageDetectionResponse;

/**
 * Business service interface orchestrating conversational language detection.
 */
public interface UserLanguageService {

    LanguageDetectionResponse evaluatePreference(LanguageDetectionRequest request);
}
