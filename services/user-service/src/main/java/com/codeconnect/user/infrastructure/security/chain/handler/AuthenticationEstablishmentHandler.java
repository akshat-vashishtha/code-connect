package com.codeconnect.user.infrastructure.security.chain.handler;

import com.codeconnect.user.infrastructure.security.chain.AbstractSecurityValidationHandler;
import com.codeconnect.user.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * CoR Handler: establishes the Spring Security context from validated request headers.
 * This is the terminal handler in the pipeline — always the last in the chain.
 */
@Slf4j
@Component
@Order(3)
public class AuthenticationEstablishmentHandler extends AbstractSecurityValidationHandler {

    @Override
    public void validate(HttpServletRequest request) {
        String userId = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ID),    "");
        String role   = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ROLE),  "");
        String email  = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_EMAIL), "");

        List<SimpleGrantedAuthority> authorities = resolveAuthorities(role);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userId, email, authorities)
        );
        log.debug("Internal security context established for userId={} role={}", userId, role);
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(String role) {
        if (role.isBlank()) {
            return Collections.emptyList();
        }
        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(normalizedRole));
    }
}
