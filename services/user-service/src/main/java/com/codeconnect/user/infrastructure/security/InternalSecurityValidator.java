package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.infrastructure.security.chain.SecurityValidationChainAssembler;
import com.codeconnect.user.infrastructure.security.chain.exception.SecurityValidationException;
import com.codeconnect.user.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Facade over the Chain of Responsibility security validation pipeline.
 * Delegates all validation steps to the ordered CoR chain:
 * TimestampValidationHandler → SignatureValidationHandler → AuthenticationEstablishmentHandler.
 *
 * This class is exclusively responsible for:
 * 1. Detecting whether a request carries internal security headers
 * 2. Invoking the CoR chain and translating SecurityValidationException to HTTP 403 responses
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSecurityValidator {

    private final SecurityValidationChainAssembler chainAssembler;

    /**
     * Returns true if validation passed or was not required (no internal headers present).
     * Returns false if a validation failure was written to the response.
     */
    public boolean validateAndAuthenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!hasInternalSecurityHeaders(request)) {
            return true;
        }
        return executeValidationChain(request, response);
    }

    private boolean hasInternalSecurityHeaders(HttpServletRequest request) {
        return request.getHeader(InternalSecurityHeaders.TIMESTAMP) != null
            && request.getHeader(InternalSecurityHeaders.SIGNATURE) != null;
    }

    private boolean executeValidationChain(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            chainAssembler.getChain().validate(request);
            return true;
        } catch (SecurityValidationException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return false;
        }
    }
}
