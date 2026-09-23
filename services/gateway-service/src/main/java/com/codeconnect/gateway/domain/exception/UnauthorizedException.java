package com.codeconnect.gateway.domain.exception;

/**
 * Thrown when an unauthenticated request attempts to access protected session resources.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
