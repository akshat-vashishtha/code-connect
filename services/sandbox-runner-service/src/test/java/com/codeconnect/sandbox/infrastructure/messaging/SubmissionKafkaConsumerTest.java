package com.codeconnect.sandbox.infrastructure.messaging;

import com.codeconnect.sandbox.application.service.CodeExecutionService;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.event.EventEnvelope;
import com.codeconnect.sandbox.domain.event.EventHeader;
import com.codeconnect.sandbox.infrastructure.messaging.idempotency.EventIdempotencyGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionKafkaConsumerTest {

    @Mock
    private CodeExecutionService executionService;

    private EventIdempotencyGuard idempotencyGuard;
    private SubmissionKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        idempotencyGuard = new EventIdempotencyGuard();
        consumer = new SubmissionKafkaConsumer(executionService, idempotencyGuard);
    }

    @Test
    @DisplayName("Should consume submission envelope and delegate to CodeExecutionService")
    void shouldConsumeAndDelegateExecution() {
        EventHeader header = new EventHeader(
            "evt-100",
            "CodeExecutionRequestedEvent",
            "corr-999",
            "submission-service",
            "1.0",
            Instant.now(),
            "test"
        );
        CodeExecutionRequestedPayload submission = new CodeExecutionRequestedPayload(
            "sub-1", "student-1", "s@codeconnect.dev", "foothold-1", "class Sol {}", "JAVA", Instant.now()
        );
        EventEnvelope<CodeExecutionRequestedPayload> envelope = new EventEnvelope<>(header, submission);

        consumer.consumeSubmission(envelope);

        verify(executionService).processExecution(envelope);
    }

    @Test
    @DisplayName("Should drop duplicate events on repeated delivery")
    void shouldDropDuplicateEvents() {
        EventHeader header = new EventHeader(
            "evt-duplicate-1",
            "CodeExecutionRequestedEvent",
            "corr-999",
            "submission-service",
            "1.0",
            Instant.now(),
            "test"
        );
        CodeExecutionRequestedPayload submission = new CodeExecutionRequestedPayload(
            "sub-1", "student-1", "s@codeconnect.dev", "foothold-1", "class Sol {}", "JAVA", Instant.now()
        );
        EventEnvelope<CodeExecutionRequestedPayload> envelope = new EventEnvelope<>(header, submission);

        // First delivery: processed
        consumer.consumeSubmission(envelope);
        verify(executionService, times(1)).processExecution(envelope);

        // Second delivery with identical eventId: dropped
        consumer.consumeSubmission(envelope);
        verify(executionService, times(1)).processExecution(envelope);
    }
}
