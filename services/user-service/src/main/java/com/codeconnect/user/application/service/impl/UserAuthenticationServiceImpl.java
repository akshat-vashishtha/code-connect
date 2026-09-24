package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.dto.response.UserResponse;
import com.codeconnect.user.application.mapper.UserMapper;
import com.codeconnect.user.application.service.UserAuthenticationService;
import com.codeconnect.user.application.service.UserCredentialAuthenticator;
import com.codeconnect.user.application.service.UserProfileResolver;
import com.codeconnect.user.application.service.UserRegistrationManager;
import com.codeconnect.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * High-level facade service orchestrating user registration, credential authentication,
 * and user profile resolution workflows.
 * Adheres strictly to the Single Responsibility Principle (SRP) and Single Level of Abstraction (SLAP)
 * by delegating to focused collaborator components.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRegistrationManager userRegistrationManager;
    private final UserCredentialAuthenticator userCredentialAuthenticator;
    private final UserProfileResolver userProfileResolver;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        log.info("Processing user registration for email={} role={}", request.email(), request.role());
        User user = userRegistrationManager.register(request);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse authenticate(UserAuthenticationRequest request) {
        log.info("Processing user authentication for email={}", request.email());
        User user = userCredentialAuthenticator.authenticate(request);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        log.debug("Processing user lookup for userId={}", userId);
        User user = userProfileResolver.resolveById(userId);
        return userMapper.toResponse(user);
    }
}
