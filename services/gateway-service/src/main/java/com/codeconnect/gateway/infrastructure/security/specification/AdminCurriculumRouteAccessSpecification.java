package com.codeconnect.gateway.infrastructure.security.specification;

import com.codeconnect.gateway.domain.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Specification governing admin-curriculum route boundaries (/api/v1/admin/curriculum/**).
 * These routes allow both ROLE_ADMIN and ROLE_MENTOR for curriculum content management.
 */
@Component
public class AdminCurriculumRouteAccessSpecification implements RouteAccessSpecification {

    private static final String ADMIN_CURRICULUM_PATTERN = "/api/v1/admin/curriculum/**";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(String path) {
        return pathMatcher.match(ADMIN_CURRICULUM_PATTERN, path);
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
