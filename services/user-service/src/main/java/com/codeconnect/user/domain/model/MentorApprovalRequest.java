package com.codeconnect.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document entity representing mentor onboarding approval records
 * in the 'mentor_approval_requests' collection.
 */
@Data
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
}
