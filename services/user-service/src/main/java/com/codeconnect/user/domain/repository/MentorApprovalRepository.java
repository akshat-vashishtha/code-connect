package com.codeconnect.user.domain.repository;

import com.codeconnect.user.domain.model.MentorApprovalRequest;
import com.codeconnect.user.domain.model.MentorApprovalStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorApprovalRepository extends MongoRepository<MentorApprovalRequest, String> {

    List<MentorApprovalRequest> findByStatus(MentorApprovalStatus status);

    Optional<MentorApprovalRequest> findByUserId(String userId);
}
