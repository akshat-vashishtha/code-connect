package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.request.LanguageDetectionRequest;
import com.codeconnect.user.application.dto.response.LanguageDetectionResponse;
import com.codeconnect.user.application.service.collaborator.language.LanguageDetector;
import com.codeconnect.user.application.service.UserLanguageService;
import com.codeconnect.user.domain.enums.LanguagePreference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserLanguageService orchestrating stateless language detection heuristics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLanguageServiceImpl implements UserLanguageService {

    private final LanguageDetector languageDetector;

    @Override
    public LanguageDetectionResponse evaluatePreference(LanguageDetectionRequest request) {
        LanguagePreference detected = languageDetector.detectLanguage(request.message());
        log.debug("Evaluated language preference: detected={}", detected);
        return new LanguageDetectionResponse(detected, detected);
    }
}
