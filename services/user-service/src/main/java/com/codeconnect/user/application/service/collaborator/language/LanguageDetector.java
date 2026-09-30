package com.codeconnect.user.application.service.collaborator.language;

import com.codeconnect.user.domain.enums.LanguagePreference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Conversational language detector facade delegating to LanguageDetectionStrategyResolver.
 * Conforms to GoF Strategy Pattern, maintaining backward-compatible interface for callers.
 */
@Component
@RequiredArgsConstructor
public class LanguageDetector {

    private final LanguageDetectionStrategyResolver strategyResolver;

    public LanguagePreference detectLanguage(String text) {
        if (text == null || text.isBlank()) {
            return LanguagePreference.ENGLISH;
        }
        return strategyResolver.resolve(text).detect(text);
    }
}
