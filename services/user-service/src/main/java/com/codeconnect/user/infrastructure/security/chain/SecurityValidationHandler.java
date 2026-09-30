package com.codeconnect.user.infrastructure.security.chain;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handler contract for the Chain of Responsibility security validation pipeline.
 * Each handler performs one focused validation step (signature, timestamp, header presence)
 * and delegates to the next handler in the chain on success.
 *
 * Follows the Chain of Responsibility Pattern (GoF): validation steps are decoupled,
 * independently testable, and trivially composable into ordered pipelines.
 */
public interface SecurityValidationHandler {

    /**
     * Links this handler to the next handler in the validation chain.
     *
     * @return this handler (fluent API for chain construction)
     */
    SecurityValidationHandler setNext(SecurityValidationHandler next);

    /**
     * Validates the request. On success, delegates to the next handler.
     * On failure, throws a domain-appropriate exception.
     *
     * @param request the incoming HTTP request
     * @throws com.codeconnect.user.domain.exception.UnauthorizedException on validation failure
     */
    void validate(HttpServletRequest request);
}
