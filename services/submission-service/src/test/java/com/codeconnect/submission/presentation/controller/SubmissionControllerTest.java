package com.codeconnect.submission.presentation.controller;

import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.service.SubmissionService;
import com.codeconnect.submission.domain.enums.SubmissionStatus;
import com.codeconnect.submission.infrastructure.config.properties.SubmissionProperties;
import com.codeconnect.submission.infrastructure.security.InternalSecurityValidator;
import com.codeconnect.submission.infrastructure.security.SecurityContextAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private SecurityContextAccessor securityContextAccessor;

    @MockBean
    private SubmissionProperties submissionProperties;

    @MockBean
    private InternalSecurityValidator internalSecurityValidator;

    @Test
    @DisplayName("POST /api/v1/submissions should return HTTP 202 ACCEPTED with submission details")
    void shouldAcceptSubmission() throws Exception {
        CreateSubmissionRequest request = new CreateSubmissionRequest("foothold-1", "class Solution {}", "JAVA");
        SubmissionResponse response = new SubmissionResponse(
            "sub-100", "student-1", "student@codeconnect.dev", "foothold-1",
            SubmissionStatus.PENDING, "class Solution {}", "JAVA", null, Instant.now(), null
        );

        when(securityContextAccessor.getAuthenticatedUserId()).thenReturn("student-1");
        when(securityContextAccessor.getAuthenticatedUserEmail()).thenReturn("student@codeconnect.dev");
        when(submissionService.createSubmission(any(CreateSubmissionRequest.class), eq("student-1"), eq("student@codeconnect.dev")))
            .thenReturn(response);

        mockMvc.perform(post("/api/v1/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.submissionId").value("sub-100"))
            .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/v1/submissions with invalid request should return HTTP 400 Bad Request")
    void shouldRejectInvalidSubmission() throws Exception {
        CreateSubmissionRequest invalidRequest = new CreateSubmissionRequest("", "", "JAVA");

        mockMvc.perform(post("/api/v1/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Failed"));
    }

    @Test
    @DisplayName("GET /api/v1/submissions/{id} should return HTTP 200 OK")
    void shouldGetSubmission() throws Exception {
        SubmissionResponse response = new SubmissionResponse(
            "sub-100", "student-1", "student@codeconnect.dev", "foothold-1",
            SubmissionStatus.PENDING, "class Solution {}", "JAVA", null, Instant.now(), null
        );

        when(submissionService.getSubmissionById("sub-100")).thenReturn(response);

        mockMvc.perform(get("/api/v1/submissions/sub-100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.submissionId").value("sub-100"));
    }
}
