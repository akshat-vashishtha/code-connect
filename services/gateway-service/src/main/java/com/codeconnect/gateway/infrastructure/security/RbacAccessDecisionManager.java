package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.infrastructure.config.GatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Evaluates route protection criteria and RBAC authorization policies.
 * Strictly adheres to Single Responsibility Principle (SRP).
 */
@Component
@RequiredArgsConstructor
public class RbacAccessDecisionManager {

    private final GatewayProperties gatewayProperties;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public boolean isProtected(String path) {
        return isAdminPath(path) || isMentorPath(path);
    }

    public boolean isAdminPath(String path) {
        return pathMatcher.match(gatewayProperties.adminPathPattern(), path);
    }

    public boolean isMentorPath(String path) {
        return pathMatcher.match(gatewayProperties.mentorPathPattern(), path);
    }

    public boolean isAuthorized(String path, String role) {
        if (role == null) {
            return false;
        }
        if (isAdminPath(path)) {
            return UserRole.ROLE_ADMIN.name().equals(role);
        }
        if (isMentorPath(path)) {
            return UserRole.ROLE_MENTOR.name().equals(role) || UserRole.ROLE_ADMIN.name().equals(role);
        }
        return true;
    }
}
