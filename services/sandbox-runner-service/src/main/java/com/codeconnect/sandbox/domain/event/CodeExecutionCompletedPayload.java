package com.codeconnect.sandbox.domain.event;

import com.codeconnect.sandbox.domain.enums.ExecutionStatus;
import com.codeconnect.sandbox.domain.model.TestResultDetail;

import java.time.Instant;
import java.util.List;

/**
 * Domain payload for completed code execution results published to Kafka.
 */
public record CodeExecutionCompletedPayload(
    String submissionId,
    String studentId,
    String studentEmail,
    String footholdId,
    ExecutionStatus status,
    int totalTests,
    int passedTests,
    List<TestResultDetail> testResults,
    String stdout,
    String stderr,
    long durationMs,
    Instant completedAt
) {}
