package com.codeconnect.user.infrastructure.security;

/**
 * Centralized, non-instantiable constants class for all internal Zero-Trust security header names.
 * Provides a single source of truth consumed by filters, validators, and HMAC signers alike —
 * eliminating the coupling of a constants holder onto a behavioural filter class.
 */
public final class InternalSecurityHeaders {

    public static final String USER_ID        = "X-User-Id";
    public static final String USER_ROLE      = "X-User-Role";
    public static final String USER_EMAIL     = "X-User-Email";
    public static final String TIMESTAMP      = "X-Timestamp";
    public static final String SIGNATURE      = "X-Internal-Signature";
    public static final String CORRELATION_ID = "X-Correlation-ID";
    public static final String SPAN_ID        = "X-Span-Id";

    private InternalSecurityHeaders() {}
}
