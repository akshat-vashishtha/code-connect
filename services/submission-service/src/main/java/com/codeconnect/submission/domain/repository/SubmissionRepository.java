package com.codeconnect.submission.domain.repository;

import com.codeconnect.submission.domain.model.SubmissionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends MongoRepository<SubmissionDocument, String> {

    List<SubmissionDocument> findByStudentIdOrderByCreatedAtDesc(String studentId);

    List<SubmissionDocument> findByFootholdIdOrderByCreatedAtDesc(String footholdId);
}
