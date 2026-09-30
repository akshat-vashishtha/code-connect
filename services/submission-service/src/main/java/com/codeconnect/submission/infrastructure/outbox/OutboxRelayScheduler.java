package com.codeconnect.submission.infrastructure.outbox;

import com.codeconnect.submission.domain.enums.OutboxEventStatus;
import com.codeconnect.submission.domain.model.OutboxEventDocument;
import com.codeconnect.submission.domain.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Polling relay that forms the second half of the Transactional Outbox Pattern.
 * Every fixed interval it reads PENDING outbox entries, publishes to Kafka, and marks them PUBLISHED.
 * On Kafka failure it increments retryCount and records lastError for observability.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>Fixed-rate polling (5 s default) keeps infrastructure simple — no Debezium CDC required.</li>
 *   <li>Page-size of 50 entries per sweep limits memory pressure and enables safe retry cadence.</li>
 *   <li>After {@code maxRetries} failures the entry is marked FAILED so it does not block the queue.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private static final int MAX_RETRIES = 5;

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Sweep PENDING outbox events and relay them to Kafka.
     * Runs every 5 seconds with an initial delay of 10 seconds to allow context warm-up.
     */
    @Scheduled(fixedDelayString = "${codeconnect.outbox.relay-interval-ms:5000}",
               initialDelayString = "${codeconnect.outbox.initial-delay-ms:10000}")
    public void relay() {
        List<OutboxEventDocument> pendingEvents =
            outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.debug("Outbox relay: processing {} PENDING event(s)", pendingEvents.size());

        for (OutboxEventDocument event : pendingEvents) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEventDocument event) {
        try {
            Object payload = objectMapper.readValue(event.getPayloadJson(), Object.class);

            kafkaTemplate.send(event.getTopic(), event.getPartitionKey(), payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        handlePublishFailure(event, ex.getMessage());
                    } else {
                        markPublished(event);
                        log.info("Outbox relay: published eventId={} topic={} partition={} offset={}",
                            event.getId(), event.getTopic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    }
                });

        } catch (Exception ex) {
            handlePublishFailure(event, ex.getMessage());
        }
    }

    private void markPublished(OutboxEventDocument event) {
        event.setStatus(OutboxEventStatus.PUBLISHED);
        event.setProcessedAt(Instant.now());
        outboxEventRepository.save(event);
    }

    private void handlePublishFailure(OutboxEventDocument event, String errorMessage) {
        int newRetryCount = event.getRetryCount() + 1;
        log.warn("Outbox relay: failed to publish eventId={} topic={} attempt={} error={}",
            event.getId(), event.getTopic(), newRetryCount, errorMessage);

        event.setRetryCount(newRetryCount);
        event.setLastError(errorMessage);

        if (newRetryCount >= MAX_RETRIES) {
            log.error("Outbox relay: max retries ({}) exhausted for eventId={}. Marking FAILED.",
                MAX_RETRIES, event.getId());
            event.setStatus(OutboxEventStatus.FAILED);
        }

        outboxEventRepository.save(event);
    }
}
