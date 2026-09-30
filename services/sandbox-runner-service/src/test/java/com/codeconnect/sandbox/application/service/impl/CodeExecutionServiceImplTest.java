package com.codeconnect.sandbox.application.service.impl;

import com.codeconnect.sandbox.application.strategy.ExecutionStrategy;
import com.codeconnect.sandbox.application.strategy.registry.ExecutionStrategyRegistry;
import com.codeconnect.sandbox.domain.enums.ExecutionStatus;
import com.codeconnect.sandbox.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.event.EventEnvelope;
import com.codeconnect.sandbox.domain.event.EventHeader;
import com.codeconnect.sandbox.domain.model.ExecutionResult;
import com.codeconnect.sandbox.infrastructure.config.properties.KafkaTopicProperties;
import com.codeconnect.sandbox.infrastructure.messaging.ExecutionResultKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeExecutionServiceImplTest {

    @Mock
    private ExecutionStrategyRegistry strategyRegistry;

    @Mock
    private ExecutionResultKafkaProducer resultProducer;

    @Mock
    private ExecutionStrategy executionStrategy;

    private CodeExecutionServiceImpl service;

    @BeforeEach
    void setUp() {
        KafkaTopicProperties topicProps = new KafkaTopicProperties(
            "dev",
            new KafkaTopicProperties.Topics("sub.topic", "comp.topic", "dlq.sub", "dlq.comp")
        );
        service = new CodeExecutionServiceImpl(strategyRegistry, resultProducer, topicProps);
    }

    @Test
    @DisplayName("Should orchestrate strategy execution and publish completed EventEnvelope")
    void shouldProcessExecutionAndPublishEnvelope() {
        EventHeader inboundHeader = new EventHeader(
            "evt-100", "CodeExecutionRequestedEvent", "corr-100", "submission-service", "1.0", Instant.now(), "dev"
        );

        CodeExecutionRequestedPayload submission = new CodeExecutionRequestedPayload(
            "sub-100", "student-1", "student@codeconnect.dev", "foothold-1",
            "public class Solution {}", "JAVA", Instant.now()
        );

        EventEnvelope<CodeExecutionRequestedPayload> envelope = new EventEnvelope<>(inboundHeader, submission);

        ExecutionResult executionResult = new ExecutionResult(
            ExecutionStatus.PASSED, 2, 2, Collections.emptyList(), "Passed", "", 120
        );

        when(strategyRegistry.resolve("JAVA")).thenReturn(executionStrategy);
        when(executionStrategy.execute(submission)).thenReturn(executionResult);

        EventEnvelope<CodeExecutionCompletedPayload> resultEnvelope = service.processExecution(envelope);

        assertThat(resultEnvelope).isNotNull();
        assertThat(resultEnvelope.payload().submissionId()).isEqualTo("sub-100");
        assertThat(resultEnvelope.payload().status()).isEqualTo(ExecutionStatus.PASSED);
        assertThat(resultEnvelope.header().correlationId()).isEqualTo("corr-100");

        ArgumentCaptor<EventEnvelope<CodeExecutionCompletedPayload>> captor = ArgumentCaptor.forClass(EventEnvelope.class);
        verify(resultProducer).publishExecutionResult(captor.capture());
        assertThat(captor.getValue().payload().submissionId()).isEqualTo("sub-100");
    }
}
