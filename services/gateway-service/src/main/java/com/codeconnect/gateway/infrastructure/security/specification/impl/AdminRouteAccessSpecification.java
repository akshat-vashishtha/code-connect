package com.codeconnect.gateway.infrastructure.security.specification.impl;

import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.infrastructure.config.properties.GatewayProperties;
import com.codeconnect.gateway.infrastructure.security.specification.RouteAccessSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Specification governing access to admin-only route boundaries (/api/v1/admin/**).
 * Authorizes only users bearing the ROLE_ADMIN role.
 */
@Component
@RequiredArgsConstructor
public class AdminRouteAccessSpecification implements RouteAccessSpecification {

    private final GatewayProperties gatewayProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(String path) {
        return pathMatcher.match(gatewayProperties.adminPathPattern(), path);
    }

    @Override
    public boolean isAuthorized(UserRole role) {
        return UserRole.ROLE_ADMIN == role;
    }

    @Override
    public boolean isProtected() {
        return true;
    }
}
