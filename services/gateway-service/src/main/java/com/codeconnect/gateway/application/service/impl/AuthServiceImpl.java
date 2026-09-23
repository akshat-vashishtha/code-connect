package com.codeconnect.gateway.application.service.impl;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.application.service.AuthService;
import com.codeconnect.gateway.infrastructure.client.UserServiceClient;
import com.codeconnect.gateway.infrastructure.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * High-level business facade orchestrating edge authentication, user onboarding,
 * and session lifecycle workflows. Delegates user persistence and verification
 * to user-service via UserServiceClient and manages Redis sessions via SessionManager.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final SessionManager sessionManager;

    @Override
    public Mono<UserResponse> signup(SignupRequest request, WebSession webSession) {
        log.info("Delegating signup request for email={} to user-service", request.email());

        return userServiceClient.register(request)
            .flatMap(user -> sessionManager.establishSession(user, webSession)
                .thenReturn(user));
    }

    @Override
    public Mono<UserResponse> login(LoginRequest request, WebSession webSession) {
        log.info("Delegating login request for email={} to user-service", request.email());

        return userServiceClient.authenticate(request)
            .flatMap(user -> sessionManager.establishSession(user, webSession)
                .thenReturn(user));
    }

    @Override
    public Mono<UserResponse> getCurrentUser(WebSession webSession) {
        return sessionManager.resolveUserId(webSession)
            .flatMap(userServiceClient::getUserById);
    }

    @Override
    public Mono<Void> logout(WebSession webSession) {
        return sessionManager.invalidateSession(webSession);
    }
}
