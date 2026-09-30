package com.codeconnect.user.infrastructure.security.chain;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstract base providing the default chain-link plumbing for all CoR security handlers.
 * Concrete handlers extend this class and implement only their focused validation step,
 * calling {@code passToNext(request)} to delegate to the next handler on success.
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
