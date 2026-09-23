package com.codeconnect.gateway.domain.repository;

import com.codeconnect.gateway.domain.model.MentorApprovalRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Reactive MongoDB repository for MentorApprovalRequest entity.
 */
@Repository
public interface ReactiveMentorApprovalRepository extends ReactiveMongoRepository<MentorApprovalRequest, String> {

    Mono<MentorApprovalRequest> findByUserId(String userId);
}
