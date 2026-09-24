package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.application.dto.response.UserResponse;
import com.codeconnect.user.application.mapper.UserMapper;
import com.codeconnect.user.application.service.collaborator.auth.UserCredentialAuthenticator;
import com.codeconnect.user.application.service.collaborator.auth.UserProfileResolver;
import com.codeconnect.user.application.service.collaborator.auth.UserRegistrationManager;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceImplTest {

    @Mock
    private UserRegistrationManager userRegistrationManager;

    @Mock
    private UserCredentialAuthenticator userCredentialAuthenticator;

    @Mock
    private UserProfileResolver userProfileResolver;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserAuthenticationServiceImpl service;

    @Test
    @DisplayName("Should orchestrate registration by delegating to UserRegistrationManager and UserMapper")
    void shouldOrchestrateRegistration() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "student@codeconnect.dev",
            "Pass123!",
            "Student",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        User mockUser = User.createStudent("student@codeconnect.dev", "hash", "Student");
        UserResponse expectedResponse = new UserResponse(
            "id-1",
            "student@codeconnect.dev",
            "Student",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userRegistrationManager.register(request)).thenReturn(mockUser);
        when(userMapper.toResponse(mockUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = service.register(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRegistrationManager).register(request);
        verify(userMapper).toResponse(mockUser);
    }

    @Test
    @DisplayName("Should orchestrate authentication by delegating to UserCredentialAuthenticator and UserMapper")
    void shouldOrchestrateAuthentication() {
        UserAuthenticationRequest request = new UserAuthenticationRequest(
            "student@codeconnect.dev",
            "Pass123!"
        );

        User mockUser = User.createStudent("student@codeconnect.dev", "hash", "Student");
        UserResponse expectedResponse = new UserResponse(
            "id-1",
            "student@codeconnect.dev",
            "Student",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userCredentialAuthenticator.authenticate(request)).thenReturn(mockUser);
        when(userMapper.toResponse(mockUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = service.authenticate(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userCredentialAuthenticator).authenticate(request);
        verify(userMapper).toResponse(mockUser);
    }

    @Test
    @DisplayName("Should orchestrate user profile lookup by delegating to UserProfileResolver and UserMapper")
    void shouldOrchestrateUserLookup() {
        User mockUser = User.createStudent("student@codeconnect.dev", "hash", "Student");
        UserResponse expectedResponse = new UserResponse(
            "user-1",
            "student@codeconnect.dev",
            "Student",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userProfileResolver.resolveById("user-1")).thenReturn(mockUser);
        when(userMapper.toResponse(mockUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = service.getUserById("user-1");

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userProfileResolver).resolveById("user-1");
        verify(userMapper).toResponse(mockUser);
    }
}
