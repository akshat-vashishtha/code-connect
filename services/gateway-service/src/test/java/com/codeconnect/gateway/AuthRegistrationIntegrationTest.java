package com.codeconnect.gateway;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.enums.UserStatus;
import com.codeconnect.gateway.domain.exception.EmailAlreadyExistsException;
import com.codeconnect.gateway.infrastructure.client.UserServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class AuthRegistrationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("Should successfully register Student with ACTIVE status and return APP_SESSION cookie")
    void shouldRegisterStudentSuccessfully() {
        SignupRequest request = new SignupRequest(
            "student@codeconnect.dev",
            "SecurePass123!",
            "Aarav Sharma",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        UserResponse mockResponse = new UserResponse(
            "user-student-1",
            "student@codeconnect.dev",
            "Aarav Sharma",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userServiceClient.register(any(SignupRequest.class))).thenReturn(Mono.just(mockResponse));

        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().value("Set-Cookie", cookie -> {
                assertThat(cookie).contains("APP_SESSION=");
                assertThat(cookie).contains("HttpOnly");
                assertThat(cookie).contains("SameSite=Strict");
            })
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.email").isEqualTo("student@codeconnect.dev")
            .jsonPath("$.data.displayName").isEqualTo("Aarav Sharma")
            .jsonPath("$.data.role").isEqualTo("ROLE_STUDENT")
            .jsonPath("$.data.status").isEqualTo("ACTIVE");

        verify(userServiceClient).register(any(SignupRequest.class));
    }

    @Test
    @DisplayName("Should register Mentor with PENDING_APPROVAL status and issue session")
    void shouldRegisterMentorWithPendingApproval() {
        SignupRequest request = new SignupRequest(
            "mentor@codeconnect.dev",
            "MentorPass123!",
            "Priya Patel",
            UserRole.ROLE_MENTOR,
            "https://linkedin.com/in/priyapatel",
            "Principal Distributed Systems Architect with 10+ years experience"
        );

        UserResponse mockResponse = new UserResponse(
            "user-mentor-1",
            "mentor@codeconnect.dev",
            "Priya Patel",
            UserRole.ROLE_MENTOR,
            UserStatus.PENDING_APPROVAL,
            Instant.now()
        );

        when(userServiceClient.register(any(SignupRequest.class))).thenReturn(Mono.just(mockResponse));

        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().value("Set-Cookie", cookie -> {
                assertThat(cookie).contains("APP_SESSION=");
            })
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.email").isEqualTo("mentor@codeconnect.dev")
            .jsonPath("$.data.role").isEqualTo("ROLE_MENTOR")
            .jsonPath("$.data.status").isEqualTo("PENDING_APPROVAL");
    }

    @Test
    @DisplayName("Should reject duplicate registration with HTTP 409 Conflict ProblemDetail")
    void shouldRejectDuplicateEmailRegistration() {
        SignupRequest request = new SignupRequest(
            "duplicate@codeconnect.dev",
            "SecurePass123!",
            "Existing User",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        when(userServiceClient.register(argThat(r -> r != null && "duplicate@codeconnect.dev".equals(r.email()))))
            .thenReturn(Mono.error(new EmailAlreadyExistsException("duplicate@codeconnect.dev")));

        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Email Conflict")
            .jsonPath("$.status").isEqualTo(409);
    }
}
