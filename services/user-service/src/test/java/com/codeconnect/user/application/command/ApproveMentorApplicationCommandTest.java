package com.codeconnect.user.application.command;

import com.codeconnect.user.application.command.impl.ApproveMentorApplicationCommand;
import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.application.service.collaborator.mentor.UserAccountElevator;
import com.codeconnect.user.domain.enums.MentorApprovalStatus;
import com.codeconnect.user.domain.enums.UserRole;
import com.codeconnect.user.domain.enums.UserStatus;
import com.codeconnect.user.domain.event.MentorApprovedEvent;
import com.codeconnect.user.domain.model.MentorApprovalDocument;
import com.codeconnect.user.domain.model.UserDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveMentorApplicationCommandTest {

    @Mock
    private MentorApprovalManager approvalManager;

    @Mock
    private UserAccountElevator accountElevator;

    @Mock
    private MentorApprovalMapper mapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("Should execute mentor approval and publish MentorApprovedEvent")
    void shouldExecuteMentorApprovalAndPublishDomainEvent() {
        String appId = "app-100";
        String reviewer = "admin@codeconnect.dev";
        String userId = "user-100";
        String email = "candidate@codeconnect.dev";

        MentorApprovalDocument pending = MentorApprovalDocument.builder()
            .id(appId)
            .userId(userId)
            .email(email)
            .status(MentorApprovalStatus.PENDING)
            .build();

        UserDocument elevatedUser = UserDocument.builder()
            .id(userId)
            .email(email)
            .role(UserRole.ROLE_MENTOR)
            .status(UserStatus.ACTIVE)
            .build();

        MentorApprovalDocument approved = MentorApprovalDocument.builder()
            .id(appId)
            .userId(userId)
            .email(email)
            .status(MentorApprovalStatus.APPROVED)
            .reviewedBy(reviewer)
            .reviewedAt(Instant.now())
            .build();

        MentorApprovalResponse response = new MentorApprovalResponse(
            appId,
            userId,
            email,
            "https://linkedin.com/in/candidate",
            "Staff Engineer",
            MentorApprovalStatus.APPROVED,
            Instant.now(),
            Instant.now(),
            reviewer
        );

        when(approvalManager.findApplication(appId)).thenReturn(pending);
        when(accountElevator.elevateToMentor(userId, email)).thenReturn(elevatedUser);
        when(approvalManager.markApproved(pending, reviewer)).thenReturn(approved);
        when(mapper.toResponse(approved)).thenReturn(response);

        ApproveMentorApplicationCommand command = new ApproveMentorApplicationCommand(
            appId, reviewer, approvalManager, accountElevator, mapper, eventPublisher
        );

        MentorApprovalResponse result = command.execute();

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(MentorApprovalStatus.APPROVED);

        verify(approvalManager).validateAdjudicable(pending);

        ArgumentCaptor<MentorApprovedEvent> eventCaptor = ArgumentCaptor.forClass(MentorApprovedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        MentorApprovedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.applicationId()).isEqualTo(appId);
        assertThat(publishedEvent.userId()).isEqualTo(userId);
        assertThat(publishedEvent.email()).isEqualTo(email);
        assertThat(publishedEvent.reviewedBy()).isEqualTo(reviewer);
    }
}
