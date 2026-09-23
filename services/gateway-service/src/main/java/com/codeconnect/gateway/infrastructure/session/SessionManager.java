package com.codeconnect.gateway.infrastructure.session;

import com.codeconnect.gateway.domain.exception.UnauthorizedException;
import com.codeconnect.gateway.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * Infrastructure manager for reactive Redis WebSession lifecycle operations.
 * Handles attribute hydration, session ID rotation, and session invalidation.
 */
@Slf4j
@Component
public class SessionManager {

    public static final String ATTR_USER_ID = "USER_ID";
    public static final String ATTR_USER_EMAIL = "USER_EMAIL";
    public static final String ATTR_USER_ROLE = "USER_ROLE";
    public static final String ATTR_USER_STATUS = "USER_STATUS";

    public Mono<Void> establishSession(User user, WebSession webSession) {
        return Mono.fromRunnable(() -> {
            webSession.getAttributes().put(ATTR_USER_ID, user.getId());
            webSession.getAttributes().put(ATTR_USER_EMAIL, user.getEmail());
            webSession.getAttributes().put(ATTR_USER_ROLE, user.getRole().name());
            webSession.getAttributes().put(ATTR_USER_STATUS, user.getStatus().name());
        })
        .then(webSession.changeSessionId())
        .then(webSession.save());
    }

    public Mono<String> resolveUserId(WebSession webSession) {
        Object userId = webSession.getAttribute(ATTR_USER_ID);
        if (userId instanceof String strId && !strId.isBlank()) {
            return Mono.just(strId);
        }
        return Mono.error(new UnauthorizedException("Active authentication session required"));
    }

    public Mono<Void> invalidateSession(WebSession webSession) {
        return webSession.invalidate();
    }
}
