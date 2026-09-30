package com.codeconnect.user.domain.repository;

import com.codeconnect.user.domain.model.MentorApprovalDocument;
import com.codeconnect.user.domain.enums.MentorApprovalStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorApprovalRepository extends MongoRepository<MentorApprovalDocument, String> {

    List<MentorApprovalDocument> findByStatus(MentorApprovalStatus status);

    Optional<MentorApprovalDocument> findByUserId(String userId);
}
