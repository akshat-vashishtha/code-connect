package com.codeconnect.user.application.service.impl;

import com.codeconnect.user.application.command.factory.MentorAdjudicationCommandFactory;
import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.application.mapper.MentorApprovalMapper;
import com.codeconnect.user.application.service.AdminMentorService;
import com.codeconnect.user.application.service.collaborator.mentor.MentorApprovalManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service facade dispatching mentor adjudication Commands.
 * Each adjudication workflow is encapsulated as a DomainCommand following the Command Pattern (GoF).
 * This class is a pure dispatcher: it constructs and executes commands, doing no business logic itself.
 */
@Service
@RequiredArgsConstructor
public class AdminMentorServiceImpl implements AdminMentorService {

    private final MentorApprovalManager mentorApprovalManager;
    private final MentorApprovalMapper mentorApprovalMapper;
    private final MentorAdjudicationCommandFactory commandFactory;

    @Override
    @Cacheable(value = "mentors", key = "'pending'", sync = true)
    public List<MentorApprovalResponse> getPendingMentorApplications() {
        return mentorApprovalManager.findPendingApplications().stream()
            .map(mentorApprovalMapper::toResponse)
            .toList();
    }

    @Override
    @CacheEvict(value = "mentors", allEntries = true)
    public MentorApprovalResponse approveMentorApplication(String applicationId, String reviewerAdminEmail) {
        return commandFactory.buildApprovalCommand(applicationId, reviewerAdminEmail).execute();
    }

    @Override
    @CacheEvict(value = "mentors", allEntries = true)
    public MentorApprovalResponse rejectMentorApplication(String applicationId, String reviewerAdminEmail) {
        return commandFactory.buildRejectionCommand(applicationId, reviewerAdminEmail).execute();
    }
}
