package com.codeconnect.sandbox.application.service.impl;

import com.codeconnect.sandbox.application.service.CodeExecutionService;
import com.codeconnect.sandbox.application.strategy.ExecutionStrategy;
import com.codeconnect.sandbox.application.strategy.registry.ExecutionStrategyRegistry;
import com.codeconnect.sandbox.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.event.EventEnvelope;
import com.codeconnect.sandbox.domain.event.EventHeader;
import com.codeconnect.sandbox.domain.model.ExecutionResult;
import com.codeconnect.sandbox.infrastructure.config.properties.KafkaTopicProperties;
import com.codeconnect.sandbox.infrastructure.messaging.ExecutionResultKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service facade implementation orchestrating strategy resolution, sandboxed execution,
 * result envelope encapsulation, and Kafka producer delegation at SLAP.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeExecutionServiceImpl implements CodeExecutionService {

    private final ExecutionStrategyRegistry strategyRegistry;
    private final ExecutionResultKafkaProducer resultProducer;
    private final KafkaTopicProperties topicProperties;

    @Override
    public EventEnvelope<CodeExecutionCompletedPayload> processExecution(EventEnvelope<CodeExecutionRequestedPayload> envelope) {
        CodeExecutionRequestedPayload submission = envelope.payload();
        EventHeader inboundHeader = envelope.header();

        log.info("Processing execution for submissionId={} studentId={} footholdId={}",
            submission.submissionId(), submission.studentId(), submission.footholdId());

        ExecutionStrategy strategy = strategyRegistry.resolve(submission.language());
        ExecutionResult result = strategy.execute(submission);

        Instant now = Instant.now();
        EventHeader outHeader = new EventHeader(
            UUID.randomUUID().toString(),
            "CodeExecutionCompletedEvent",
            inboundHeader.correlationId(),
            "sandbox-runner-service",
            "1.0",
            now,
            topicProperties.env() != null ? topicProperties.env() : "dev"
        );

        CodeExecutionCompletedPayload payload = new CodeExecutionCompletedPayload(
            submission.submissionId(),
            submission.studentId(),
            submission.studentEmail(),
            submission.footholdId(),
            result.status(),
            result.totalTests(),
            result.passedTests(),
            result.testResults(),
            result.stdout(),
            result.stderr(),
            result.durationMs(),
            now
        );

        EventEnvelope<CodeExecutionCompletedPayload> outEnvelope = new EventEnvelope<>(outHeader, payload);
        resultProducer.publishExecutionResult(outEnvelope);
        return outEnvelope;
    }
}
