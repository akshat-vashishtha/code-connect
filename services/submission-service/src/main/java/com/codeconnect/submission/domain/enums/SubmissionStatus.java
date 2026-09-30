package com.codeconnect.submission.domain.enums;

/**
 * Lifecycle states of a code submission throughout the asynchronous pipeline.
 */
public enum SubmissionStatus {
    PENDING,
    RUNNING,
    PASSED,
    FAILED,
    TIMED_OUT,
    MEMORY_EXCEEDED,
    ERROR
}
