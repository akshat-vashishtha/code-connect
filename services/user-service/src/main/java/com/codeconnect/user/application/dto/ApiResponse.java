package com.codeconnect.user.application.dto;

import java.time.Instant;

/**
 * Standard API response envelope matching enterprise guidelines.
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    Instant timestamp
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }
}
