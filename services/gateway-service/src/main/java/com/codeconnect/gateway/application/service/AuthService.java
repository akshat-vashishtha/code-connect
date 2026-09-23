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

    /**
     * Authenticates existing user credentials, initiates a new Redis session,
     * and rotates the session ID to issue the APP_SESSION cookie.
     *
     * @param request validated login credentials
     * @param webSession the active reactive WebSession
     * @return Mono emitting the authenticated UserResponse
     */
    Mono<UserResponse> login(com.codeconnect.gateway.application.dto.request.LoginRequest request, WebSession webSession);

    /**
     * Resolves the currently authenticated user profile from the active WebSession.
     *
     * @param webSession the active reactive WebSession
     * @return Mono emitting the current UserResponse
     */
    Mono<UserResponse> getCurrentUser(WebSession webSession);

    /**
     * Terminates the active session in Redis and clears browser session cookies.
     *
     * @param webSession the active reactive WebSession
     * @return Mono completing when session is invalidated
     */
    Mono<Void> logout(WebSession webSession);
}
