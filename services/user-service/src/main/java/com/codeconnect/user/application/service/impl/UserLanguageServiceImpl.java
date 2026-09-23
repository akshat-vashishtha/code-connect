package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.request.LanguageDetectionRequest;
import com.codeconnect.user.application.dto.response.LanguageDetectionResponse;
import com.codeconnect.user.application.service.LanguageDetector;
import com.codeconnect.user.application.service.UserLanguageService;
import com.codeconnect.user.domain.enums.LanguagePreference;
import com.codeconnect.user.infrastructure.session.UserSessionPreferenceManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserLanguageService orchestrating detection heuristics and session state updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLanguageServiceImpl implements UserLanguageService {

    private final LanguageDetector languageDetector;
    private final UserSessionPreferenceManager sessionPreferenceManager;

    @Override
    public LanguageDetectionResponse evaluatePreference(LanguageDetectionRequest request, HttpSession session) {
        LanguagePreference detected = languageDetector.detectLanguage(request.message());
        sessionPreferenceManager.setLanguagePreference(session, detected);

        log.debug("Evaluated language preference: detected={}, sessionUpdated={}", detected, session != null);
        return new LanguageDetectionResponse(detected, detected);
    }
}
