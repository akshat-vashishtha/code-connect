package com.codeconnect.user.application.service;

import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationManagerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MentorApprovalManager mentorApprovalManager;

    @InjectMocks
    private UserRegistrationManager userRegistrationManager;

    @Test
    @DisplayName("Should successfully register a Student with ACTIVE status and encoded password")
    void shouldRegisterStudentSuccessfully() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "student@codeconnect.dev",
            "RawPassword123!",
            "John Doe",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        when(userRepository.existsByEmail("student@codeconnect.dev")).thenReturn(false);
        when(passwordEncoder.encode("RawPassword123!")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userRegistrationManager.register(request);

        assertThat(registeredUser.getEmail()).isEqualTo("student@codeconnect.dev");
        assertThat(registeredUser.getPasswordHash()).isEqualTo("hashedPassword");
        assertThat(registeredUser.getRole()).isEqualTo(UserRole.ROLE_STUDENT);
        assertThat(registeredUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

        verify(userRepository).save(any(User.class));
        verify(mentorApprovalManager, never()).createPendingApplication(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should successfully register a Mentor with PENDING status and create approval application")
    void shouldRegisterMentorSuccessfully() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "mentor@codeconnect.dev",
            "RawPassword123!",
            "Jane Mentor",
            UserRole.ROLE_MENTOR,
            "https://linkedin.com/in/janementor",
            "10 years Java architect"
        );

        when(userRepository.existsByEmail("mentor@codeconnect.dev")).thenReturn(false);
        when(passwordEncoder.encode("RawPassword123!")).thenReturn("hashedPassword");
        User savedMentor = User.builder()
            .id("generated-user-id")
            .email("mentor@codeconnect.dev")
            .passwordHash("hashedPassword")
            .displayName("Jane Mentor")
            .role(UserRole.ROLE_MENTOR)
            .status(UserStatus.PENDING_APPROVAL)
            .build();
        when(userRepository.save(any(User.class))).thenReturn(savedMentor);

        User registeredUser = userRegistrationManager.register(request);

        assertThat(registeredUser.getEmail()).isEqualTo("mentor@codeconnect.dev");
        assertThat(registeredUser.getRole()).isEqualTo(UserRole.ROLE_MENTOR);
        assertThat(registeredUser.getStatus()).isEqualTo(UserStatus.PENDING_APPROVAL);

        verify(userRepository).save(any(User.class));
        verify(mentorApprovalManager).createPendingApplication(
            eq("generated-user-id"),
            eq("mentor@codeconnect.dev"),
            eq("https://linkedin.com/in/janementor"),
            eq("10 years Java architect")
        );
    }

    @Test
    @DisplayName("Should reject registration when email already exists")
    void shouldRejectWhenEmailAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "duplicate@codeconnect.dev",
            "RawPassword123!",
            "Duplicate User",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        when(userRepository.existsByEmail("duplicate@codeconnect.dev")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationManager.register(request))
            .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }
}
