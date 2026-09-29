package com.codeconnect.user.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter enforcing Zero-Trust Tier 2 security for incoming microservice requests.
 * Delegates HMAC signature verification and timestamp validation to InternalSecurityValidator collaborator (SLAP / SRP).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_ID = InternalSecurityValidator.HEADER_USER_ID;
    public static final String HEADER_USER_ROLE = InternalSecurityValidator.HEADER_USER_ROLE;
    public static final String HEADER_USER_EMAIL = InternalSecurityValidator.HEADER_USER_EMAIL;
    public static final String HEADER_TIMESTAMP = InternalSecurityValidator.HEADER_TIMESTAMP;
    public static final String HEADER_INTERNAL_SIGNATURE = InternalSecurityValidator.HEADER_INTERNAL_SIGNATURE;
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_SPAN_ID = "X-Span-Id";

    private final InternalSecurityValidator securityValidator;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        setupMdcContext(request, response);

        try {
            String path = request.getRequestURI();
            if (path.startsWith("/actuator/health")) {
                filterChain.doFilter(request, response);
                return;
            }

            boolean isValid = securityValidator.validateAndAuthenticate(request, response);

            if (isValid) {
                filterChain.doFilter(request, response);
            }
        } finally {
            clearMdcContext();
        }
    }

    private void setupMdcContext(HttpServletRequest request, HttpServletResponse response) {
        String incomingCorrelationId = request.getHeader(HEADER_CORRELATION_ID);
        String correlationId = (incomingCorrelationId != null && !incomingCorrelationId.isBlank())
            ? incomingCorrelationId
            : "req-" + UUID.randomUUID().toString().substring(0, 8);

        String spanId = "span-user-" + UUID.randomUUID().toString().substring(0, 8);

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
