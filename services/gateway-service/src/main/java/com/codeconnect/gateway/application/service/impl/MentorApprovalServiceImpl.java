package com.codeconnect.gateway.application.service.impl;

import com.codeconnect.gateway.application.dto.request.SignupRequest;
import com.codeconnect.gateway.application.service.MentorApprovalService;
import com.codeconnect.gateway.domain.enums.MentorApprovalStatus;
import com.codeconnect.gateway.domain.enums.UserRole;
import com.codeconnect.gateway.domain.model.MentorApprovalRequest;
import com.codeconnect.gateway.domain.model.User;
import com.codeconnect.gateway.domain.repository.ReactiveMentorApprovalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Implementation of MentorApprovalService persisting verification requests
 * to the 'mentor_approval_requests' MongoDB collection.
 */
@Slf4j
@Service
public class MentorApprovalServiceImpl implements MentorApprovalService {

    private final ReactiveMentorApprovalRepository mentorApprovalRepository;

    public MentorApprovalServiceImpl(ReactiveMentorApprovalRepository mentorApprovalRepository) {
        this.mentorApprovalRepository = mentorApprovalRepository;
    }

    @Override
    public Mono<Void> recordSubmission(User user, SignupRequest request) {
        if (user.getRole() != UserRole.ROLE_MENTOR) {
            return Mono.empty();
        }

        MentorApprovalRequest approvalRequest = MentorApprovalRequest.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .linkedInUrl(request.linkedInUrl() != null ? request.linkedInUrl().trim() : null)
            .bio(request.bio() != null ? request.bio().trim() : null)
            .status(MentorApprovalStatus.PENDING)
            .submittedAt(Instant.now())
            .build();

        log.info("Persisting mentor approval request for userId={} email={}", user.getId(), user.getEmail());
        return mentorApprovalRepository.save(approvalRequest).then();
    }
}
