package com.codeconnect.collab.domain.enums;

/**
 * Execution status reflecting the code sandbox outcome.
 */
public enum ExecutionStatus {
    PASSED,
    FAILED,
    COMPILATION_ERROR,
    RUNTIME_ERROR,
    TIMED_OUT,
    MEMORY_EXCEEDED
}
