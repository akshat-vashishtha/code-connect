package com.codeconnect.user.infrastructure.security;

import com.codeconnect.user.domain.enums.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpringSecurityContextAccessorTest {

    private SpringSecurityContextAccessor accessor;

    @BeforeEach
    void setUp() {
        accessor = new SpringSecurityContextAccessor();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should resolve authenticated user id and role accurately")
    void shouldResolveAuthenticatedUser() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "user-123", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> userId = accessor.resolveAuthenticatedUserId();
        assertThat(userId).isPresent().contains("user-123");
        assertThat(accessor.isAuthenticated()).isTrue();
        assertThat(accessor.hasRole(UserRole.ROLE_ADMIN)).isTrue();
        assertThat(accessor.hasRole(UserRole.ROLE_STUDENT)).isFalse();
    }

    @Test
    @DisplayName("Should return empty optional and false when unauthenticated")
    void shouldHandleUnauthenticated() {
        assertThat(accessor.resolveAuthenticatedUserId()).isEmpty();
        assertThat(accessor.isAuthenticated()).isFalse();
        assertThat(accessor.hasRole(UserRole.ROLE_ADMIN)).isFalse();
    }
}
