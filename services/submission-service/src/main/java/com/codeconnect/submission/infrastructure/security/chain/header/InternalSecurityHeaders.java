package com.codeconnect.submission.infrastructure.security.chain.header;

public final class InternalSecurityHeaders {

    public static final String USER_ID = "X-Internal-User-Id";
    public static final String USER_ROLE = "X-Internal-User-Role";
    public static final String USER_EMAIL = "X-Internal-User-Email";
    public static final String TIMESTAMP = "X-Internal-Timestamp";
    public static final String SIGNATURE = "X-Internal-Signature";

    private InternalSecurityHeaders() {}
}
