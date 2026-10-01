package com.codeconnect.user.application.command;

import com.codeconnect.user.application.command.factory.MentorAdjudicationCommandFactory;
import com.codeconnect.user.application.command.impl.ApproveMentorApplicationCommand;
import com.codeconnect.user.application.command.impl.RejectMentorApplicationCommand;
import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import com.codeconnect.user.application.service.collaborator.mentor.UserAccountElevator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MentorAdjudicationCommandFactoryTest {

    @Mock
    private MentorApprovalManager approvalManager;

    @Mock
    private UserAccountElevator accountElevator;

    @Mock
    private MentorApprovalMapper mapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MentorAdjudicationCommandFactory factory;

    @Test
    @DisplayName("Should build approval command successfully")
    void shouldBuildApprovalCommand() {
        DomainCommand<MentorApprovalResponse> command = factory.buildApprovalCommand("app-1", "admin@codeconnect.dev");
        assertThat(command).isInstanceOf(ApproveMentorApplicationCommand.class);
    }

    @Test
    @DisplayName("Should build rejection command successfully")
    void shouldBuildRejectionCommand() {
        DomainCommand<MentorApprovalResponse> command = factory.buildRejectionCommand("app-1", "admin@codeconnect.dev");
        assertThat(command).isInstanceOf(RejectMentorApplicationCommand.class);
    }
}
