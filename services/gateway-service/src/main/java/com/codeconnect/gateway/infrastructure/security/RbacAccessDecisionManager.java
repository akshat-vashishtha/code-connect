package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.infrastructure.security.specification.RouteAccessSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Evaluates route protection and RBAC authorization policies by delegating
 * to the registered chain of {@link RouteAccessSpecification} implementations.
 * Follows the Specification Pattern (GoF) and Open/Closed Principle:
 * new route boundaries are registered as Spring beans with zero edits to this class.
 */
@Component
@RequiredArgsConstructor
public class RbacAccessDecisionManager {

    private final List<RouteAccessSpecification> specifications;

    public boolean isPublicPath(String path) {
        return specifications.stream()
            .filter(spec -> spec.matches(path))
            .noneMatch(RouteAccessSpecification::isProtected);
    }

    public boolean isAuthorized(String path, String roleStr) {
        UserRole role = resolveRole(roleStr).orElse(null);

        return specifications.stream()
            .filter(spec -> spec.matches(path))
            .findFirst()
            .map(spec -> spec.isAuthorized(role))
            .orElse(true);
    }

    private Optional<UserRole> resolveRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UserRole.valueOf(roleStr));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
