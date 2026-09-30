package com.codeconnect.curriculum.infrastructure.security;

import com.codeconnect.curriculum.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter orchestrating incoming microservice request tracing context and internal zero-trust security authentication.
 * Delegates security validation and HMAC evaluation to InternalSecurityValidator collaborator (SLAP / SRP).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_ID           = InternalSecurityHeaders.USER_ID;
    public static final String HEADER_USER_ROLE          = InternalSecurityHeaders.USER_ROLE;
    public static final String HEADER_USER_EMAIL         = InternalSecurityHeaders.USER_EMAIL;
    public static final String HEADER_TIMESTAMP          = InternalSecurityHeaders.TIMESTAMP;
    public static final String HEADER_INTERNAL_SIGNATURE = InternalSecurityHeaders.SIGNATURE;
    public static final String HEADER_CORRELATION_ID     = "X-Correlation-ID";
    public static final String HEADER_SPAN_ID            = "X-Span-Id";

    private final InternalSecurityValidator securityValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        setupMdcContext(request, response);

        try {
            securityValidator.validateAndAuthenticate(request);
            filterChain.doFilter(request, response);
        } finally {
            clearMdcContext();
        }
    }

    private void setupMdcContext(HttpServletRequest request, HttpServletResponse response) {
        String incomingCorrelationId = request.getHeader(HEADER_CORRELATION_ID);
        String correlationId = (incomingCorrelationId != null && !incomingCorrelationId.isBlank())
            ? incomingCorrelationId
            : "req-" + UUID.randomUUID().toString().substring(0, 8);

        String spanId = "span-curr-" + UUID.randomUUID().toString().substring(0, 8);

        org.slf4j.MDC.put("traceId", correlationId);
        org.slf4j.MDC.put("spanId", spanId);

        response.setHeader(HEADER_CORRELATION_ID, correlationId);
        response.setHeader(HEADER_SPAN_ID, spanId);
    }

    private void clearMdcContext() {
        org.slf4j.MDC.remove("traceId");
        org.slf4j.MDC.remove("spanId");
    }
}
