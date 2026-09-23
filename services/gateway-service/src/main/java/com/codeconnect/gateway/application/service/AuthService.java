package com.codeconnect.gateway.application.service;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * Authentication and registration business service contract.
 */
public interface AuthService {

    /**
     * Registers a new user account with role-directed activation,
     * hashes credentials, stores audit requests if applicable, and initiates a Redis session.
     *
     * @param request the validated signup payload
     * @param webSession the active reactive WebSession
     * @return Mono emitting the sanitized UserResponse
     */
    Mono<UserResponse> signup(SignupRequest request, WebSession webSession);
}
