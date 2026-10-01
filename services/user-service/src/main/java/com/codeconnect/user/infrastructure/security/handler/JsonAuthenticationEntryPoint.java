package com.codeconnect.user.infrastructure.security.handler;

import com.codeconnect.user.application.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * {@link AuthenticationEntryPoint} that writes a structured JSON {@code 401 Unauthorized}
 * response on every authentication failure.
 *
 * <p>Named {@code Json*} to clearly communicate what distinguishes it from Spring Security's
 * default entry point (which produces an HTML redirect to a login page). The {@code Json} prefix
 * is the precise, domain-meaningful differentiator — not the meaningless {@code Rest} prefix.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest  request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        log.warn("Unauthorized access attempt on URI={}: {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> body = ApiResponse.failure("Authentication required: " + authException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
