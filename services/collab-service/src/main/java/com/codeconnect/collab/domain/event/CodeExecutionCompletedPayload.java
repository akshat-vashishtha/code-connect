package com.codeconnect.collab.domain.event;

import com.codeconnect.collab.domain.enums.ExecutionStatus;
import com.codeconnect.collab.domain.model.TestResultDetail;

import java.time.Instant;
import java.util.List;

/**
 * Domain payload for completed code execution results consumed from Kafka.
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
