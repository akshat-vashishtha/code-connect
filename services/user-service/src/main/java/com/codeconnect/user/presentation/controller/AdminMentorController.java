package com.codeconnect.user.presentation.controller;

import com.codeconnect.user.application.dto.ApiResponse;
import com.codeconnect.user.application.dto.MentorApprovalResponse;
import com.codeconnect.user.application.service.AdminMentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrative REST controller for reviewing and adjudicating mentor onboarding applications.
 */
@RestController
@RequestMapping("/api/v1/admin/mentors")
public class AdminMentorController {

    private final AdminMentorService adminMentorService;

    public AdminMentorController(AdminMentorService adminMentorService) {
        this.adminMentorService = adminMentorService;
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<MentorApprovalResponse>>> getPendingMentors() {
        List<MentorApprovalResponse> pending = adminMentorService.getPendingMentorApplications();
        return ResponseEntity.ok(ApiResponse.ok("Pending mentor applications retrieved", pending));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<MentorApprovalResponse>> approveMentor(
        @PathVariable String id,
        @RequestHeader(value = "X-User-Email", required = false) String adminEmail
    ) {
        MentorApprovalResponse response = adminMentorService.approveMentorApplication(id, adminEmail);
        return ResponseEntity.ok(ApiResponse.ok("Mentor application approved successfully", response));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<MentorApprovalResponse>> rejectMentor(
        @PathVariable String id,
        @RequestHeader(value = "X-User-Email", required = false) String adminEmail
    ) {
        MentorApprovalResponse response = adminMentorService.rejectMentorApplication(id, adminEmail);
        return ResponseEntity.ok(ApiResponse.ok("Mentor application rejected", response));
    }
}
