package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.session.SessionManager;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

/**
 * Enriches downstream microservice requests with verified session identities.
 * Strictly encapsulates downstream HTTP header mutation.
 */
@Component
public class DownstreamHeaderEnricher {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_EMAIL = "X-User-Email";

    public ServerHttpRequest enrich(ServerHttpRequest request, WebSession session, String role) {
        String userId = session.getAttribute(SessionManager.ATTR_USER_ID);
        String email = session.getAttribute(SessionManager.ATTR_USER_EMAIL);

        return request.mutate()
            .header(HEADER_USER_ID, userId != null ? userId : "")
            .header(HEADER_USER_ROLE, role != null ? role : "")
            .header(HEADER_USER_EMAIL, email != null ? email : "")
            .build();
    }
}
