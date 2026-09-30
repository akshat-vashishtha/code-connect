package com.codeconnect.collab.application.dto.response;

/**
 * Sanitized, client-safe test result DTO.
 * Guarantees hidden tests redact input and expected outputs to prevent test sniffing.
 */
public record SanitizedTestResultDto(
        String testCaseId,
        String input,
        String expectedOutput,
        String actualOutput,
        boolean passed,
        boolean hidden,
        long executionTimeMs,
        String errorMessage
) {}
