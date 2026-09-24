package com.codeconnect.user.application.service;

import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.enums.MentorApprovalStatus;
import com.codeconnect.user.domain.repository.MentorApprovalRepository;
import com.codeconnect.user.infrastructure.config.UserProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Collaborator responsible for querying and mutating MentorApprovalRequest lifecycle state.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentorApprovalManager {

    private final MentorApprovalRepository mentorApprovalRepository;
    private final UserProperties userProperties;

    public List<MentorApprovalRequest> findPendingApplications() {
        return mentorApprovalRepository.findByStatus(MentorApprovalStatus.PENDING);
    }

    public MentorApprovalRequest createPendingApplication(String userId, String email, String linkedInUrl, String bio) {
        if (linkedInUrl == null || linkedInUrl.isBlank()) {
            throw new IllegalArgumentException("LinkedIn URL is required for mentor registration");
        }
        if (bio == null || bio.isBlank()) {
            throw new IllegalArgumentException("Professional bio is required for mentor registration");
        }
        MentorApprovalRequest request = MentorApprovalRequest.createPending(userId, email, linkedInUrl, bio);
        log.debug("Recording pending mentor approval request for userId={} email={}", userId, email);
        return mentorApprovalRepository.save(request);
    }

    public MentorApprovalRequest findApplication(String applicationId) {
        return mentorApprovalRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentor approval application not found for id: " + applicationId));
    }

    public void validateAdjudicable(MentorApprovalRequest request) {
        request.validatePending();
    }

    public MentorApprovalRequest markApproved(MentorApprovalRequest request, String reviewerAdminEmail) {
        request.approve(resolveReviewer(reviewerAdminEmail));
        log.debug("Marked application id={} as APPROVED by reviewer={}", request.getId(), request.getReviewedBy());
        return mentorApprovalRepository.save(request);
    }

    public MentorApprovalRequest markRejected(MentorApprovalRequest request, String reviewerAdminEmail) {
        request.reject(resolveReviewer(reviewerAdminEmail));
        log.debug("Marked application id={} as REJECTED by reviewer={}", request.getId(), request.getReviewedBy());
        return mentorApprovalRepository.save(request);
    }

    private String resolveReviewer(String reviewerAdminEmail) {
        return reviewerAdminEmail != null && !reviewerAdminEmail.isBlank()
            ? reviewerAdminEmail
            : userProperties.defaultReviewer();
    }
}
