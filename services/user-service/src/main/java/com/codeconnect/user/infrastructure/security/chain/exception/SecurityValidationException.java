package com.codeconnect.user.infrastructure.security.chain.exception;

/**
 * Runtime exception signalling a security validation failure in the CoR pipeline.
 * Caught by InternalAuthenticationFilter to send a 403 Forbidden response.
 * Using a runtime exception keeps handler signatures clean (no checked IOException propagation).
 */
public class SecurityValidationException extends RuntimeException {

    public SecurityValidationException(String message) {
        super(message);
    }
}
