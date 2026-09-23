package com.codeconnect.gateway.infrastructure.security;

import com.codeconnect.gateway.infrastructure.config.GatewayProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Writes RFC 7807 Problem Details reactively to HTTP response streams.
 * Encapsulates JSON serialization, status code setting, and DataBuffer wrapping.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveProblemResponseWriter {

    private final ObjectMapper objectMapper;
    private final GatewayProperties gatewayProperties;

    public Mono<Void> writeForbiddenResponse(ServerHttpResponse response, String detail) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        byte[] bodyBytes = serializeForbiddenProblem(detail);
        DataBuffer buffer = response.bufferFactory().wrap(bodyBytes);
        return response.writeWith(Mono.just(buffer));
    }

    private byte[] serializeForbiddenProblem(String detail) {
        String forbiddenUri = gatewayProperties.forbiddenErrorUri();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, detail);
        problem.setType(URI.create(forbiddenUri));
        problem.setTitle("Access Denied");
        problem.setProperty("timestamp", Instant.now());

        try {
            return objectMapper.writeValueAsBytes(problem);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize RFC 7807 ProblemDetail, using fallback bytes", ex);
            return ("{\"type\":\"" + forbiddenUri + "\",\"title\":\"Access Denied\",\"status\":403,\"detail\":\"" + detail + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        }
    }
}
