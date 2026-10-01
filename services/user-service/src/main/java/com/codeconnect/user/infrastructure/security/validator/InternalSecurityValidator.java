package com.codeconnect.user.infrastructure.security.validator;

import com.codeconnect.user.infrastructure.security.InternalSecurityHeaders;
import com.codeconnect.user.infrastructure.security.hmac.HmacSignatureVerifier;
import com.codeconnect.user.infrastructure.security.token.InternalAuthenticationTokenFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Facade orchestrating the user-service Zero-Trust Tier-2 security validation pipeline.
 *
 * <p>This class is a pure orchestrator operating at a Single Level of Abstraction (SLAP).
 * It contains no validation logic of its own — each concern is handled by a dedicated,
 * single-responsibility collaborator:
 *
 * <ol>
 *   <li>{@link RequestTimestampValidator} — timestamp format parsing and clock-skew enforcement</li>
 *   <li>{@link HmacSignatureVerifier} — HMAC-SHA256 canonical payload computation and comparison</li>
 *   <li>{@link InternalAuthenticationTokenFactory} — {@code Authentication} token assembly from headers</li>
 * </ol>
 *
 * <p>Any collaborator throws a Spring Security {@code AuthenticationException} on failure,
 * which the calling {@code InternalAuthenticationFilter} catches to write a structured JSON 401.
 */
@Component
@RequiredArgsConstructor
public class InternalSecurityValidator {

    private final RequestTimestampValidator          timestampValidator;
    private final HmacSignatureVerifier              signatureVerifier;
    private final InternalAuthenticationTokenFactory tokenFactory;

    /**
     * Returns {@code true} if the request carries the minimum set of Tier-2 internal
     * security headers required to attempt HMAC validation.
     */
    public boolean hasInternalSecurityHeaders(HttpServletRequest request) {
        return request.getHeader(InternalSecurityHeaders.TIMESTAMP) != null
            && request.getHeader(InternalSecurityHeaders.SIGNATURE) != null;
    }

    /**
     * Runs the full validation pipeline and returns an authenticated {@link Authentication} token.
     * Delegates each step to a focused collaborator; throws an {@code AuthenticationException} on failure.
     */
    public Authentication authenticate(HttpServletRequest request) {
        timestampValidator.validate(request);
        signatureVerifier.verify(request);
        return tokenFactory.createFrom(request);
    }
}
