package com.codeconnect.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Domain entity recording a mentor registration approval request for administrative verification.
 * Persisted in 'mentor_approval_requests' collection.
 */
@Getter
@Setter
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

    private String displayName;

    private String linkedInUrl;

    private String bio;

    @Builder.Default
    private MentorApprovalStatus status = MentorApprovalStatus.PENDING;

    @CreatedDate
    private Instant submittedAt;
}
