package com.codeconnect.user.application.service.collaborator.language;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resolver responsible for evaluating ordered LanguageDetectionStrategy beans
 * and selecting the first strategy that supports the given text.
 */
@Component
@RequiredArgsConstructor
public class LanguageDetectionStrategyResolver {

    private final List<LanguageDetectionStrategy> strategies;

    /**
     * Resolves the appropriate strategy for the given text.
     *
     * @param text input conversational text
     * @return matching LanguageDetectionStrategy, or DefaultEnglishLanguageDetectionStrategy fallback
     */
    public LanguageDetectionStrategy resolve(String text) {
        return strategies.stream()
            .filter(strategy -> strategy.supports(text))
            .findFirst()
            .orElseGet(DefaultEnglishLanguageDetectionStrategy::new);
    }
}
