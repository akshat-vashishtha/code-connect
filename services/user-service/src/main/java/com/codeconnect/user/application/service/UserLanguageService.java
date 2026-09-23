package com.codeconnect.user.application.service;

import com.codeconnect.user.application.dto.request.LanguageDetectionRequest;
import com.codeconnect.user.application.dto.response.LanguageDetectionResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Business service interface orchestrating conversational language detection and session preference updates.
 */
public interface UserLanguageService {

    LanguageDetectionResponse evaluatePreference(LanguageDetectionRequest request, HttpSession session);
}
