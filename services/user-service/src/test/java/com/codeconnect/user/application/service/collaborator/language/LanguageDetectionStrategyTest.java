package com.codeconnect.user.application.service.collaborator.language;

import com.codeconnect.user.domain.enums.LanguagePreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageDetectionStrategyTest {

    private RegexHeuristicLanguageDetectionStrategy regexStrategy;
    private DefaultEnglishLanguageDetectionStrategy defaultStrategy;
    private LanguageDetectionStrategyResolver resolver;
    private LanguageDetector languageDetector;

    @BeforeEach
    void setUp() {
        regexStrategy = new RegexHeuristicLanguageDetectionStrategy();
        defaultStrategy = new DefaultEnglishLanguageDetectionStrategy();
        resolver = new LanguageDetectionStrategyResolver(List.of(regexStrategy, defaultStrategy));
        languageDetector = new LanguageDetector(resolver);
    }

    @Test
    @DisplayName("Should detect Hinglish when colloquial Hindi/Hinglish vocabulary is present")
    void shouldDetectHinglishForColloquialText() {
        String input = "bhai yeh code kaise fix kare?";
        assertThat(regexStrategy.supports(input)).isTrue();
        assertThat(regexStrategy.detect(input)).isEqualTo(LanguagePreference.HINGLISH);

        assertThat(languageDetector.detectLanguage(input)).isEqualTo(LanguagePreference.HINGLISH);
    }

    @Test
    @DisplayName("Should fallback to English for standard English text")
    void shouldDetectEnglishForStandardText() {
        String input = "How can I implement clean architecture in Spring Boot?";
        assertThat(regexStrategy.supports(input)).isFalse();
        assertThat(defaultStrategy.supports(input)).isTrue();
        assertThat(defaultStrategy.detect(input)).isEqualTo(LanguagePreference.ENGLISH);

        assertThat(languageDetector.detectLanguage(input)).isEqualTo(LanguagePreference.ENGLISH);
    }

    @Test
    @DisplayName("Should safely handle null or blank input by defaulting to English")
    void shouldHandleNullOrBlankInput() {
        assertThat(languageDetector.detectLanguage(null)).isEqualTo(LanguagePreference.ENGLISH);
        assertThat(languageDetector.detectLanguage("   ")).isEqualTo(LanguagePreference.ENGLISH);
    }
}
