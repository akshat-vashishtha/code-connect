package com.codeconnect.gateway.presentation.controller;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.ApiResponse;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * REST controller boundary for user registration and authentication gatekeeping.
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
}
