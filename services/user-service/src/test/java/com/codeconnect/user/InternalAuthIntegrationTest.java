package com.codeconnect.user;

import com.codeconnect.user.application.dto.request.UserAuthenticationRequest;
import com.codeconnect.user.application.dto.request.UserRegistrationRequest;
import com.codeconnect.user.domain.enums.MentorApprovalStatus;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.repository.MentorApprovalRepository;
import com.codeconnect.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InternalAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorApprovalRepository mentorApprovalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mentorApprovalRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a Student with ACTIVE status")
    void shouldRegisterStudentSuccessfully() throws Exception {
        UserRegistrationRequest req = new UserRegistrationRequest(
            "student@codeconnect.dev",
            "SecurePass123!",
            "Aarav Sharma",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        mockMvc.perform(post("/api/v1/internal/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("student@codeconnect.dev"))
            .andExpect(jsonPath("$.data.role").value("ROLE_STUDENT"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        User savedUser = userRepository.findByEmail("student@codeconnect.dev").orElseThrow();
        assertThat(passwordEncoder.matches("SecurePass123!", savedUser.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("Should register Mentor with PENDING_APPROVAL status and persist MentorApprovalRequest")
    void shouldRegisterMentorAndCreateApprovalRequest() throws Exception {
        UserRegistrationRequest req = new UserRegistrationRequest(
            "priya.mentor@codeconnect.dev",
            "SecurePass123!",
            "Priya Patel",
            UserRole.ROLE_MENTOR,
            "https://linkedin.com/in/priyapatel",
            "Staff Systems Architect with 10+ years distributed systems expertise"
        );

        mockMvc.perform(post("/api/v1/internal/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.role").value("ROLE_MENTOR"))
            .andExpect(jsonPath("$.data.status").value("PENDING_APPROVAL"));

        User savedUser = userRepository.findByEmail("priya.mentor@codeconnect.dev").orElseThrow();
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.PENDING_APPROVAL);

        List<MentorApprovalRequest> approvals = mentorApprovalRepository.findByStatus(MentorApprovalStatus.PENDING);
        assertThat(approvals).hasSize(1);
        assertThat(approvals.get(0).getUserId()).isEqualTo(savedUser.getId());
        assertThat(approvals.get(0).getLinkedInUrl()).isEqualTo("https://linkedin.com/in/priyapatel");
    }

    @Test
    @DisplayName("Should reject duplicate registration with HTTP 409 Conflict")
    void shouldRejectDuplicateRegistration() throws Exception {
        User existing = User.createStudent("duplicate@codeconnect.dev", "hash", "Existing User");
        userRepository.save(existing);

        UserRegistrationRequest req = new UserRegistrationRequest(
            "duplicate@codeconnect.dev",
            "SecurePass123!",
            "Another Name",
            UserRole.ROLE_STUDENT,
            null,
            null
        );

        mockMvc.perform(post("/api/v1/internal/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void shouldAuthenticateValidUser() throws Exception {
        String hash = passwordEncoder.encode("SecretPassword123!");
        User user = User.createStudent("auth.user@codeconnect.dev", hash, "Auth User");
        userRepository.save(user);

        UserAuthenticationRequest authReq = new UserAuthenticationRequest("auth.user@codeconnect.dev", "SecretPassword123!");

        mockMvc.perform(post("/api/v1/internal/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("auth.user@codeconnect.dev"));
    }

    @Test
    @DisplayName("Should reject authentication with invalid password (401 Unauthorized)")
    void shouldRejectInvalidPassword() throws Exception {
        String hash = passwordEncoder.encode("CorrectPassword!");
        User user = User.createStudent("auth.wrong@codeconnect.dev", hash, "User");
        userRepository.save(user);

        UserAuthenticationRequest authReq = new UserAuthenticationRequest("auth.wrong@codeconnect.dev", "WrongPassword!");

        mockMvc.perform(post("/api/v1/internal/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should reject authentication for non-existent email (401 Unauthorized)")
    void shouldRejectNonExistentEmail() throws Exception {
        UserAuthenticationRequest authReq = new UserAuthenticationRequest("nobody@codeconnect.dev", "AnyPassword!");

        mockMvc.perform(post("/api/v1/internal/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }


    @Test
    @DisplayName("Should retrieve user profile by ID")
    void shouldGetUserById() throws Exception {
        User user = User.createStudent("lookup@codeconnect.dev", "hash", "Lookup User");
        User saved = userRepository.save(user);

        mockMvc.perform(get("/api/v1/internal/users/{id}", saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(saved.getId()))
            .andExpect(jsonPath("$.data.email").value("lookup@codeconnect.dev"));
    }
}
