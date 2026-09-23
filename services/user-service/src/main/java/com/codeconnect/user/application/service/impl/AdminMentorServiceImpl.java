package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.AdminMentorService;
import com.codeconnect.user.application.service.MentorApprovalManager;
import com.codeconnect.user.application.service.UserAccountElevator;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.User;
import com.codeconnect.user.infrastructure.session.SessionElevationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service facade orchestrating administrative mentor review workflows.
 * Adheres strictly to the Single Level of Abstraction Principle (SLAP):
 * delegates persistence queries and state transitions to dedicated collaborators.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMentorServiceImpl implements AdminMentorService {

    private final MentorApprovalManager mentorApprovalManager;
    private final UserAccountElevator userAccountElevator;
    private final SessionElevationManager sessionElevationManager;
    private final MentorApprovalMapper mentorApprovalMapper;

    @Override
    public List<MentorApprovalResponse> getPendingMentorApplications() {
        return mentorApprovalManager.findPendingApplications().stream()
            .map(mentorApprovalMapper::toResponse)
            .toList();
    }

    @Override
    public MentorApprovalResponse approveMentorApplication(String applicationId, String reviewerAdminEmail) {
        log.info("Approving mentor application id={} by reviewer={}", applicationId, reviewerAdminEmail);

        MentorApprovalRequest request = mentorApprovalManager.findApplication(applicationId);
        mentorApprovalManager.validateAdjudicable(request);
        User user = userAccountElevator.elevateToMentor(request.getUserId(), request.getEmail());
        MentorApprovalRequest approvedRequest = mentorApprovalManager.markApproved(request, reviewerAdminEmail);

        sessionElevationManager.elevateUserSessions(user.getId(), user.getEmail());

        return mentorApprovalMapper.toResponse(approvedRequest);
    }

    @Override
    public MentorApprovalResponse rejectMentorApplication(String applicationId, String reviewerAdminEmail) {
        log.info("Rejecting mentor application id={} by reviewer={}", applicationId, reviewerAdminEmail);

        MentorApprovalRequest request = mentorApprovalManager.findApplication(applicationId);
        MentorApprovalRequest rejectedRequest = mentorApprovalManager.markRejected(request, reviewerAdminEmail);

        return mentorApprovalMapper.toResponse(rejectedRequest);
    }
}
