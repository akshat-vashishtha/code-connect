package com.codeconnect.curriculum.infrastructure.security;

import com.codeconnect.curriculum.infrastructure.security.chain.SecurityValidationChainAssembler;
import com.codeconnect.curriculum.infrastructure.security.chain.exception.SecurityValidationException;
import com.codeconnect.curriculum.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Facade over the Chain of Responsibility security validation pipeline for curriculum-service.
 * Delegates all validation steps to:
 * TimestampValidationHandler → SignatureValidationHandler → AuthenticationEstablishmentHandler.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSecurityValidator {

    private final SecurityValidationChainAssembler chainAssembler;

    public void validateAndAuthenticate(HttpServletRequest request) {
        if (!hasInternalSecurityHeaders(request)) {
            return;
        }
        executeValidationChain(request);
    }

    private boolean hasInternalSecurityHeaders(HttpServletRequest request) {
        return request.getHeader(InternalSecurityHeaders.TIMESTAMP) != null
            && request.getHeader(InternalSecurityHeaders.SIGNATURE) != null;
    }

    private void executeValidationChain(HttpServletRequest request) {
        try {
            chainAssembler.getChain().validate(request);
        } catch (SecurityValidationException e) {
            log.warn("Security validation failed for path={}: {}", request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}
