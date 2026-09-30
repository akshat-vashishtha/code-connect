package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.domain.enums.UserRole;

import java.util.Optional;

/**
 * Abstraction over the security context providing domain-level access to the authenticated principal.
 * Follows the Dependency Inversion Principle: classes depend on this interface,
 * not on Spring Security's static SecurityContextHolder infrastructure directly.
 */
public interface SecurityContextAccessor {

    Optional<String> resolveAuthenticatedUserId();

    Optional<String> resolveAuthenticatedUserEmail();

    boolean hasRole(UserRole role);

    boolean isAuthenticated();
}
