package com.codeconnect.submission.domain.model;

import com.codeconnect.submission.domain.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "submissions")
public class SubmissionDocument {

    @Id
    private String id;
    private String studentId;
    private String studentEmail;
    private String footholdId;
    private String code;
    private String language;
    private SubmissionStatus status;
    private String errorMessage;
    private Instant createdAt;
    private Instant completedAt;
}
