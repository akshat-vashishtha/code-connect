package com.codeconnect.gateway.domain.exception;

/**
 * Thrown when an authenticated login attempt is made for a suspended/banned account.
 */
public class AccountBannedException extends RuntimeException {

    public AccountBannedException(String message) {
        super(message);
    }

    public AccountBannedException() {
        super("Account has been suspended by an administrator");
    }
}
