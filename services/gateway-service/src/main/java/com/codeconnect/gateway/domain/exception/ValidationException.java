package com.codeconnect.gateway.domain.exception;

/**
 * Thrown when domain validation rules are violated (e.g., missing mentor qualifications).
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
