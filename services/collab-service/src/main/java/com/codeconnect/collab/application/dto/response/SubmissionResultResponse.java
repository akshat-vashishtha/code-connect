package com.codeconnect.collab.application.dto.response;

import com.codeconnect.collab.domain.enums.ExecutionStatus;

import java.time.Instant;
import java.util.List;

/**
 * Clean, client-safe execution result envelope broadcasted over WebSocket STOMP.
 */
public record SubmissionResultResponse(
        String submissionId,
        String footholdId,
        String studentId,
        ExecutionStatus status,
        String compilerOutput,
        List<SanitizedTestResultDto> testResults,
        long totalDurationMs,
        boolean allPassed,
        Instant executedAt
) {}
