package com.codeconnect.user.application.service.collaborator.auth;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.exception.AccountBannedException;
import com.codeconnect.user.domain.exception.InvalidCredentialsException;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCredentialAuthenticatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCredentialAuthenticator authenticator;

    @Test
    @DisplayName("Should successfully authenticate valid credentials for active account")
    void shouldAuthenticateValidCredentials() {
        UserAuthenticationRequest request = new UserAuthenticationRequest(
            "student@codeconnect.dev",
            "ValidPassword123!"
        );

        User mockUser = User.createStudent("student@codeconnect.dev", "hashedPassword", "Aarav");
        when(userRepository.findByEmail("student@codeconnect.dev")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("ValidPassword123!", "hashedPassword")).thenReturn(true);

        User authenticated = authenticator.authenticate(request);

        assertThat(authenticated).isEqualTo(mockUser);
        assertThat(authenticated.getEmail()).isEqualTo("student@codeconnect.dev");
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when email is not found")
    void shouldThrowWhenEmailNotFound() {
        UserAuthenticationRequest request = new UserAuthenticationRequest(
            "unknown@codeconnect.dev",
            "AnyPassword"
        );

        when(userRepository.findByEmail("unknown@codeconnect.dev")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticator.authenticate(request))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password does not match")
    void shouldThrowWhenPasswordMismatch() {
        UserAuthenticationRequest request = new UserAuthenticationRequest(
            "student@codeconnect.dev",
            "WrongPassword"
        );

        User mockUser = User.createStudent("student@codeconnect.dev", "hashedPassword", "Aarav");
        when(userRepository.findByEmail("student@codeconnect.dev")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authenticator.authenticate(request))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Should throw AccountBannedException when user account status is BANNED")
    void shouldThrowWhenAccountBanned() {
        UserAuthenticationRequest request = new UserAuthenticationRequest(
            "banned@codeconnect.dev",
            "ValidPassword123!"
        );

        User mockUser = User.builder()
            .id("banned-id")
            .email("banned@codeconnect.dev")
            .passwordHash("hashedPassword")
            .role(UserRole.ROLE_STUDENT)
            .status(UserStatus.BANNED)
            .build();

        when(userRepository.findByEmail("banned@codeconnect.dev")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("ValidPassword123!", "hashedPassword")).thenReturn(true);

        assertThatThrownBy(() -> authenticator.authenticate(request))
            .isInstanceOf(AccountBannedException.class);
    }
}
