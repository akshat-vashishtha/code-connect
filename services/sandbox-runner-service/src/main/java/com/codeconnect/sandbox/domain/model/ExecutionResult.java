package com.codeconnect.sandbox.domain.model;

import com.codeconnect.sandbox.domain.enums.ExecutionStatus;

import java.util.List;

/**
 * Domain value object encapsulating complete execution outcomes.
 */
public record ExecutionResult(
    ExecutionStatus status,
    int totalTests,
    int passedTests,
    List<TestResultDetail> testResults,
    String stdout,
    String stderr,
    long durationMs
) {}
