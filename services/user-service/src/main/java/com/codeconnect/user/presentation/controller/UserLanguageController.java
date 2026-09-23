package com.codeconnect.user.presentation.controller;

import com.codeconnect.user.application.dto.request.LanguageDetectionRequest;
import com.codeconnect.user.application.dto.response.ApiResponse;
import com.codeconnect.user.application.dto.response.LanguageDetectionResponse;
import com.codeconnect.user.application.service.UserLanguageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ultra-thin controller exposing conversational language detection endpoint.
 * Follows strict layering: delegates all domain logic and session mutation to UserLanguageService.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    @PostMapping("/detect-language")
    public ResponseEntity<ApiResponse<LanguageDetectionResponse>> detectLanguage(
        @Valid @RequestBody LanguageDetectionRequest request,
        HttpSession session
    ) {
        LanguageDetectionResponse response = userLanguageService.evaluatePreference(request, session);
        return ResponseEntity.ok(ApiResponse.ok("Language preference evaluated", response));
    }
}
