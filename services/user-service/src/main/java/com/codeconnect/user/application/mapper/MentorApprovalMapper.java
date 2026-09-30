package com.codeconnect.user.application.mapper;

import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.domain.model.MentorApprovalDocument;
import org.springframework.stereotype.Component;

/**
 * Dedicated collaborator converting MentorApprovalDocument entities to immutable response DTOs.
 */
@Component
public class MentorApprovalMapper {

    public MentorApprovalResponse toResponse(MentorApprovalDocument document) {
        return new MentorApprovalResponse(
            document.getId(),
            document.getUserId(),
            document.getEmail(),
            document.getLinkedInUrl(),
            document.getBio(),
            document.getStatus(),
            document.getSubmittedAt(),
            document.getReviewedAt(),
            document.getReviewedBy()
        );
    }
}
