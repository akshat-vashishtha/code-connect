package com.codeconnect.user.presentation.controller;

import com.codeconnect.user.application.dto.ApiResponse;
import com.codeconnect.user.application.dto.LanguageDetectionRequest;
import com.codeconnect.user.application.dto.LanguageDetectionResponse;
import com.codeconnect.user.application.service.LanguageDetector;
import com.codeconnect.user.domain.model.LanguagePreference;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing heuristic language detection and active session preference tagging.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserLanguageController {

    public static final String ATTR_LANGUAGE_PREFERENCE = "languagePreference";

    private final LanguageDetector languageDetector;

    public UserLanguageController(LanguageDetector languageDetector) {
        this.languageDetector = languageDetector;
    }

    @PostMapping("/detect-language")
    public ResponseEntity<ApiResponse<LanguageDetectionResponse>> detectLanguage(
        @Valid @RequestBody LanguageDetectionRequest request,
        HttpSession session
    ) {
        LanguagePreference detected = languageDetector.detectLanguage(request.message());

        if (session != null) {
            session.setAttribute(ATTR_LANGUAGE_PREFERENCE, detected.name());
        }

        LanguageDetectionResponse response = new LanguageDetectionResponse(
            detected,
            detected
        );

        return ResponseEntity.ok(ApiResponse.ok("Language preference evaluated", response));
    }
}
