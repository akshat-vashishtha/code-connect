package com.codeconnect.gateway.presentation.controller;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.ApiResponse;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * REST controller boundary for user registration, direct credential login,
 * identity retrieval, and session termination.
 * Ultra-thin: strictly validates request records and delegates to AuthService.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> signup(
        @Valid @RequestBody SignupRequest request,
        WebSession webSession
    ) {
        return authService.signup(request, webSession)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registration successful", response)));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> login(
        @Valid @RequestBody LoginRequest request,
        WebSession webSession
    ) {
        return authService.login(request, webSession)
            .map(response -> ResponseEntity.ok(ApiResponse.ok("Login successful", response)));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getCurrentUser(WebSession webSession) {
        return authService.getCurrentUser(webSession)
            .map(response -> ResponseEntity.ok(ApiResponse.ok("Current user profile retrieved", response)));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<ApiResponse<Void>>> logout(WebSession webSession) {
        return authService.logout(webSession)
            .thenReturn(ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null)));
    }
}
