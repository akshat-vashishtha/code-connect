package com.codeconnect.user.application.service.collaborator.auth;

import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileResolverTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileResolver resolver;

    @Test
    @DisplayName("Should successfully resolve user by ID")
    void shouldResolveUserById() {
        User user = User.builder()
            .id("user-123")
            .email("user@codeconnect.dev")
            .passwordHash("hash")
            .displayName("Alice")
            .role(UserRole.ROLE_STUDENT)
            .status(UserStatus.ACTIVE)
            .build();

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        User resolved = resolver.resolveById("user-123");

        assertThat(resolved).isEqualTo(user);
        assertThat(resolved.getId()).isEqualTo("user-123");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user ID is not found")
    void shouldThrowWhenUserIdNotFound() {
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolveById("non-existent"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("non-existent");
    }
}
