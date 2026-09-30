package com.codeconnect.curriculum.infrastructure.security.chain;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstract base providing chain-link plumbing for curriculum-service CoR security handlers.
 */
public abstract class AbstractSecurityValidationHandler implements SecurityValidationHandler {

    private SecurityValidationHandler next;

    @Override
    public SecurityValidationHandler setNext(SecurityValidationHandler next) {
        this.next = next;
        return next;
    }

    protected void passToNext(HttpServletRequest request) {
        if (next != null) {
            next.validate(request);
        }
    }
}
