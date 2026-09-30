package com.codeconnect.user.application.service.collaborator.language;

import com.codeconnect.user.domain.enums.LanguagePreference;

/**
 * Strategy interface (GoF Strategy Pattern) defining conversational language detection.
 * Allows pluggable heuristics, regex matchers, or machine learning models.
 */
public interface LanguageDetectionStrategy {

    /**
     * Determines whether this strategy can evaluate or claims detection for the provided text.
     *
     * @param text input conversational text
     * @return true if this strategy applies to the text
     */
    boolean supports(String text);

    /**
     * Detects the language preference from the provided text.
     *
     * @param text input conversational text
     * @return detected LanguagePreference
     */
    LanguagePreference detect(String text);
}
