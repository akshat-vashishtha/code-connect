package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.AdminMentorService;
import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.domain.model.UserRole;
import com.codeconnect.user.domain.model.UserStatus;
import com.codeconnect.user.domain.repository.MentorApprovalRepository;
import com.codeconnect.user.domain.repository.UserRepository;
import com.codeconnect.user.infrastructure.session.SessionElevationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service facade implementation for reviewing, approving, and rejecting mentor verification requests.
 * Orchestrates document updates in MongoDB and real-time session attribute mutation in Redis.
 */
@Slf4j
@Service
public class AdminMentorServiceImpl implements AdminMentorService {

    private final MentorApprovalRepository mentorApprovalRepository;
    private final UserRepository userRepository;
    private final MentorApprovalMapper mentorApprovalMapper;
    private final SessionElevationManager sessionElevationManager;

    public AdminMentorServiceImpl(
        MentorApprovalRepository mentorApprovalRepository,
        UserRepository userRepository,
        MentorApprovalMapper mentorApprovalMapper,
        SessionElevationManager sessionElevationManager
    ) {
        this.mentorApprovalRepository = mentorApprovalRepository;
        this.userRepository = userRepository;
        this.mentorApprovalMapper = mentorApprovalMapper;
        this.sessionElevationManager = sessionElevationManager;
    }

    @Override
    public List<MentorApprovalResponse> getPendingMentorApplications() {
        return mentorApprovalRepository.findByStatus("PENDING").stream()
            .map(mentorApprovalMapper::toResponse)
            .toList();
    }

    @Override
    public MentorApprovalResponse approveMentorApplication(String applicationId, String reviewerAdminEmail) {
        log.info("Approving mentor application id={} by reviewer={}", applicationId, reviewerAdminEmail);

        MentorApprovalRequest request = mentorApprovalRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentor approval application not found for id: " + applicationId));

        User user = userRepository.findById(request.getUserId())
            .or(() -> userRepository.findByEmail(request.getEmail()))
            .orElseThrow(() -> new ResourceNotFoundException("User associated with application not found"));

        // Elevate User Account State
        user.setRole(UserRole.ROLE_MENTOR);
        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        // Update Approval Record
        request.setStatus("APPROVED");
        request.setReviewedAt(Instant.now());
        request.setReviewedBy(reviewerAdminEmail != null ? reviewerAdminEmail : "admin@codeconnect.dev");
        MentorApprovalRequest savedRequest = mentorApprovalRepository.save(request);

        // Elevate Active Redis Sessions in O(1) time
        sessionElevationManager.elevateUserSessions(user.getId(), user.getEmail());

        return mentorApprovalMapper.toResponse(savedRequest);
    }

    @Override
    public MentorApprovalResponse rejectMentorApplication(String applicationId, String reviewerAdminEmail) {
        log.info("Rejecting mentor application id={} by reviewer={}", applicationId, reviewerAdminEmail);

        MentorApprovalRequest request = mentorApprovalRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentor approval application not found for id: " + applicationId));

        request.setStatus("REJECTED");
        request.setReviewedAt(Instant.now());
        request.setReviewedBy(reviewerAdminEmail != null ? reviewerAdminEmail : "admin@codeconnect.dev");
        MentorApprovalRequest savedRequest = mentorApprovalRepository.save(request);

        return mentorApprovalMapper.toResponse(savedRequest);
    }
}
