package com.codeconnect.user.application.mapper;

import com.codeconnect.user.application.dto.response.MentorApprovalResponse;
import com.codeconnect.user.domain.model.MentorApprovalRequest;
import org.springframework.stereotype.Component;

/**
 * Dedicated collaborator converting MentorApprovalRequest document entities to immutable response DTOs.
 */
@Component
public class MentorApprovalMapper {

    public MentorApprovalResponse toResponse(MentorApprovalRequest request) {
        return new MentorApprovalResponse(
            request.getId(),
            request.getUserId(),
            request.getEmail(),
            request.getLinkedInUrl(),
            request.getBio(),
            request.getStatus(),
            request.getSubmittedAt(),
            request.getReviewedAt(),
            request.getReviewedBy()
        );
    }
}
