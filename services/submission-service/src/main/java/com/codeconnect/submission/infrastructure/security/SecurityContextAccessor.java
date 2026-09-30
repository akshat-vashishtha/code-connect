package com.codeconnect.submission.infrastructure.security;

public interface SecurityContextAccessor {
    String getAuthenticatedUserId();
    String getAuthenticatedUserEmail();
}
