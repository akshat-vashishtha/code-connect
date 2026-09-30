package com.codeconnect.submission.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSubmissionRequest(
    @NotBlank(message = "Foothold ID cannot be blank")
    String footholdId,

    @NotBlank(message = "Submission code cannot be blank")
    @Size(max = 65536, message = "Code payload cannot exceed 64KB")
    String code,

    String language
) {}
