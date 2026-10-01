package com.codeconnect.user.infrastructure.security.filter;

import com.codeconnect.user.infrastructure.security.InternalSecurityHeaders;
import com.codeconnect.user.infrastructure.security.handler.JsonAuthenticationEntryPoint;
import com.codeconnect.user.infrastructure.security.validator.InternalSecurityValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Stateless Zero-Trust Tier-2 security filter for user-service inter-microservice requests.
 *
 * <h3>Responsibilities (single level of abstraction)</h3>
 * <ol>
 *   <li>Establish an MDC tracing context ({@code traceId} / {@code spanId}) for every request.</li>
 *   <li>If Zero-Trust headers are present, delegate validation to {@link InternalSecurityValidator}.</li>
 *   <li>On success: set the resulting {@link Authentication} into the {@link SecurityContextHolder}.</li>
 *   <li>On failure: clear the context and write a structured JSON 401 via {@link JsonAuthenticationEntryPoint}.</li>
 *   <li>Always clear the MDC context in a {@code finally} block — even on error paths.</li>
 * </ol>
 *
 * <h3>Filter chain position</h3>
 * Positioned <em>before</em> {@link UsernamePasswordAuthenticationFilter} — the canonical position
 * for stateless header-based pre-authentication filters in Spring Security 6. This ensures the
 * {@code SecurityContextHolder} is populated before {@code AnonymousAuthenticationFilter} runs,
 * preventing authenticated internal requests from being treated as anonymous.
 *
 * <h3>Why exceptions are caught internally (not re-thrown)</h3>
 * This filter runs <em>before</em> {@code ExceptionTranslationFilter} in the chain.
 * Any uncaught {@link AuthenticationException} would propagate past the
 * {@code ExceptionTranslationFilter} unseen (it only wraps filters that run <em>after</em> it).
 * The filter therefore catches the exception and invokes {@link JsonAuthenticationEntryPoint}
 * directly — the correct pattern per the Spring Security architecture documentation.
 *
 * <h3>Header constants</h3>
 * Constants are re-exported from {@link InternalSecurityHeaders} as {@code public static final}
 * fields to preserve backwards-compatibility with any test or caller referencing
 * {@code InternalAuthenticationFilter.HEADER_*}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalAuthenticationFilter extends OncePerRequestFilter {

    // Re-exported from InternalSecurityHeaders to preserve test/caller compatibility
    public static final String HEADER_USER_ID            = InternalSecurityHeaders.USER_ID;
    public static final String HEADER_USER_ROLE          = InternalSecurityHeaders.USER_ROLE;
    public static final String HEADER_USER_EMAIL         = InternalSecurityHeaders.USER_EMAIL;
    public static final String HEADER_TIMESTAMP          = InternalSecurityHeaders.TIMESTAMP;
    public static final String HEADER_INTERNAL_SIGNATURE = InternalSecurityHeaders.SIGNATURE;
    public static final String HEADER_CORRELATION_ID     = InternalSecurityHeaders.CORRELATION_ID;
    public static final String HEADER_SPAN_ID            = InternalSecurityHeaders.SPAN_ID;

    private final InternalSecurityValidator    securityValidator;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest  request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        setupMdcContext(request, response);

        try {
            if (securityValidator.hasInternalSecurityHeaders(request)) {
                try {
                    Authentication authentication = securityValidator.authenticate(request);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (AuthenticationException ex) {
                    SecurityContextHolder.clearContext();
                    authenticationEntryPoint.commence(request, response, ex);
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            clearMdcContext();
        }
    }

    // --- MDC tracing context (observability concern, kept as private helpers) ---

    private void setupMdcContext(HttpServletRequest request, HttpServletResponse response) {
        String incoming      = request.getHeader(HEADER_CORRELATION_ID);
        String correlationId = (incoming != null && !incoming.isBlank())
            ? incoming
            : "req-" + UUID.randomUUID().toString().substring(0, 8);
        String spanId = "span-user-" + UUID.randomUUID().toString().substring(0, 8);

        MDC.put("traceId", correlationId);
        MDC.put("spanId",  spanId);
        response.setHeader(HEADER_CORRELATION_ID, correlationId);
        response.setHeader(HEADER_SPAN_ID,        spanId);
    }

    private void clearMdcContext() {
        MDC.remove("traceId");
        MDC.remove("spanId");
    }
}
