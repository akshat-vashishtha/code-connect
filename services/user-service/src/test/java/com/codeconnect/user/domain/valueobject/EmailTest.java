package com.codeconnect.user.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "student@codeconnect.com",
        "mentor.alex@university.edu",
        "ADMIN_123@domain.org"
    })
    @DisplayName("Should instantiate valid email and normalize to lowercase")
    void validEmailShouldSucceed(String validEmail) {
        Email email = new Email(validEmail);
        assertThat(email.value()).isEqualTo(validEmail.trim().toLowerCase());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "missing-at-domain.com",
        "@nodomain.com",
        "user@",
        ""
    })
    @DisplayName("Should throw IllegalArgumentException for invalid email format")
    void invalidEmailShouldThrowException(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when email is null")
    void nullEmailShouldThrowException() {
        assertThatThrownBy(() -> new Email(null))
            .isInstanceOf(NullPointerException.class);
    }
}
