package com.codeconnect.user.application.service.collaborator.language;

import com.codeconnect.user.domain.enums.LanguagePreference;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Fallback strategy defaulting to English when no specific regional or colloquial markers match.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultEnglishLanguageDetectionStrategy implements LanguageDetectionStrategy {

    @Override
    public boolean supports(String text) {
        return true;
    }

    @Override
    public LanguagePreference detect(String text) {
        return LanguagePreference.ENGLISH;
    }
}
