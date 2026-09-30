package com.codeconnect.curriculum.infrastructure.security.chain.exception;

/**
 * Security exception signalling validation failure in the curriculum-service CoR pipeline.
 * Caught by InternalAuthenticationFilter to clear the SecurityContext.
 */
public class SecurityValidationException extends RuntimeException {

    public SecurityValidationException(String message) {
        super(message);
    }
}
