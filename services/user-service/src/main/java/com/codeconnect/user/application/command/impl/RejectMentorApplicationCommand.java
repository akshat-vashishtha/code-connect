package com.codeconnect.user.application.command.impl;

import com.codeconnect.user.application.command.DomainCommand;
import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.domain.event.MentorRejectedEvent;
import com.codeconnect.user.domain.model.MentorApprovalDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

/**
 * Command encapsulating the mentor rejection transactional workflow:
 * 1. Locates the application record
 * 2. Marks the application as REJECTED with reviewer attribution
 * 3. Publishes {@link MentorRejectedEvent} via {@link ApplicationEventPublisher} for cross-service
 *    propagation to Kafka (picked up by {@link com.codeconnect.user.infrastructure.messaging.UserDomainEventPublisher}
 *    after the MongoDB transaction commits via {@code @TransactionalEventListener(AFTER_COMMIT)}).
 *
 * Follows the Command Pattern (GoF): self-contained, independently testable.
 */
@Slf4j
@RequiredArgsConstructor
public class RejectMentorApplicationCommand implements DomainCommand<MentorApprovalResponse> {

    private final String applicationId;
    private final String reviewerAdminEmail;
    private final MentorApprovalManager approvalManager;
    private final MentorApprovalMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public MentorApprovalResponse execute() {
        log.info("Executing RejectMentorApplicationCommand for applicationId={} by reviewer={}", applicationId, reviewerAdminEmail);

        MentorApprovalDocument application = approvalManager.findApplication(applicationId);
        MentorApprovalDocument rejectedApplication = approvalManager.markRejected(application, reviewerAdminEmail);

        eventPublisher.publishEvent(new MentorRejectedEvent(
            rejectedApplication.getId(),
            rejectedApplication.getUserId(),
            rejectedApplication.getEmail(),
            reviewerAdminEmail,
            Instant.now()
        ));

        return mapper.toResponse(rejectedApplication);
    }
}
