package com.codeconnect.user.domain.model;

import com.codeconnect.user.domain.enums.MentorApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Rich domain entity representing mentor onboarding approval records
 * in the 'mentor_approval_requests' MongoDB collection.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mentor_approval_requests")
public class MentorApprovalRequest {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String email;

    private String linkedInUrl;

    private String bio;

    @Builder.Default
    private MentorApprovalStatus status = MentorApprovalStatus.PENDING;

    private Instant submittedAt;

    private Instant reviewedAt;

    private String reviewedBy;

    public static MentorApprovalRequest createPending(String userId, String email, String linkedInUrl, String bio) {
        return MentorApprovalRequest.builder()
            .userId(userId)
            .email(email)
            .linkedInUrl(linkedInUrl != null ? linkedInUrl.trim() : null)
            .bio(bio != null ? bio.trim() : null)
            .status(MentorApprovalStatus.PENDING)
            .submittedAt(Instant.now())
            .build();
    }

    /**
     * Approves the mentor application.
     * Enforces precondition invariant that the application must be PENDING.
     */
    public void approve(String reviewer) {
        validatePending();
        this.status = MentorApprovalStatus.APPROVED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = reviewer;
    }

    /**
     * Rejects the mentor application.
     * Enforces precondition invariant that the application must be PENDING.
     */
    public void reject(String reviewer) {
        validatePending();
        this.status = MentorApprovalStatus.REJECTED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = reviewer;
    }

    public void validatePending() {
        if (this.status != MentorApprovalStatus.PENDING) {
            throw new IllegalStateException("Application has already been adjudicated: " + this.status);
        }
    }
}
