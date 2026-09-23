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
class GatewayRbacIntegrationTest {

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
            .email("student-rbac@codeconnect.dev")
            .passwordHash(passwordEncoder.encode("StudentPass123!"))
            .displayName("Active Student")
            .role(UserRole.ROLE_STUDENT)
            .status(UserStatus.ACTIVE)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        userRepository.save(student).block();

        // Seed Active Mentor
        User mentor = User.builder()
            .email("mentor-rbac@codeconnect.dev")
            .passwordHash(passwordEncoder.encode("MentorPass123!"))
            .displayName("Active Mentor")
            .role(UserRole.ROLE_MENTOR)
            .status(UserStatus.ACTIVE)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        userRepository.save(mentor).block();
    }

    private String loginAndGetSessionCookie(String email, String password) {
        AtomicReference<String> sessionCookie = new AtomicReference<>();
        webTestClient.post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new LoginRequest(email, password))
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Set-Cookie", cookie -> {
                String pair = cookie.split(";")[0];
                sessionCookie.set(pair.substring("APP_SESSION=".length()));
            });
        return sessionCookie.get();
    }

    @Test
    @DisplayName("Should reject unauthenticated access to /api/v1/admin/** with HTTP 403 Forbidden RFC 7807")
    void shouldRejectUnauthenticatedAccessToAdminRoutes() {
        webTestClient.get()
            .uri("/api/v1/admin/mentors/pending")
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/forbidden")
            .jsonPath("$.title").isEqualTo("Access Denied")
            .jsonPath("$.status").isEqualTo(403)
            .jsonPath("$.detail").value(detail ->
                assertThat(detail.toString()).contains("Authentication required"));
    }

    @Test
    @DisplayName("Should reject student access to /api/v1/admin/** with HTTP 403 Forbidden RFC 7807")
    void shouldRejectStudentAccessToAdminRoutes() {
        String studentCookie = loginAndGetSessionCookie("student-rbac@codeconnect.dev", "StudentPass123!");

        webTestClient.get()
            .uri("/api/v1/admin/mentors/pending")
            .cookie("APP_SESSION", studentCookie)
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/forbidden")
            .jsonPath("$.title").isEqualTo("Access Denied")
            .jsonPath("$.status").isEqualTo(403)
            .jsonPath("$.detail").isEqualTo("Insufficient role permissions for requested resource");
    }

    @Test
    @DisplayName("Should reject student access to /api/v1/mentor/** with HTTP 403 Forbidden RFC 7807")
    void shouldRejectStudentAccessToMentorRoutes() {
        String studentCookie = loginAndGetSessionCookie("student-rbac@codeconnect.dev", "StudentPass123!");

        webTestClient.get()
            .uri("/api/v1/mentor/analytics")
            .cookie("APP_SESSION", studentCookie)
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/forbidden")
            .jsonPath("$.title").isEqualTo("Access Denied")
            .jsonPath("$.status").isEqualTo(403);
    }

    @Test
    @DisplayName("Should reject mentor access to /api/v1/admin/** with HTTP 403 Forbidden RFC 7807")
    void shouldRejectMentorAccessToAdminRoutes() {
        String mentorCookie = loginAndGetSessionCookie("mentor-rbac@codeconnect.dev", "MentorPass123!");

        webTestClient.get()
            .uri("/api/v1/admin/mentors/pending")
            .cookie("APP_SESSION", mentorCookie)
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/forbidden")
            .jsonPath("$.title").isEqualTo("Access Denied")
            .jsonPath("$.status").isEqualTo(403);
    }

    @Test
    @DisplayName("Should allow non-restricted routes (e.g. /actuator/health) without RBAC interference")
    void shouldAllowUnrestrictedRoutes() {
        webTestClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }
}
