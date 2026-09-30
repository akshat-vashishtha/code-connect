package com.codeconnect.collab.infrastructure.messaging;

import com.codeconnect.collab.application.service.CollaborationBroadcastService;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.collab.domain.event.EventEnvelope;
import com.codeconnect.collab.infrastructure.messaging.idempotency.EventIdempotencyGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer listening to execution completion topics and delegating broadcast
 * to CollaborationBroadcastService.
 *
 * <p>Applies the idempotency guard ({@link EventIdempotencyGuard}) to skip duplicate
 * deliveries and prevent double-broadcast over STOMP to WebSocket clients.
 * AckMode is RECORD (configured in application.yml) — offset committed only after
 * {@code consumeExecutionResult} returns successfully.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionResultKafkaConsumer {

    private final CollaborationBroadcastService broadcastService;
    private final EventIdempotencyGuard idempotencyGuard;

    @KafkaListener(
            topics = "${codeconnect.kafka.topics.execution-completed:${codeconnect.collab.results-topic:code.results}}",
            groupId = "${spring.kafka.consumer.group-id:collab-group}"
    )
    public void consumeExecutionResult(EventEnvelope<CodeExecutionCompletedPayload> envelope) {
        String eventId = envelope.header().eventId();
        CodeExecutionCompletedPayload payload = envelope.payload();

        log.info("Received ExecutionResult EventEnvelope eventId={} correlationId={} submissionId={}, footholdId={}, status={}",
                eventId, envelope.header().correlationId(), payload.submissionId(), payload.footholdId(), payload.status());

        if (idempotencyGuard.isDuplicate(eventId)) {
            return;
        }

        broadcastService.broadcastExecutionResult(payload);
        idempotencyGuard.markProcessed(eventId);
    }
}
