package com.codeconnect.sandbox.domain.model;

/**
 * Detailed test case result record.
 */
public record TestResultDetail(
    String testName,
    boolean passed,
    String input,
    String expectedOutput,
    String actualOutput,
    String errorMessage,
    long executionTimeMs
) {}
