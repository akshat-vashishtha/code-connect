package com.codeconnect.sandbox.infrastructure.messaging;

import com.codeconnect.sandbox.application.service.CodeExecutionService;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.event.EventEnvelope;
import com.codeconnect.sandbox.infrastructure.messaging.idempotency.EventIdempotencyGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * High-performance Kafka Consumer listening to submission requests
 * and delegating execution orchestration to CodeExecutionService.
 *
 * <p>Applies the idempotency guard ({@link EventIdempotencyGuard}) to skip duplicate
 * deliveries that arise from Kafka's at-least-once delivery guarantee.
 * AckMode is RECORD (configured in application.yml) — the listener framework
 * commits the offset only after {@code consumeSubmission} returns successfully.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionKafkaConsumer {

    private final CodeExecutionService executionService;
    private final EventIdempotencyGuard idempotencyGuard;

    @KafkaListener(
        topics = "${codeconnect.kafka.topics.submission-requested:${codeconnect.sandbox.submission-topic:code.submissions}}",
        groupId = "${spring.kafka.consumer.group-id:sandbox-runner-group}"
    )
    public void consumeSubmission(EventEnvelope<CodeExecutionRequestedPayload> envelope) {
        String eventId = envelope.header().eventId();

        log.info("Received submission job: eventId={} correlationId={} submissionId={} studentId={} footholdId={}",
            eventId, envelope.header().correlationId(),
            envelope.payload().submissionId(), envelope.payload().studentId(), envelope.payload().footholdId());

        if (idempotencyGuard.isDuplicate(eventId)) {
            return;
        }

        executionService.processExecution(envelope);
        idempotencyGuard.markProcessed(eventId);
    }
}
