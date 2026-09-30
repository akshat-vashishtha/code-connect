package com.codeconnect.collab.domain.model;

/**
 * Immutable record representing the execution output of a single test case.
 */
public record TestResultDetail(
    String testCaseId,
    String input,
    String expectedOutput,
    String actualOutput,
    boolean passed,
    boolean hidden,
    long executionTimeMs,
    String errorMessage
) {}
