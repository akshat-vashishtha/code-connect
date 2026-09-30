package com.codeconnect.submission.domain.model;

import com.codeconnect.submission.domain.enums.OutboxEventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Outbox document persisted atomically alongside the business entity in the same MongoDB write.
 * The polling relay reads PENDING entries and publishes them to Kafka, then marks PUBLISHED.
 * Implements the Transactional Outbox Pattern to guarantee at-least-once delivery without dual-write risk.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "outbox_events")
public class OutboxEventDocument {

    @Id
    private String id;

    @Indexed
    private OutboxEventStatus status;

    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String topic;
    private String partitionKey;
    private String payloadJson;
    private int retryCount;
    private String lastError;
    private Instant createdAt;
    private Instant processedAt;
}
