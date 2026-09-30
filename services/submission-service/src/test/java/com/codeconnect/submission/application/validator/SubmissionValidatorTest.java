package com.codeconnect.submission.application.validator;

import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.domain.exception.SubmissionValidationException;
import com.codeconnect.submission.infrastructure.config.properties.SubmissionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubmissionValidatorTest {

    private SubmissionValidator validator;

    @BeforeEach
    void setUp() {
        SubmissionProperties properties = new SubmissionProperties("secret", 65536);
        validator = new SubmissionValidator(properties);
    }

    @Test
    @DisplayName("Should pass validation for valid request")
    void shouldPassValidRequest() {
        CreateSubmissionRequest request = new CreateSubmissionRequest("foothold-1", "class Solution {}", "JAVA");
        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw when request is null")
    void shouldThrowWhenNull() {
        assertThatThrownBy(() -> validator.validate(null))
            .isInstanceOf(SubmissionValidationException.class)
            .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should throw when foothold ID is blank")
    void shouldThrowWhenBlankFootholdId() {
        CreateSubmissionRequest request = new CreateSubmissionRequest("", "class Solution {}", "JAVA");
        assertThatThrownBy(() -> validator.validate(request))
            .isInstanceOf(SubmissionValidationException.class)
            .hasMessageContaining("Foothold ID is required");
    }

    @Test
    @DisplayName("Should throw when code is blank")
    void shouldThrowWhenBlankCode() {
        CreateSubmissionRequest request = new CreateSubmissionRequest("foothold-1", "   ", "JAVA");
        assertThatThrownBy(() -> validator.validate(request))
            .isInstanceOf(SubmissionValidationException.class)
            .hasMessageContaining("Source code cannot be blank");
    }

    @Test
    @DisplayName("Should throw when code size exceeds 64KB")
    void shouldThrowWhenSizeExceeded() {
        String giantCode = "a".repeat(70000);
        CreateSubmissionRequest request = new CreateSubmissionRequest("foothold-1", giantCode, "JAVA");
        assertThatThrownBy(() -> validator.validate(request))
            .isInstanceOf(SubmissionValidationException.class)
            .hasMessageContaining("exceeds maximum limit");
    }
}
