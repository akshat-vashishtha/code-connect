package com.codeconnect.user.application.dto.response;

import java.time.Instant;

/**
 * Standard API Response envelope defined in coding-standards.md.
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    String traceId,
    Instant timestamp
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok("Success", data);
    }

    public static <T> ApiResponse<T> failure(String message, String traceId) {
        return new ApiResponse<>(false, message, null, traceId, Instant.now());
    }
}
