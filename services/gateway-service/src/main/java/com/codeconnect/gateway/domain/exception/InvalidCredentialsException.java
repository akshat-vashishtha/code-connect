package com.codeconnect.gateway.domain.exception;

/**
 * Thrown when credential authentication fails (unregistered email or mismatched password).
 * Does not differentiate between the two cases to guard against user enumeration attacks.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
