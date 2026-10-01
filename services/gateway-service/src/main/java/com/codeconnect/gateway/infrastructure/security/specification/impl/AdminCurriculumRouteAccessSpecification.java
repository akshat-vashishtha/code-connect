package com.codeconnect.gateway.infrastructure.security.specification.impl;

import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.infrastructure.config.properties.GatewayProperties;
import com.codeconnect.gateway.infrastructure.security.specification.RouteAccessSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Specification governing admin-curriculum route boundaries (/api/v1/admin/curriculum/**).
 * These routes allow both ROLE_ADMIN and ROLE_MENTOR for curriculum content management.
 */
@Component
@RequiredArgsConstructor
public class AdminCurriculumRouteAccessSpecification implements RouteAccessSpecification {

    private final GatewayProperties gatewayProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(String path) {
        return pathMatcher.match(gatewayProperties.adminCurriculumPathPattern(), path);
    }

    @Override
    public boolean isAuthorized(UserRole role) {
        return UserRole.ROLE_ADMIN == role || UserRole.ROLE_MENTOR == role;
    }

    @Override
    public boolean isProtected() {
        return true;
    }
}
