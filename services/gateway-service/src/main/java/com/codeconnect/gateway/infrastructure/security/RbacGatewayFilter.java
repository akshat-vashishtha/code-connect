package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * Perimeter reactive security filter enforcing dual-layer Role-Based Access Control (RBAC).
 * Acts as an orchestrating facade delegating to focused single-responsibility collaborators:
 * - {@link RbacAccessDecisionManager}: route protection and role authorization policy evaluation
 * - {@link DownstreamHeaderEnricher}: request header mutation for downstream microservices
 * - {@link ReactiveProblemResponseWriter}: RFC 7807 ProblemDetail serialization and response writing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacGatewayFilter implements WebFilter, Ordered {

    private static final int FILTER_ORDER = -100;

    private final RbacAccessDecisionManager accessDecisionManager;
    private final DownstreamHeaderEnricher downstreamHeaderEnricher;
    private final ReactiveProblemResponseWriter responseWriter;

    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!accessDecisionManager.isProtected(path)) {
            return chain.filter(exchange);
        }

        return exchange.getSession()
            .flatMap(session -> processProtectedRequest(exchange, chain, session, path));
    }

    private Mono<Void> processProtectedRequest(
        ServerWebExchange exchange,
        WebFilterChain chain,
        WebSession session,
        String path
    ) {
        String role = session.getAttribute(SessionManager.ATTR_USER_ROLE);
        if (!accessDecisionManager.isAuthorized(path, role)) {
            return rejectUnauthorized(exchange, path, role);
        }

        ServerHttpRequest enrichedRequest = downstreamHeaderEnricher.enrich(exchange.getRequest(), session, role);
        return chain.filter(exchange.mutate().request(enrichedRequest).build());
    }

    private Mono<Void> rejectUnauthorized(ServerWebExchange exchange, String path, String role) {
        log.warn("RBAC Access Denied: User role={} attempted to access path={}", role, path);
        String detail = role == null
            ? "Authentication required with appropriate role permissions"
            : "Insufficient role permissions for requested resource";
        return responseWriter.writeForbiddenResponse(exchange.getResponse(), detail);
    }
}
