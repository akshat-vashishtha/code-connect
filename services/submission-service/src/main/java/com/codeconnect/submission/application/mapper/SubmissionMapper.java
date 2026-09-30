package com.codeconnect.submission.application.mapper;

import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.domain.model.SubmissionDocument;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    public SubmissionResponse toResponse(SubmissionDocument doc) {
        if (doc == null) {
            return null;
        }
        return new SubmissionResponse(
            doc.getId(),
            doc.getStudentId(),
            doc.getStudentEmail(),
            doc.getFootholdId(),
            doc.getStatus(),
            doc.getCode(),
            doc.getLanguage(),
            doc.getErrorMessage(),
            doc.getCreatedAt(),
            doc.getCompletedAt()
        );
    }
}
