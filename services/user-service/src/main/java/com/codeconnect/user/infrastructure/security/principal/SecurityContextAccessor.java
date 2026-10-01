package com.codeconnect.user.infrastructure.security.principal;

import com.codeconnect.user.domain.enums.UserRole;

import java.util.Optional;

/**
 * Abstraction over the Spring Security context providing domain-level access to
 * the authenticated principal.
 *
 * <p>Follows the Dependency Inversion Principle: application services depend on this
 * interface, not on Spring Security's static {@code SecurityContextHolder} infrastructure.
 * This makes services independently testable without starting a Spring Security context.
 */
public interface SecurityContextAccessor {

    Optional<String> resolveAuthenticatedUserId();

    Optional<String> resolveAuthenticatedUserEmail();

    boolean hasRole(UserRole role);

    boolean isAuthenticated();
}
