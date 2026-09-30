package com.codeconnect.user.application.command.impl;

import com.codeconnect.user.application.command.DomainCommand;
import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.application.service.collaborator.mentor.UserAccountElevator;
import com.codeconnect.user.domain.event.MentorApprovedEvent;
import com.codeconnect.user.domain.model.MentorApprovalDocument;
import com.codeconnect.user.domain.model.UserDocument;
import com.codeconnect.user.infrastructure.session.SessionElevationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

/**
 * Command encapsulating the complete mentor approval transactional workflow:
 * 1. Validates the application is adjudicable (PENDING state)
 * 2. Elevates the user account to ROLE_MENTOR
 * 3. Marks the application as APPROVED with reviewer attribution
 * 4. Propagates role elevation to all active Redis sessions
 * 5. Publishes MentorApprovedEvent for downstream asynchronous processing
 *
 * Follows the Command Pattern (GoF): the workflow is a first-class, self-contained object,
 * enabling retryability, audit logging, and independent testability.
 */
@Slf4j
@RequiredArgsConstructor
public class ApproveMentorApplicationCommand implements DomainCommand<MentorApprovalResponse> {

    private final String applicationId;
    private final String reviewerAdminEmail;
    private final MentorApprovalManager approvalManager;
    private final UserAccountElevator accountElevator;
    private final SessionElevationManager sessionElevationManager;
    private final MentorApprovalMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public MentorApprovalResponse execute() {
        log.info("Executing ApproveMentorApplicationCommand for applicationId={} by reviewer={}", applicationId, reviewerAdminEmail);

        MentorApprovalDocument application = approvalManager.findApplication(applicationId);
        approvalManager.validateAdjudicable(application);

        UserDocument elevatedUser = accountElevator.elevateToMentor(application.getUserId(), application.getEmail());
        MentorApprovalDocument approvedApplication = approvalManager.markApproved(application, reviewerAdminEmail);

        sessionElevationManager.elevateUserSessions(elevatedUser.getId(), elevatedUser.getEmail());

        eventPublisher.publishEvent(new MentorApprovedEvent(
            approvedApplication.getId(),
            elevatedUser.getId(),
            elevatedUser.getEmail(),
            reviewerAdminEmail,
            Instant.now()
        ));

        return mapper.toResponse(approvedApplication);
    }
}
