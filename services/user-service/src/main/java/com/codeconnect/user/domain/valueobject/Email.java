package com.codeconnect.user.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable Email Value Object enforcing domain invariants.
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public Email {
        Objects.requireNonNull(value, "Email value must not be null");
        String trimmed = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        value = trimmed;
    }
}
