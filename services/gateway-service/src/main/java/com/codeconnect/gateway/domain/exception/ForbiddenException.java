package com.codeconnect.gateway.domain.exception;

/**
 * Thrown when an authenticated or unauthenticated entity attempts to access a protected
 * resource without requisite role privileges.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Insufficient role permissions for requested resource");
    }
}
