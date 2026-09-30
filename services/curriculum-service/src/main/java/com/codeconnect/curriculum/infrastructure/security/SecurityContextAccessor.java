package com.codeconnect.curriculum.infrastructure.security;

import com.codeconnect.curriculum.domain.enums.UserRole;

import java.util.Optional;

/**
 * Abstraction over the security context providing domain-level access to the authenticated principal.
 * Follows the Dependency Inversion Principle: service classes depend on this interface,
 * not on Spring Security's static SecurityContextHolder infrastructure directly.
 */
public interface SecurityContextAccessor {

    Optional<String> resolveAuthenticatedUserId();

    boolean hasRole(UserRole role);

    boolean isAuthenticated();
}
