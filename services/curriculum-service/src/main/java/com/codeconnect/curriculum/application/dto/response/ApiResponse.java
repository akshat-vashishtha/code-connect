package com.codeconnect.curriculum.application.dto.response;

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

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> failure(String message, String traceId) {
        return new ApiResponse<>(false, message, null, traceId, Instant.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ok(message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return ok(data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null, Instant.now());
    }
}
