package com.codeconnect.user.infrastructure.security.handler;

import com.codeconnect.user.application.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * {@link AccessDeniedHandler} that writes a structured JSON {@code 403 Forbidden}
 * response when an authenticated principal lacks the required authority.
 *
 * <p>Named {@code Json*} to precisely communicate the differentiating behaviour —
 * producing a JSON body instead of Spring Security's default HTML error page.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
        HttpServletRequest  request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        log.warn("Access denied for URI={}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> body = ApiResponse.failure("Access denied: " + accessDeniedException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
