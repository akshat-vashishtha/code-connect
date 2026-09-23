package com.codeconnect.user.infrastructure.session;

import com.codeconnect.user.domain.enums.LanguagePreference;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * Collaborator responsible for persisting and retrieving user preferences in HTTP sessions.
 * Encapsulates session attribute keys and mutations away from presentation and domain services.
 */
@Component
public class UserSessionPreferenceManager {

    private static final String ATTR_LANGUAGE_PREFERENCE = "languagePreference";

    public void setLanguagePreference(HttpSession session, LanguagePreference preference) {
        if (session != null && preference != null) {
            session.setAttribute(ATTR_LANGUAGE_PREFERENCE, preference.name());
        }
    }

    public LanguagePreference getLanguagePreference(HttpSession session) {
        if (session == null) {
            return LanguagePreference.ENGLISH;
        }
        Object attr = session.getAttribute(ATTR_LANGUAGE_PREFERENCE);
        if (attr instanceof String name) {
            try {
                return LanguagePreference.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                return LanguagePreference.ENGLISH;
            }
        }
        return LanguagePreference.ENGLISH;
    }
}
