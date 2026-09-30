package com.codeconnect.curriculum.infrastructure.security.chain.header;

/**
 * Centralized header name constants for the curriculum-service internal security protocol.
 */
public final class InternalSecurityHeaders {

    public static final String USER_ID    = "X-User-Id";
    public static final String USER_ROLE  = "X-User-Role";
    public static final String USER_EMAIL = "X-User-Email";
    public static final String TIMESTAMP  = "X-Timestamp";
    public static final String SIGNATURE  = "X-Internal-Signature";

    private InternalSecurityHeaders() {}
}
