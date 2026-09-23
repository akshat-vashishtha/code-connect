package com.codeconnect.gateway;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.domain.model.MentorApprovalStatus;
import com.codeconnect.gateway.domain.model.UserRole;
import com.codeconnect.gateway.domain.repository.ReactiveMentorApprovalRepository;
import com.codeconnect.gateway.domain.repository.ReactiveUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class AuthRegistrationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReactiveUserRepository userRepository;

    @Autowired
    private ReactiveMentorApprovalRepository mentorApprovalRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
        mentorApprovalRepository.deleteAll().block();
    }

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
            .jsonPath("$.data.status").isEqualTo("ACTIVE")
            .jsonPath("$.data.password").doesNotExist()
            .jsonPath("$.data.passwordHash").doesNotExist();

        // Verify MongoDB persistence and password hashing
        var savedUser = userRepository.findByEmail("student@codeconnect.dev").block();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPasswordHash()).startsWith("$2a$");
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("SecurePass123!");
    }

    @Test
    @DisplayName("Should register Mentor with PENDING_APPROVAL status and create approval audit request")
    void shouldRegisterMentorWithPendingApprovalAndCreateAuditRequest() {
        SignupRequest request = new SignupRequest(
            "mentor@codeconnect.dev",
            "MentorPass123!",
            "Priya Patel",
            UserRole.ROLE_MENTOR,
            "https://linkedin.com/in/priyapatel",
            "Principal Distributed Systems Architect with 10+ years experience"
        );

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

        // Verify user in Mongo
        var savedUser = userRepository.findByEmail("mentor@codeconnect.dev").block();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getStatus().name()).isEqualTo("PENDING_APPROVAL");

        // Verify mentor approval audit entry in Mongo
        var approvalRequest = mentorApprovalRepository.findByUserId(savedUser.getId()).block();
        assertThat(approvalRequest).isNotNull();
        assertThat(approvalRequest.getLinkedInUrl()).isEqualTo("https://linkedin.com/in/priyapatel");
        assertThat(approvalRequest.getBio()).contains("Principal Distributed Systems Architect");
        assertThat(approvalRequest.getStatus()).isEqualTo(MentorApprovalStatus.PENDING);
    }

    @Test
    @DisplayName("Should reject duplicate email registration with HTTP 409 Conflict in RFC 7807 Problem Details")
    void shouldRejectDuplicateEmailWith409ConflictAndRfc7807ProblemDetail() {
        SignupRequest initialRequest = new SignupRequest(
            "duplicate@codeconnect.dev",
            "Password123!",
            "First User",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        // First registration succeeds
        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(initialRequest)
            .exchange()
            .expectStatus().isCreated();

        // Second registration with identical email (case-insensitive) fails with 409
        SignupRequest duplicateRequest = new SignupRequest(
            "DUPLICATE@codeconnect.dev",
            "DifferentPass456!",
            "Second User",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(duplicateRequest)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://codeconnect.dev/errors/email-conflict")
            .jsonPath("$.title").isEqualTo("Email Conflict")
            .jsonPath("$.status").isEqualTo(409)
            .jsonPath("$.detail").value(detail -> {
                assertThat(detail.toString()).contains("duplicate@codeconnect.dev");
                assertThat(detail.toString()).contains("already registered");
            });
    }

    @Test
    @DisplayName("Should reject invalid email format with HTTP 400 Bad Request")
    void shouldRejectInvalidEmailFormatWith400BadRequest() {
        SignupRequest invalidRequest = new SignupRequest(
            "invalid-email-address",
            "Password123!",
            "User Name",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Validation Failed");
    }

    @Test
    @DisplayName("Should reject mentor signup without LinkedIn URL with HTTP 400 Bad Request")
    void shouldRejectMentorWithoutLinkedInUrlWith400BadRequest() {
        SignupRequest mentorWithoutLinkedIn = new SignupRequest(
            "incomplete-mentor@codeconnect.dev",
            "Password123!",
            "Incomplete Mentor",
            UserRole.ROLE_MENTOR,
            "",
            "A valid bio description"
        );

        webTestClient.post()
            .uri("/api/v1/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mentorWithoutLinkedIn)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.detail").value(detail ->
                assertThat(detail.toString()).contains("LinkedIn URL is required"));
    }
}
