package com.codeconnect.user.infrastructure.security.principal;

import com.codeconnect.user.domain.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Spring Security-backed implementation of {@link SecurityContextAccessor}.
 * Encapsulates all direct access to the static {@code SecurityContextHolder} within the
 * infrastructure layer — keeping the application layer decoupled from Spring Security internals.
 */
@Component
public class SpringSecurityContextAccessor implements SecurityContextAccessor {

    @Override
    public Optional<String> resolveAuthenticatedUserId() {
        return Optional.ofNullable(resolveAuthentication())
            .filter(Authentication::isAuthenticated)
            .filter(auth -> !isAnonymous(auth))
            .map(Authentication::getName);
    }

    @Override
    public Optional<String> resolveAuthenticatedUserEmail() {
        return Optional.ofNullable(resolveAuthentication())
            .filter(Authentication::isAuthenticated)
            .filter(auth -> !isAnonymous(auth))
            .map(auth -> auth.getCredentials() != null ? auth.getCredentials().toString() : null);
    }

    @Override
    public boolean hasRole(UserRole role) {
        Authentication auth = resolveAuthentication();
        if (auth == null || !auth.isAuthenticated() || isAnonymous(auth)) {
            return false;
        }
        String normalizedRole = "ROLE_" + role.name();
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(role.name()) || a.getAuthority().equals(normalizedRole));
    }

    @Override
    public boolean isAuthenticated() {
        Authentication auth = resolveAuthentication();
        return auth != null && auth.isAuthenticated() && !isAnonymous(auth);
    }

    private Authentication resolveAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAnonymous(Authentication auth) {
        return "anonymous".equals(auth.getPrincipal()) || "anonymousUser".equals(auth.getPrincipal());
    }
}
