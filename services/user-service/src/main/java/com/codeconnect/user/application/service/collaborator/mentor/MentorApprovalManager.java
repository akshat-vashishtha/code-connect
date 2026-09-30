package com.codeconnect.user.application.service.collaborator.mentor;

import com.codeconnect.user.domain.enums.MentorApprovalStatus;
import com.codeconnect.user.domain.exception.ResourceNotFoundException;
import com.codeconnect.user.domain.model.MentorApprovalDocument;
import com.codeconnect.user.domain.repository.MentorApprovalRepository;
import com.codeconnect.user.infrastructure.config.properties.UserProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Collaborator responsible for querying, creating, and mutating MentorApprovalDocument lifecycle state.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentorApprovalManager {

    private final MentorApprovalRepository mentorApprovalRepository;
    private final UserProperties userProperties;

    public List<MentorApprovalDocument> findPendingApplications() {
        return mentorApprovalRepository.findByStatus(MentorApprovalStatus.PENDING);
    }

    public MentorApprovalDocument createPendingApplication(String userId, String email, String linkedInUrl, String bio) {
        if (linkedInUrl == null || linkedInUrl.isBlank()) {
            throw new IllegalArgumentException("LinkedIn URL is required for mentor registration");
        }
        if (bio == null || bio.isBlank()) {
            throw new IllegalArgumentException("Professional bio is required for mentor registration");
        }
        MentorApprovalDocument document = MentorApprovalDocument.createPending(userId, email, linkedInUrl, bio);
        log.debug("Recording pending mentor approval document for userId={} email={}", userId, email);
        return mentorApprovalRepository.save(document);
    }

    public MentorApprovalDocument findApplication(String applicationId) {
        return mentorApprovalRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Mentor approval application not found for id: " + applicationId));
    }

    public void validateAdjudicable(MentorApprovalDocument document) {
        document.validatePending();
    }

    public MentorApprovalDocument markApproved(MentorApprovalDocument document, String reviewerAdminEmail) {
        document.approve(resolveReviewer(reviewerAdminEmail));
        log.debug("Marked application id={} as APPROVED by reviewer={}", document.getId(), document.getReviewedBy());
        return mentorApprovalRepository.save(document);
    }

    public MentorApprovalDocument markRejected(MentorApprovalDocument document, String reviewerAdminEmail) {
        document.reject(resolveReviewer(reviewerAdminEmail));
        log.debug("Marked application id={} as REJECTED by reviewer={}", document.getId(), document.getReviewedBy());
        return mentorApprovalRepository.save(document);
    }

    private String resolveReviewer(String reviewerAdminEmail) {
        return reviewerAdminEmail != null && !reviewerAdminEmail.isBlank()
            ? reviewerAdminEmail
            : userProperties.defaultReviewer();
    }
}
