package com.codeconnect.gateway.infrastructure.security.specification;

import com.codeconnect.gateway.domain.enums.UserRole;

/**
 * Specification contract for route-level access control decisions.
 * Encapsulates pattern matching and role authorization policy for a specific route boundary.
 * Follows the Specification Pattern (GoF / DDD): each implementation codifies exactly one route policy.
 * Follows Open/Closed Principle: adding new route boundaries requires only a new implementation,
 * never modifying existing classes.
 */
public interface RouteAccessSpecification {

    /**
     * Returns true when this specification governs access to the given request path.
     */
    boolean matches(String path);

    /**
     * Returns true when the provided role satisfies the access policy for this route boundary.
     */
    boolean isAuthorized(UserRole role);

    /**
     * Returns true when this specification covers a protected route requiring authentication.
     */
    boolean isProtected();
}
