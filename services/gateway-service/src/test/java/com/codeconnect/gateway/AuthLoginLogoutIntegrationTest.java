package com.codeconnect.gateway;

import com.codeconnect.gateway.application.dto.request.LoginRequest;
import com.codeconnect.gateway.domain.model.User;
import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.enums.UserStatus;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class AuthLoginLogoutIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReactiveUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();

        // Seed Active Student
        User student = User.builder()
            .email("student@codeconnect.dev")
            .passwordHash(passwordEncoder.encode("StudentPass123!"))
            .displayName("Active Student")
            .role(UserRole.ROLE_STUDENT)
            .status(UserStatus.ACTIVE)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        userRepository.save(student).block();

        // Seed Pending Mentor
        User pendingMentor = User.builder()
            .email("pending-mentor@codeconnect.dev")
            .passwordHash(passwordEncoder.encode("MentorPass123!"))
            .displayName("Pending Mentor")
            .role(UserRole.ROLE_MENTOR)
            .status(UserStatus.PENDING_APPROVAL)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        userRepository.save(pendingMentor).block();

        // Seed Banned User
        User bannedUser = User.builder()
            .email("banned@codeconnect.dev")
            .passwordHash(passwordEncoder.encode("BannedPass123!"))
            .displayName("Banned User")
            .role(UserRole.ROLE_STUDENT)
            .status(UserStatus.BANNED)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        userRepository.save(bannedUser).block();
    }

    @Test
    @DisplayName("Should successfully login active student and issue APP_SESSION cookie")
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginRequest request = new LoginRequest("student@codeconnect.dev", "StudentPass123!");

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
            .jsonPath("$.data.displayName").isEqualTo("Active Student")
            .jsonPath("$.data.role").isEqualTo("ROLE_STUDENT")
            .jsonPath("$.data.status").isEqualTo("ACTIVE")
            .jsonPath("$.data.password").doesNotExist()
            .jsonPath("$.data.passwordHash").doesNotExist();
    }

    @Test
    @DisplayName("Should allow pending mentor to login with PENDING_APPROVAL status")
    void shouldAllowPendingMentorToLoginAndReturnPendingStatus() {
        LoginRequest request = new LoginRequest("pending-mentor@codeconnect.dev", "MentorPass123!");

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
            .jsonPath("$.data.email").isEqualTo("pending-mentor@codeconnect.dev")
            .jsonPath("$.data.role").isEqualTo("ROLE_MENTOR")
            .jsonPath("$.data.status").isEqualTo("PENDING_APPROVAL");
    }

    @Test
    @DisplayName("Should reject login with non-existent email with HTTP 401 Unauthorized in RFC 7807")
    void shouldRejectLoginWithNonExistentEmailWith401Unauthorized() {
        LoginRequest request = new LoginRequest("unknown@codeconnect.dev", "AnyPassword123!");

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/invalid-credentials")
            .jsonPath("$.title").isEqualTo("Invalid Credentials")
            .jsonPath("$.status").isEqualTo(401)
            .jsonPath("$.detail").isEqualTo("Invalid email or password");
    }

    @Test
    @DisplayName("Should reject login with wrong password with HTTP 401 Unauthorized in RFC 7807")
    void shouldRejectLoginWithIncorrectPasswordWith401Unauthorized() {
        LoginRequest request = new LoginRequest("student@codeconnect.dev", "WrongPassword999!");

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/invalid-credentials")
            .jsonPath("$.title").isEqualTo("Invalid Credentials")
            .jsonPath("$.status").isEqualTo(401)
            .jsonPath("$.detail").isEqualTo("Invalid email or password");
    }

    @Test
    @DisplayName("Should reject login for banned user with HTTP 403 Forbidden in RFC 7807")
    void shouldRejectLoginForBannedUserWith403Forbidden() {
        LoginRequest request = new LoginRequest("banned@codeconnect.dev", "BannedPass123!");

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/account-banned")
            .jsonPath("$.title").isEqualTo("Account Banned")
            .jsonPath("$.status").isEqualTo(403)
            .jsonPath("$.detail").value(detail ->
                assertThat(detail.toString()).contains("suspended"));
    }

    @Test
    @DisplayName("Should return user profile on GET /api/v1/auth/me when session cookie is provided")
    void shouldReturnCurrentUserWhenSessionIsValid() {
        AtomicReference<String> sessionCookie = new AtomicReference<>();

        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new LoginRequest("student@codeconnect.dev", "StudentPass123!"))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                String pair = cookie.split(";")[0];
                sessionCookie.set(pair.substring("APP_SESSION=".length()));
            });

        assertThat(sessionCookie.get()).isNotNull();

        // Call /me with session cookie
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .cookie("APP_SESSION", sessionCookie.get())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.email").isEqualTo("student@codeconnect.dev")
            .jsonPath("$.data.displayName").isEqualTo("Active Student")
            .jsonPath("$.data.role").isEqualTo("ROLE_STUDENT");
    }

    @Test
    @DisplayName("Should reject GET /api/v1/auth/me with HTTP 401 Unauthorized when unauthenticated")
    void shouldRejectGetCurrentUserWhenUnauthenticatedWith401() {
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/unauthorized")
            .jsonPath("$.status").isEqualTo(401);
    }

    @Test
    @DisplayName("Should successfully logout, invalidate session in Redis, and block subsequent /me calls")
    void shouldLogoutSuccessfullyAndInvalidateSession() {
        AtomicReference<String> sessionCookie = new AtomicReference<>();

        // Login
        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new LoginRequest("student@codeconnect.dev", "StudentPass123!"))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                String pair = cookie.split(";")[0];
                sessionCookie.set(pair.substring("APP_SESSION=".length()));
            });

        assertThat(sessionCookie.get()).isNotNull();

        // Logout
        webTestClient.post()
            .uri("/api/v1/auth/logout")
            .cookie("APP_SESSION", sessionCookie.get())
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                assertThat(cookie).contains("Max-Age=0");
            })
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Logged out successfully");

        // Subsequent /me should fail with 401
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .cookie("APP_SESSION", sessionCookie.get())
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
