package com.codeconnect.gateway.infrastructure.client;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import reactor.core.publisher.Mono;

/**
 * Reactive client boundary communicating with downstream user-service.
 */
public interface UserServiceClient {

    Mono<UserResponse> register(SignupRequest request);

    Mono<UserResponse> authenticate(LoginRequest request);

    Mono<UserResponse> getUserById(String userId);
}
