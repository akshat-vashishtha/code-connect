package com.codeconnect.user.infrastructure.security.validator;

import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.security.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

/**
 * Validates the {@code X-Timestamp} header against the permitted clock-skew window.
 *
 * <p>Single Responsibility: this class does exactly one thing — parse the timestamp header
 * and assert that it falls within the configured {@code maxClockSkewSeconds} window of the
 * server's current clock. Stale or malformed timestamps signal replay attacks.
 *
 * <p>Throws {@link BadCredentialsException} (a Spring Security {@code AuthenticationException})
 * on validation failure so the calling filter can route to the configured entry point.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestTimestampValidator {

    private final InternalSecurityProperties securityProperties;

    /**
     * Validates the {@code X-Timestamp} header on the given request.
     *
     * @throws BadCredentialsException if the timestamp is missing, non-numeric, or outside the skew window
     */
    public void validate(HttpServletRequest request) {
        String timestampStr = request.getHeader(InternalSecurityHeaders.TIMESTAMP);
        long   timestamp    = parseTimestamp(timestampStr, request.getRequestURI());
        assertWithinClockSkew(timestamp, request.getRequestURI());
    }

    private long parseTimestamp(String timestampStr, String path) {
        try {
            return Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            log.warn("Security validation failed: Invalid timestamp format for path={}", path);
            throw new BadCredentialsException("Security verification failed: Invalid timestamp format");
        }
    }

    private void assertWithinClockSkew(long timestamp, String path) {
        long maxSkewMs = securityProperties.maxClockSkewSeconds() * 1000L;
        if (Math.abs(System.currentTimeMillis() - timestamp) > maxSkewMs) {
            log.warn("Security validation failed: Clock skew exceeded for path={}", path);
            throw new BadCredentialsException("Security verification failed: Stale request timestamp");
        }
    }
}
