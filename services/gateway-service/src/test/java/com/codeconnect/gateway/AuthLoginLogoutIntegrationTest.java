package com.codeconnect.gateway;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.application.dto.response.UserResponse;
import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.enums.UserStatus;
import com.codeconnect.gateway.domain.exception.InvalidCredentialsException;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class AuthLoginLogoutIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("Should successfully login active student and issue APP_SESSION cookie")
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginRequest request = new LoginRequest("student@codeconnect.dev", "StudentPass123!");
        UserResponse studentResponse = new UserResponse(
            "student-123",
            "student@codeconnect.dev",
            "Active Student",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userServiceClient.authenticate(argThat(r -> r != null && "student@codeconnect.dev".equals(r.email()))))
            .thenReturn(Mono.just(studentResponse));

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                assertThat(cookie).contains("APP_SESSION=");
                assertThat(cookie).contains("HttpOnly");
                assertThat(cookie).contains("SameSite=Strict");
            })
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.email").isEqualTo("student@codeconnect.dev")
            .jsonPath("$.data.role").isEqualTo("ROLE_STUDENT");
    }

    @Test
    @DisplayName("Should login pending mentor and return PENDING_APPROVAL status in envelope")
    void shouldLoginPendingMentorWithPendingApprovalStatus() {
        LoginRequest request = new LoginRequest("pending-mentor@codeconnect.dev", "MentorPass123!");
        UserResponse mentorResponse = new UserResponse(
            "mentor-456",
            "pending-mentor@codeconnect.dev",
            "Pending Mentor",
            UserRole.ROLE_MENTOR,
            UserStatus.PENDING_APPROVAL,
            Instant.now()
        );

        when(userServiceClient.authenticate(argThat(r -> r != null && "pending-mentor@codeconnect.dev".equals(r.email()))))
            .thenReturn(Mono.just(mentorResponse));

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                assertThat(cookie).contains("APP_SESSION=");
            })
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.role").isEqualTo("ROLE_MENTOR")
            .jsonPath("$.data.status").isEqualTo("PENDING_APPROVAL");
    }

    @Test
    @DisplayName("Should reject invalid password with 401 Unauthorized ProblemDetail")
    void shouldRejectInvalidPassword() {
        LoginRequest request = new LoginRequest("student@codeconnect.dev", "WrongPassword!");

        when(userServiceClient.authenticate(argThat(r -> r != null && "student@codeconnect.dev".equals(r.email()))))
            .thenReturn(Mono.error(new InvalidCredentialsException()));

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Invalid Credentials")
            .jsonPath("$.status").isEqualTo(401);
    }


    @Test
    @DisplayName("Should successfully retrieve current user profile from active session via /api/v1/auth/me")
    void shouldRetrieveCurrentUserFromActiveSession() {
        LoginRequest request = new LoginRequest("me@codeconnect.dev", "Pass123!");
        UserResponse response = new UserResponse(
            "user-me-1",
            "me@codeconnect.dev",
            "Me User",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userServiceClient.authenticate(any())).thenReturn(Mono.just(response));
        when(userServiceClient.getUserById("user-me-1")).thenReturn(Mono.just(response));

        AtomicReference<String> sessionCookie = new AtomicReference<>();

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                String pair = cookie.split(";")[0];
                sessionCookie.set(pair.substring("APP_SESSION=".length()));
            });

        webTestClient.get()
            .uri("/api/v1/auth/me")
            .cookie("APP_SESSION", sessionCookie.get())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.email").isEqualTo("me@codeconnect.dev")
            .jsonPath("$.data.displayName").isEqualTo("Me User");
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for /api/v1/auth/me when no session cookie is provided")
    void shouldRejectMeEndpointWithoutSession() {
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Unauthorized")
            .jsonPath("$.status").isEqualTo(401);
    }

    @Test
    @DisplayName("Should terminate session on /logout so subsequent /me returns 401")
    void shouldTerminateSessionOnLogout() {
        LoginRequest request = new LoginRequest("logout@codeconnect.dev", "Pass123!");
        UserResponse response = new UserResponse(
            "user-logout-1",
            "logout@codeconnect.dev",
            "Logout User",
            UserRole.ROLE_STUDENT,
            UserStatus.ACTIVE,
            Instant.now()
        );

        when(userServiceClient.authenticate(any())).thenReturn(Mono.just(response));
        when(userServiceClient.getUserById("user-logout-1")).thenReturn(Mono.just(response));

        AtomicReference<String> sessionCookie = new AtomicReference<>();

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                String pair = cookie.split(";")[0];
                sessionCookie.set(pair.substring("APP_SESSION=".length()));
            });

        webTestClient.post()
            .uri("/api/v1/auth/logout")
            .cookie("APP_SESSION", sessionCookie.get())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true);

        webTestClient.get()
            .uri("/api/v1/auth/me")
            .cookie("APP_SESSION", sessionCookie.get())
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
