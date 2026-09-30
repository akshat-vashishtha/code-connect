package com.codeconnect.submission.presentation.controller;

import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.ApiResponse;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.service.SubmissionService;
import com.codeconnect.submission.infrastructure.security.SecurityContextAccessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SecurityContextAccessor securityContextAccessor;

    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitCode(
        @Valid @RequestBody CreateSubmissionRequest request
    ) {
        String studentId = securityContextAccessor.getAuthenticatedUserId();
        String studentEmail = securityContextAccessor.getAuthenticatedUserEmail();

        log.info("Received submission request from studentId={} for footholdId={}", studentId, request.footholdId());

        SubmissionResponse response = submissionService.createSubmission(request, studentId, studentEmail);

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(ApiResponse.success("Submission accepted for asynchronous processing", response));
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmission(
        @PathVariable String submissionId
    ) {
        SubmissionResponse response = submissionService.getSubmissionById(submissionId);
        return ResponseEntity.ok(ApiResponse.success("Submission retrieved successfully", response));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getStudentSubmissions(
        @PathVariable String studentId
    ) {
        List<SubmissionResponse> responses = submissionService.getSubmissionsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student submissions retrieved successfully", responses));
    }

    @GetMapping("/foothold/{footholdId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getFootholdSubmissions(
        @PathVariable String footholdId
    ) {
        List<SubmissionResponse> responses = submissionService.getSubmissionsByFootholdId(footholdId);
        return ResponseEntity.ok(ApiResponse.success("Foothold submissions retrieved successfully", responses));
    }
}
