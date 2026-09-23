package com.codeconnect.user.presentation.controller;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.dto.response.ApiResponse;
import com.codeconnect.user.application.dto.response.UserResponse;
import com.codeconnect.user.application.service.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing internal REST endpoints for gateway authentication and registration delegation.
 */
@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalAuthController {

    private final UserAuthenticationService userAuthenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userAuthenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("User registered successfully", response));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<UserResponse>> authenticate(@Valid @RequestBody UserAuthenticationRequest request) {
        UserResponse response = userAuthenticationService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.ok("Authentication successful", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse response = userAuthenticationService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User retrieved successfully", response));
    }
}
