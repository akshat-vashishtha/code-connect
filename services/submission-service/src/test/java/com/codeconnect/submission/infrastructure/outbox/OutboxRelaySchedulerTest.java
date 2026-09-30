package com.codeconnect.submission.infrastructure.outbox;

import com.codeconnect.submission.domain.enums.OutboxEventStatus;
import com.codeconnect.submission.domain.model.OutboxEventDocument;
import com.codeconnect.submission.domain.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRelaySchedulerTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ObjectMapper objectMapper;
    private OutboxRelayScheduler scheduler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        scheduler = new OutboxRelayScheduler(outboxEventRepository, kafkaTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should do nothing when no pending outbox events exist")
    void shouldDoNothingWhenNoPendingEvents() {
        when(outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING))
            .thenReturn(Collections.emptyList());

        scheduler.relay();

        verifyNoInteractions(kafkaTemplate);
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should publish pending event to Kafka and mark as PUBLISHED")
    void shouldPublishPendingEventAndMarkPublished() {
        OutboxEventDocument event = OutboxEventDocument.builder()
            .id("event-123")
            .status(OutboxEventStatus.PENDING)
            .aggregateType("Submission")
            .aggregateId("sub-1")
            .topic("test.submission.topic")
            .partitionKey("sub-1")
            .payloadJson("{\"key\":\"val\"}")
            .retryCount(0)
            .createdAt(Instant.now())
            .build();

        when(outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING))
            .thenReturn(List.of(event));

        RecordMetadata metadata = new RecordMetadata(new TopicPartition("test.submission.topic", 0), 0, 0, 0, 0, 0);
        SendResult<String, Object> sendResult = new SendResult<>(null, metadata);
        when(kafkaTemplate.send(eq("test.submission.topic"), eq("sub-1"), any()))
            .thenReturn(CompletableFuture.completedFuture(sendResult));

        scheduler.relay();

        verify(kafkaTemplate).send(eq("test.submission.topic"), eq("sub-1"), any());
        verify(outboxEventRepository).save(event);
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        assertThat(event.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should increment retryCount and record lastError on Kafka send failure")
    void shouldHandleFailureAndIncrementRetryCount() {
        OutboxEventDocument event = OutboxEventDocument.builder()
            .id("event-456")
            .status(OutboxEventStatus.PENDING)
            .aggregateType("Submission")
            .aggregateId("sub-2")
            .topic("test.submission.topic")
            .partitionKey("sub-2")
            .payloadJson("{\"key\":\"val\"}")
            .retryCount(1)
            .createdAt(Instant.now())
            .build();

        when(outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING))
            .thenReturn(List.of(event));

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Broker unreachable"));
        when(kafkaTemplate.send(eq("test.submission.topic"), eq("sub-2"), any()))
            .thenReturn(failedFuture);

        scheduler.relay();

        verify(outboxEventRepository).save(event);
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(event.getRetryCount()).isEqualTo(2);
        assertThat(event.getLastError()).contains("Broker unreachable");
    }

    @Test
    @DisplayName("Should mark event as FAILED when max retries are exceeded")
    void shouldMarkFailedWhenMaxRetriesExceeded() {
        OutboxEventDocument event = OutboxEventDocument.builder()
            .id("event-789")
            .status(OutboxEventStatus.PENDING)
            .aggregateType("Submission")
            .aggregateId("sub-3")
            .topic("test.submission.topic")
            .partitionKey("sub-3")
            .payloadJson("{\"key\":\"val\"}")
            .retryCount(4)
            .createdAt(Instant.now())
            .build();

        when(outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING))
            .thenReturn(List.of(event));

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Connection refused"));
        when(kafkaTemplate.send(eq("test.submission.topic"), eq("sub-3"), any()))
            .thenReturn(failedFuture);

        scheduler.relay();

        verify(outboxEventRepository).save(event);
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(event.getRetryCount()).isEqualTo(5);
    }
}
