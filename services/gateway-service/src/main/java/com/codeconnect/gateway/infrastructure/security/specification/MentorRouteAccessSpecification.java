package com.codeconnect.gateway.infrastructure.security.specification;

import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.infrastructure.config.properties.GatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Specification governing access to mentor route boundaries (/api/v1/mentor/**).
 * Authorizes users bearing ROLE_MENTOR or ROLE_ADMIN roles.
 */
@Component
@RequiredArgsConstructor
public class MentorRouteAccessSpecification implements RouteAccessSpecification {

    private final GatewayProperties gatewayProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(String path) {
        return pathMatcher.match(gatewayProperties.mentorPathPattern(), path);
    }

    @Override
    public boolean isAuthorized(UserRole role) {
        return UserRole.ROLE_MENTOR == role || UserRole.ROLE_ADMIN == role;
    }

    @Override
    public boolean isProtected() {
        return true;
    }
}
