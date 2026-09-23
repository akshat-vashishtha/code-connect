package com.codeconnect.user.domain.exception;

public class AccountBannedException extends RuntimeException {
    public AccountBannedException() {
        super("Account has been suspended");
    }
}
