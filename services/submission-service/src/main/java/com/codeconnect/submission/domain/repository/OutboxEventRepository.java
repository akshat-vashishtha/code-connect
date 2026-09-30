package com.codeconnect.submission.domain.repository;

import com.codeconnect.submission.domain.enums.OutboxEventStatus;
import com.codeconnect.submission.domain.model.OutboxEventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for the Transactional Outbox collection.
 * The polling relay reads PENDING entries, publishes to Kafka, then marks PUBLISHED.
 */
@Repository
public interface OutboxEventRepository extends MongoRepository<OutboxEventDocument, String> {

    List<OutboxEventDocument> findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}
