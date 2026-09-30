package com.codeconnect.user.infrastructure.security.chain.handler;

import com.codeconnect.user.infrastructure.config.properties.InternalSecurityProperties;
import com.codeconnect.user.infrastructure.security.chain.AbstractSecurityValidationHandler;
import com.codeconnect.user.infrastructure.security.chain.exception.SecurityValidationException;
import com.codeconnect.user.infrastructure.security.chain.header.InternalSecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * CoR Handler: validates the request timestamp against the permitted clock-skew window.
 * Throws IllegalStateException on failure to trigger a 403 response in the filter.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TimestampValidationHandler extends AbstractSecurityValidationHandler {

    private final InternalSecurityProperties securityProperties;

    @Override
    public void validate(HttpServletRequest request) {
        String timestampStr = request.getHeader(InternalSecurityHeaders.TIMESTAMP);
        assertTimestampFormat(timestampStr, request.getRequestURI());
        assertClockSkewAcceptable(Long.parseLong(timestampStr), request.getRequestURI());
        passToNext(request);
    }

    private void assertTimestampFormat(String timestampStr, String path) {
        try {
            Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            log.warn("Security validation failed: Invalid timestamp format for path={}", path);
            throw new SecurityValidationException("Security verification failed: Invalid timestamp format");
        }
    }

    private void assertClockSkewAcceptable(long timestamp, String path) {
        long maxSkewMs = securityProperties.maxClockSkewSeconds() * 1000L;
        if (Math.abs(System.currentTimeMillis() - timestamp) > maxSkewMs) {
            log.warn("Security validation failed: Clock skew exceeded for path={}", path);
            throw new SecurityValidationException("Security verification failed: Stale request timestamp");
        }
    }
}
