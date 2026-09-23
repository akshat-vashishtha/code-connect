package com.codeconnect.user;

import com.codeconnect.user.application.dto.LanguageDetectionRequest;
import com.codeconnect.user.domain.model.LanguagePreference;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.model.UserRole;
import com.codeconnect.user.domain.model.UserStatus;
import com.codeconnect.user.domain.repository.MentorApprovalRepository;
import com.codeconnect.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminMentorVerificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorApprovalRepository mentorApprovalRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mentorApprovalRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list all pending mentor applications")
    void shouldListPendingMentorApplications() throws Exception {
        MentorApprovalRequest pendingReq = MentorApprovalRequest.builder()
            .userId("user-123")
            .email("mentor.pending@codeconnect.dev")
            .linkedInUrl("https://linkedin.com/in/mentor-pending")
            .bio("Staff Systems Engineer with 8+ years experience")
            .status("PENDING")
            .submittedAt(Instant.now())
            .build();
        mentorApprovalRepository.save(pendingReq);

        MentorApprovalRequest approvedReq = MentorApprovalRequest.builder()
            .userId("user-456")
            .email("mentor.approved@codeconnect.dev")
            .linkedInUrl("https://linkedin.com/in/mentor-approved")
            .bio("Senior Architect")
            .status("APPROVED")
            .submittedAt(Instant.now())
            .build();
        mentorApprovalRepository.save(approvedReq);

        mockMvc.perform(get("/api/v1/admin/mentors/pending"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].email").value("mentor.pending@codeconnect.dev"))
            .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Should approve pending mentor, update MongoDB status/role, and mutate Redis session in O(1) time")
    void shouldApproveMentorAndUpdateMongoAndRedisSession() throws Exception {
        // 1. Seed user in MongoDB with PENDING_APPROVAL status
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .email("priya.mentor@codeconnect.dev")
            .displayName("Priya Patel")
            .passwordHash("$2a$10$hashedPassword")
            .role(UserRole.ROLE_MENTOR)
            .status(UserStatus.PENDING_APPROVAL)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        userRepository.save(user);

        // 2. Seed mentor approval request
        MentorApprovalRequest approvalReq = MentorApprovalRequest.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .linkedInUrl("https://linkedin.com/in/priyapatel")
            .bio("Principal Engineer at CloudScale")
            .status("PENDING")
            .submittedAt(Instant.now())
            .build();
        MentorApprovalRequest savedReq = mentorApprovalRepository.save(approvalReq);

        // 3. Seed active Redis session for this user
        String sessionKey = "spring:session:sessions:" + UUID.randomUUID();
        redisTemplate.opsForHash().put(sessionKey, "sessionAttr:USER_ID", user.getId());
        redisTemplate.opsForHash().put(sessionKey, "sessionAttr:USER_EMAIL", user.getEmail());
        redisTemplate.opsForHash().put(sessionKey, "sessionAttr:USER_ROLE", "ROLE_MENTOR");
        redisTemplate.opsForHash().put(sessionKey, "sessionAttr:USER_STATUS", "PENDING_APPROVAL");

        // 4. Perform Admin Approval
        mockMvc.perform(post("/api/v1/admin/mentors/{id}/approve", savedReq.getId())
                .header("X-User-Email", "admin@codeconnect.dev"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("APPROVED"))
            .andExpect(jsonPath("$.data.reviewedBy").value("admin@codeconnect.dev"));

        // 5. Verify MongoDB User State Elevated
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(updatedUser.getRole()).isEqualTo(UserRole.ROLE_MENTOR);

        // 6. Verify MongoDB Approval Request Status
        MentorApprovalRequest updatedReq = mentorApprovalRepository.findById(savedReq.getId()).orElseThrow();
        assertThat(updatedReq.getStatus()).isEqualTo("APPROVED");

        // 7. Verify Redis Session Attributes Mutated
        Object redisRole = redisTemplate.opsForHash().get(sessionKey, "sessionAttr:USER_ROLE");
        Object redisStatus = redisTemplate.opsForHash().get(sessionKey, "sessionAttr:USER_STATUS");
        assertThat(redisRole).isEqualTo("ROLE_MENTOR");
        assertThat(redisStatus).isEqualTo("ACTIVE");

        // Clean up test redis key
        redisTemplate.delete(sessionKey);
    }

    @Test
    @DisplayName("Should reject pending mentor and update status to REJECTED")
    void shouldRejectMentorApplication() throws Exception {
        MentorApprovalRequest req = MentorApprovalRequest.builder()
            .userId("user-reject-1")
            .email("unqualified@codeconnect.dev")
            .linkedInUrl("https://linkedin.com/in/unqualified")
            .bio("Brief bio")
            .status("PENDING")
            .submittedAt(Instant.now())
            .build();
        MentorApprovalRequest savedReq = mentorApprovalRepository.save(req);

        mockMvc.perform(post("/api/v1/admin/mentors/{id}/reject", savedReq.getId())
                .header("X-User-Email", "admin@codeconnect.dev"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("REJECTED"));

        MentorApprovalRequest updatedReq = mentorApprovalRepository.findById(savedReq.getId()).orElseThrow();
        assertThat(updatedReq.getStatus()).isEqualTo("REJECTED");
    }

    @Test
    @DisplayName("Should detect Hinglish phrasing and tag session preference")
    void shouldDetectHinglishPhrasing() throws Exception {
        LanguageDetectionRequest hinglishReq = new LanguageDetectionRequest("bhai ye code kaise kare samajh nahi aaya");

        mockMvc.perform(post("/api/v1/users/detect-language")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hinglishReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.detectedLanguage").value(LanguagePreference.HINGLISH.name()))
            .andExpect(jsonPath("$.data.sessionPreference").value(LanguagePreference.HINGLISH.name()));

        LanguageDetectionRequest englishReq = new LanguageDetectionRequest("Can you explain the difference between comparable and comparator in Java?");

        mockMvc.perform(post("/api/v1/users/detect-language")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(englishReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.detectedLanguage").value(LanguagePreference.ENGLISH.name()));
    }
}
