package com.codeconnect.submission.application.validator;

import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.domain.exception.SubmissionValidationException;
import com.codeconnect.submission.infrastructure.config.properties.SubmissionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SubmissionValidator {

    private final SubmissionProperties submissionProperties;

    public void validate(CreateSubmissionRequest request) {
        if (request == null) {
            throw new SubmissionValidationException("Submission request cannot be null");
        }
        if (request.footholdId() == null || request.footholdId().isBlank()) {
            throw new SubmissionValidationException("Foothold ID is required");
        }
        if (request.code() == null || request.code().isBlank()) {
            throw new SubmissionValidationException("Source code cannot be blank");
        }
        byte[] bytes = request.code().getBytes(StandardCharsets.UTF_8);
        if (bytes.length > submissionProperties.maxCodeSizeBytes()) {
            throw new SubmissionValidationException(
                "Source code size (" + bytes.length + " bytes) exceeds maximum limit of " + submissionProperties.maxCodeSizeBytes() + " bytes"
            );
        }
    }
}
