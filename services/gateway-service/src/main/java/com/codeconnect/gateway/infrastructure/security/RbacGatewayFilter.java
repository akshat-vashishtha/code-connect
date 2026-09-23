package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.session.SessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Perimeter reactive security filter enforcing dual-layer Role-Based Access Control (RBAC).
 * Intercepts protected URI path prefixes:
 * - /api/v1/admin/**: requires ROLE_ADMIN
 * - /api/v1/mentor/**: requires ROLE_MENTOR or ROLE_ADMIN
 * 
 * Rejects unauthorized entities with RFC 7807 Problem Details (HTTP 403 Forbidden).
 * On success, mutates request headers (X-User-Id, X-User-Role, X-User-Email) for downstream microservices.
 */
@Slf4j
@Component
public class RbacGatewayFilter implements WebFilter, Ordered {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_MENTOR = "ROLE_MENTOR";
    private static final URI FORBIDDEN_TYPE = URI.create("https://codeconnect.dev/errors/forbidden");

    private final ObjectMapper objectMapper;

    public RbacGatewayFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return -100; // High precedence to execute before routing and handler execution
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        boolean isAdminPath = path.startsWith("/api/v1/admin");
        boolean isMentorPath = path.startsWith("/api/v1/mentor");

        if (!isAdminPath && !isMentorPath) {
            return chain.filter(exchange);
        }

        return exchange.getSession().flatMap(session -> {
            String role = session.getAttribute(SessionManager.ATTR_USER_ROLE);
            String userId = session.getAttribute(SessionManager.ATTR_USER_ID);
            String email = session.getAttribute(SessionManager.ATTR_USER_EMAIL);

            if (role == null) {
                log.warn("RBAC Access Denied: Unauthenticated attempt to access protected path={}", path);
                return writeForbiddenResponse(exchange, "Authentication required with appropriate role permissions");
            }

            if (isAdminPath && !ROLE_ADMIN.equals(role)) {
                log.warn("RBAC Access Denied: User role={} attempted to access admin path={}", role, path);
                return writeForbiddenResponse(exchange, "Insufficient role permissions for requested resource");
            }

            if (isMentorPath && !ROLE_MENTOR.equals(role) && !ROLE_ADMIN.equals(role)) {
                log.warn("RBAC Access Denied: User role={} attempted to access mentor path={}", role, path);
                return writeForbiddenResponse(exchange, "Insufficient role permissions for requested resource");
            }

            // Propagate user identity headers to downstream microservices
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId != null ? userId : "")
                .header("X-User-Role", role)
                .header("X-User-Email", email != null ? email : "")
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        });
    }

    private Mono<Void> writeForbiddenResponse(ServerWebExchange exchange, String detail) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, detail);
        problem.setType(FORBIDDEN_TYPE);
        problem.setTitle("Access Denied");
        problem.setProperty("timestamp", Instant.now());

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(problem);
        } catch (JsonProcessingException e) {
            bytes = ("{\"type\":\"https://codeconnect.dev/errors/forbidden\",\"title\":\"Access Denied\",\"status\":403,\"detail\":\"" + detail + "\"}").getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
