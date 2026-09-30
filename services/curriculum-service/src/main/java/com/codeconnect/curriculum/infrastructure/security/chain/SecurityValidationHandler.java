package com.codeconnect.curriculum.infrastructure.security.chain;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handler contract for the Chain of Responsibility security validation pipeline in curriculum-service.
 * Each handler encapsulates one focused validation step and delegates to the next on success.
 */
public interface SecurityValidationHandler {

    SecurityValidationHandler setNext(SecurityValidationHandler next);

    /**
     * Validates the request. Delegates to the next handler on success.
     * Throws SecurityValidationException on failure.
     */
    void validate(HttpServletRequest request);
}
