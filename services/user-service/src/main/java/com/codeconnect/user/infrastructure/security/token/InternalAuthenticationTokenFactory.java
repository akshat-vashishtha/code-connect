package com.codeconnect.user.infrastructure.security.token;

import com.codeconnect.user.infrastructure.security.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Factory that constructs a fully-authenticated Spring Security {@link Authentication} token
 * from pre-validated internal request headers.
 *
 * <p>Single Responsibility: this class does exactly one thing — given a request whose headers
 * have already passed timestamp and HMAC signature validation, extract the user identity
 * ({@code X-User-Id}, {@code X-User-Role}, {@code X-User-Email}) and assemble a
 * {@link UsernamePasswordAuthenticationToken} ready to be stored in the
 * {@code SecurityContextHolder}.
 *
 * <p>Only called after all security validations have already succeeded.
 */
@Slf4j
@Component
public class InternalAuthenticationTokenFactory {

    /**
     * Creates an authenticated {@link Authentication} token from validated request headers.
     *
     * @param request the inbound request with pre-validated Zero-Trust headers
     * @return a fully-authenticated {@link UsernamePasswordAuthenticationToken}
     */
    public Authentication createFrom(HttpServletRequest request) {
        String userId = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ID),    "");
        String role   = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_ROLE),  "");
        String email  = Objects.requireNonNullElse(request.getHeader(InternalSecurityHeaders.USER_EMAIL), "");

        List<SimpleGrantedAuthority> authorities = resolveAuthorities(role);

        log.debug("Internal authentication token created for userId={} role={}", userId, role);

        return new UsernamePasswordAuthenticationToken(userId, email, authorities);
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(String role) {
        if (role.isBlank()) {
            return Collections.emptyList();
        }
        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(normalizedRole));
    }
}
