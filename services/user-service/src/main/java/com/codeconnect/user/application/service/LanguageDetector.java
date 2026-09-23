package com.codeconnect.user.application.service;

import com.codeconnect.user.domain.enums.LanguagePreference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Heuristic conversational language detector identifying English vs. Hinglish phrasing.
 * Recognizes characteristic Hindi/Hinglish vocabulary markers (e.g. bhai, samajh, kaise, kare, yaar).
 */
@Component
public class LanguageDetector {

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

    public LanguagePreference detectLanguage(String text) {
        if (text == null || text.isBlank()) {
            return LanguagePreference.ENGLISH;
        }

        long matches = HINGLISH_PATTERNS.stream()
            .filter(pattern -> pattern.matcher(text).find())
            .count();

        return matches >= 1 ? LanguagePreference.HINGLISH : LanguagePreference.ENGLISH;
    }
}
