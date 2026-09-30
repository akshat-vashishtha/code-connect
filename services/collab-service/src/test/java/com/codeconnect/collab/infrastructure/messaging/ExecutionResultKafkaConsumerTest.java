package com.codeconnect.collab.infrastructure.messaging;

import com.codeconnect.collab.application.service.CollaborationBroadcastService;
import com.codeconnect.collab.domain.enums.ExecutionStatus;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.collab.domain.event.EventEnvelope;
import com.codeconnect.collab.domain.event.EventHeader;
import com.codeconnect.collab.domain.model.TestResultDetail;
import com.codeconnect.collab.infrastructure.messaging.idempotency.EventIdempotencyGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionResultKafkaConsumerTest {

    @Mock
    private CollaborationBroadcastService broadcastService;

    private EventIdempotencyGuard idempotencyGuard;
    private ExecutionResultKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        idempotencyGuard = new EventIdempotencyGuard();
        consumer = new ExecutionResultKafkaConsumer(broadcastService, idempotencyGuard);
    }

    @Test
    @DisplayName("Should consume ExecutionResult EventEnvelope and delegate to CollaborationBroadcastService")
    void shouldConsumeAndDelegateBroadcast() {
        String submissionId = "sub-777";
        Instant now = Instant.now();

        EventHeader header = new EventHeader(
            "evt-777",
            "CodeExecutionCompletedEvent",
            "corr-777",
            "sandbox-runner-service",
            "1.0",
            now,
            "test"
        );

        CodeExecutionCompletedPayload payload = new CodeExecutionCompletedPayload(
            submissionId,
            "student-1",
            "student@codeconnect.dev",
            "foothold-1",
            ExecutionStatus.PASSED,
            1,
            1,
            List.of(new TestResultDetail("tc-1", "x", "y", "y", true, false, 10, null)),
            "Output",
            "",
            50,
            now
        );

        EventEnvelope<CodeExecutionCompletedPayload> envelope = new EventEnvelope<>(header, payload);

        consumer.consumeExecutionResult(envelope);

        verify(broadcastService).broadcastExecutionResult(payload);
    }

    @Test
    @DisplayName("Should drop duplicate execution result events on repeated delivery")
    void shouldDropDuplicateEvents() {
        String submissionId = "sub-888";
        Instant now = Instant.now();

        EventHeader header = new EventHeader(
            "evt-duplicate-888",
            "CodeExecutionCompletedEvent",
            "corr-888",
            "sandbox-runner-service",
            "1.0",
            now,
            "test"
        );

        CodeExecutionCompletedPayload payload = new CodeExecutionCompletedPayload(
            submissionId,
            "student-1",
            "student@codeconnect.dev",
            "foothold-1",
            ExecutionStatus.PASSED,
            1,
            1,
            List.of(new TestResultDetail("tc-1", "x", "y", "y", true, false, 10, null)),
            "Output",
            "",
            50,
            now
        );

        EventEnvelope<CodeExecutionCompletedPayload> envelope = new EventEnvelope<>(header, payload);

        // First delivery: processed
        consumer.consumeExecutionResult(envelope);
        verify(broadcastService, times(1)).broadcastExecutionResult(payload);

        // Second delivery with same eventId: skipped
        consumer.consumeExecutionResult(envelope);
        verify(broadcastService, times(1)).broadcastExecutionResult(payload);
    }
}
