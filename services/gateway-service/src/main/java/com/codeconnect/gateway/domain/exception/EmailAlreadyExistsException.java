package com.codeconnect.gateway.domain.exception;

/**
 * Thrown when registration is attempted with an email that is already registered.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("Email '" + email + "' is already registered in CodeConnect");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
