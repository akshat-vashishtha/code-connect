package com.codeconnect.submission.infrastructure.outbox;

import com.codeconnect.submission.domain.enums.OutboxEventStatus;
import com.codeconnect.submission.domain.event.EventEnvelope;
import com.codeconnect.submission.domain.model.OutboxEventDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Collaborator responsible for assembling an {@link OutboxEventDocument} from a domain {@link EventEnvelope}.
 * Encapsulates JSON serialization and outbox entry construction — single responsibility.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventAssembler {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a PENDING {@link OutboxEventDocument} ready for atomic persistence alongside
     * the business aggregate in the same MongoDB write.
     *
     * @param envelope     the event envelope to persist as the outbox payload
     * @param topic        the Kafka topic the relay will publish to
     * @param aggregateType human-readable name of the aggregate (e.g. "Submission")
     * @param aggregateId  the business entity ID for tracing
     * @param partitionKey the Kafka partition key (e.g. submissionId)
     * @return a PENDING {@link OutboxEventDocument}
     */
    public OutboxEventDocument assemble(
            EventEnvelope<?> envelope,
            String topic,
            String aggregateType,
            String aggregateId,
            String partitionKey) {

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                "Outbox assembler: failed to serialize EventEnvelope for aggregateId=" + aggregateId, ex);
        }

        return OutboxEventDocument.builder()
            .id(UUID.randomUUID().toString())
            .status(OutboxEventStatus.PENDING)
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(envelope.header().eventType())
            .topic(topic)
            .partitionKey(partitionKey)
            .payloadJson(payloadJson)
            .retryCount(0)
            .createdAt(Instant.now())
            .build();
    }
}
