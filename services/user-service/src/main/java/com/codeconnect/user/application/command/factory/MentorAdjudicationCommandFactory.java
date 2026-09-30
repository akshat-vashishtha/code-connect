package com.codeconnect.user.application.command.factory;

import com.codeconnect.user.application.command.DomainCommand;
import com.codeconnect.user.application.command.impl.ApproveMentorApplicationCommand;
import com.codeconnect.user.application.command.impl.RejectMentorApplicationCommand;
import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.application.service.collaborator.mentor.UserAccountElevator;
import com.codeconnect.user.infrastructure.session.SessionElevationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Factory for constructing mentor adjudication commands with all required dependencies.
 * Follows the Factory Pattern (GoF): centralizes command wiring, keeping the service layer
 * free of command construction details.
 *
 * <p>Both approval and rejection commands are wired with {@link ApplicationEventPublisher}
 * to dispatch domain events that the {@code UserDomainEventPublisher} bridges to Kafka
 * via {@code @TransactionalEventListener(AFTER_COMMIT)}.
 */
@Component
@RequiredArgsConstructor
public class MentorAdjudicationCommandFactory {

    private final MentorApprovalManager approvalManager;
    private final UserAccountElevator accountElevator;
    private final SessionElevationManager sessionElevationManager;
    private final MentorApprovalMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    public DomainCommand<MentorApprovalResponse> buildApprovalCommand(String applicationId, String reviewerAdminEmail) {
        return new ApproveMentorApplicationCommand(
            applicationId,
            reviewerAdminEmail,
            approvalManager,
            accountElevator,
            sessionElevationManager,
            mapper,
            eventPublisher
        );
    }

    public DomainCommand<MentorApprovalResponse> buildRejectionCommand(String applicationId, String reviewerAdminEmail) {
        return new RejectMentorApplicationCommand(
            applicationId,
            reviewerAdminEmail,
            approvalManager,
            mapper,
            eventPublisher
        );
    }
}
