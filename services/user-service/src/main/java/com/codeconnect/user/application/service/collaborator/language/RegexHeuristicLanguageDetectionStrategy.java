package com.codeconnect.user.application.service.collaborator.language;

import com.codeconnect.user.domain.enums.LanguagePreference;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Concrete strategy implementing regex heuristic matching for conversational Hinglish markers.
 */
@Component
@Order(10)
public class RegexHeuristicLanguageDetectionStrategy implements LanguageDetectionStrategy {

    private static final List<Pattern> HINGLISH_PATTERNS = List.of(
        Pattern.compile("\\b(bhai|bhaiya|bro|yaar)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(samajh|samaj|samjha|samjho)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(kaise|kese|kaise kare|kaise karu)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(nahi|nhi|na|mat)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(aaya|aya|aa rha|aa raha)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(kya|kyun|kyu|kisko)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(batao|bata|bataiye|bolo)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(karna|karenge|karne|karo|karte)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(yeh|ye|woh|wo|iska|uska)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(hota|hoti|hote|hoga|hogi)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(madad|problem|code fat gaya|error aa gaya)\\b", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public boolean supports(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return HINGLISH_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(text).find());
    }

    @Override
    public LanguagePreference detect(String text) {
        return LanguagePreference.HINGLISH;
    }
}
