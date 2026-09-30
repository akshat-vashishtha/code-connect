package com.codeconnect.sandbox.domain.enums;

/**
 * Execution status classification produced by the sandboxed worker.
 */
public enum ExecutionStatus {
    PASSED,
    FAILED,
    COMPILATION_ERROR,
    RUNTIME_ERROR,
    TIMED_OUT,
    MEMORY_EXCEEDED,
    ERROR
}
