package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.domain.model.UserRole;
import com.codeconnect.gateway.infrastructure.config.GatewayProperties;
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
import org.springframework.web.server.WebSession;
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

    private static final String ADMIN_PATH_PREFIX = "/api/v1/admin";
    private static final String MENTOR_PATH_PREFIX = "/api/v1/mentor";

    private final ObjectMapper objectMapper;
    private final GatewayProperties gatewayProperties;

    public RbacGatewayFilter(ObjectMapper objectMapper, GatewayProperties gatewayProperties) {
        this.objectMapper = objectMapper;
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public int getOrder() {
        return -100; // High precedence to execute before routing and handler execution
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!isProtectedPath(path)) {
            return chain.filter(exchange);
        }
        return authorizeAndDispatch(exchange, chain, path);
    }

    private Mono<Void> authorizeAndDispatch(ServerWebExchange exchange, WebFilterChain chain, String path) {
        return exchange.getSession().flatMap(session -> {
            String role = session.getAttribute(SessionManager.ATTR_USER_ROLE);
            if (!isAuthorized(path, role)) {
                return rejectUnauthorized(exchange, path, role);
            }
            ServerHttpRequest mutatedRequest = buildDownstreamRequest(exchange.getRequest(), session, role);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        });
    }

    private boolean isProtectedPath(String path) {
        return path.startsWith(ADMIN_PATH_PREFIX) || path.startsWith(MENTOR_PATH_PREFIX);
    }

    private boolean isAuthorized(String path, String role) {
        if (role == null) {
            return false;
        }
        if (path.startsWith(ADMIN_PATH_PREFIX)) {
            return UserRole.ROLE_ADMIN.name().equals(role);
        }
        if (path.startsWith(MENTOR_PATH_PREFIX)) {
            return UserRole.ROLE_MENTOR.name().equals(role) || UserRole.ROLE_ADMIN.name().equals(role);
        }
        return true;
    }

    private Mono<Void> rejectUnauthorized(ServerWebExchange exchange, String path, String role) {
        log.warn("RBAC Access Denied: User role={} attempted to access path={}", role, path);
        String detail = role == null
            ? "Authentication required with appropriate role permissions"
            : "Insufficient role permissions for requested resource";
        return writeForbiddenResponse(exchange, detail);
    }

    private ServerHttpRequest buildDownstreamRequest(ServerHttpRequest request, WebSession session, String role) {
        String userId = session.getAttribute(SessionManager.ATTR_USER_ID);
        String email = session.getAttribute(SessionManager.ATTR_USER_EMAIL);

        return request.mutate()
            .header("X-User-Id", userId != null ? userId : "")
            .header("X-User-Role", role)
            .header("X-User-Email", email != null ? email : "")
            .build();
    }

    private Mono<Void> writeForbiddenResponse(ServerWebExchange exchange, String detail) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        byte[] bodyBytes = serializeProblem(detail);
        DataBuffer buffer = response.bufferFactory().wrap(bodyBytes);
        return response.writeWith(Mono.just(buffer));
    }

    private byte[] serializeProblem(String detail) {
        String forbiddenUri = gatewayProperties.errorBaseUri() + "/forbidden";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, detail);
        problem.setType(URI.create(forbiddenUri));
        problem.setTitle("Access Denied");
        problem.setProperty("timestamp", Instant.now());

        try {
            return objectMapper.writeValueAsBytes(problem);
        } catch (JsonProcessingException e) {
            return ("{\"type\":\"" + forbiddenUri + "\",\"title\":\"Access Denied\",\"status\":403,\"detail\":\"" + detail + "\"}").getBytes(StandardCharsets.UTF_8);
        }
    }
}
