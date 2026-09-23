package com.codeconnect.user.application.service;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.dto.response.UserResponse;

/**
 * Service interface defining user registration, credential authentication,
 * and user profile resolution workflows.
 */
public interface UserAuthenticationService {

    UserResponse register(UserRegistrationRequest request);

    UserResponse authenticate(UserAuthenticationRequest request);

    UserResponse getUserById(String userId);
}
