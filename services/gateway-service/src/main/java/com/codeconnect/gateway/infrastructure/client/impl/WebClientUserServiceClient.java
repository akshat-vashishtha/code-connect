package com.codeconnect.gateway.infrastructure.client.impl;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.ApiResponse;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.gateway.domain.exception.InvalidCredentialsException;
import com.codeconnect.gateway.domain.exception.UnauthorizedException;
import com.codeconnect.gateway.infrastructure.client.UserServiceClient;
import com.codeconnect.gateway.infrastructure.config.GatewayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Reactive WebClient implementation communicating with user-service endpoints.
 */
@Slf4j
@Component
public class WebClientUserServiceClient implements UserServiceClient {

    private final WebClient webClient;

    public WebClientUserServiceClient(WebClient.Builder webClientBuilder, GatewayProperties gatewayProperties) {
        this.webClient = webClientBuilder.baseUrl(gatewayProperties.userServiceUri()).build();
    }

    @Override
    public Mono<UserResponse> register(SignupRequest request) {
        return webClient.post()
            .uri("/api/v1/internal/users/register")
            .bodyValue(request)
            .retrieve()
            .onStatus(status -> status.equals(HttpStatus.CONFLICT), response ->
                Mono.error(new EmailAlreadyExistsException(request.email())))
            .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
            .map(ApiResponse::data);
    }

    @Override
    public Mono<UserResponse> authenticate(LoginRequest request) {
        return webClient.post()
            .uri("/api/v1/internal/users/authenticate")
            .bodyValue(request)
            .retrieve()
            .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED), response ->
                Mono.error(new InvalidCredentialsException()))
            .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
            .map(ApiResponse::data);
    }

    @Override
    public Mono<UserResponse> getUserById(String userId) {
        return webClient.get()
            .uri("/api/v1/internal/users/{id}", userId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response ->
                Mono.error(new UnauthorizedException("User profile not found")))
            .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
            .map(ApiResponse::data);
    }
}
