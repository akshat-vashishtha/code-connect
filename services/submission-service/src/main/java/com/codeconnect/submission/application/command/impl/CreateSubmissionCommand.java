package com.codeconnect.submission.application.command.impl;

import com.codeconnect.submission.application.command.DomainCommand;
import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.mapper.SubmissionMapper;
import com.codeconnect.submission.application.validator.SubmissionValidator;
import com.codeconnect.submission.domain.enums.SubmissionStatus;
import com.codeconnect.submission.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.submission.domain.event.EventEnvelope;
import com.codeconnect.submission.domain.event.EventHeader;
import com.codeconnect.submission.domain.model.OutboxEventDocument;
import com.codeconnect.submission.domain.model.SubmissionDocument;
import com.codeconnect.submission.domain.repository.OutboxEventRepository;
import com.codeconnect.submission.domain.repository.SubmissionRepository;
import com.codeconnect.submission.infrastructure.config.properties.KafkaTopicProperties;
import com.codeconnect.submission.infrastructure.outbox.OutboxEventAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

/**
 * Command encapsulating the ingestion, MongoDB persistence, and Transactional Outbox
 * registration of a student submission.
 *
 * <p>The Transactional Outbox Pattern is applied here:
 * <ol>
 *   <li>The {@link SubmissionDocument} is persisted with status {@code PENDING}.</li>
 *   <li>An {@link OutboxEventDocument} is persisted atomically in the same MongoDB write call chain.</li>
 *   <li>The {@link com.codeconnect.submission.infrastructure.outbox.OutboxRelayScheduler} later
 *       picks up PENDING outbox entries and forwards them to Kafka — guaranteeing at-least-once delivery
 *       without the dual-write risk.</li>
 * </ol>
 *
 * <p>There is intentionally no {@link com.codeconnect.submission.infrastructure.messaging.SubmissionKafkaProducer}
 * call in this command — Kafka publishing is fully delegated to the outbox relay.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateSubmissionCommand implements DomainCommand<SubmissionResponse> {

    private final CreateSubmissionRequest request;
    private final String studentId;
    private final String studentEmail;
    private final SubmissionValidator validator;
    private final SubmissionRepository submissionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventAssembler outboxEventAssembler;
    private final SubmissionMapper mapper;
    private final KafkaTopicProperties topicProperties;

    @Override
    public SubmissionResponse execute() {
        log.info("Executing CreateSubmissionCommand for studentId={} footholdId={}", studentId, request.footholdId());

        // 1. Validate payload invariants
        validator.validate(request);

        // 2. Persist initial PENDING aggregate to MongoDB
        Instant now = Instant.now();
        SubmissionDocument document = SubmissionDocument.builder()
            .studentId(studentId)
            .studentEmail(studentEmail)
            .footholdId(request.footholdId())
            .code(request.code())
            .language(request.language() != null ? request.language() : "JAVA")
            .status(SubmissionStatus.PENDING)
            .createdAt(now)
            .build();

        SubmissionDocument savedDocument = submissionRepository.save(document);

        // 3. Assemble CloudEvents-compliant EventEnvelope
        String eventId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        String env = topicProperties.env() != null ? topicProperties.env() : "dev";

        EventHeader header = new EventHeader(
            eventId,
            "CodeExecutionRequestedEvent",
            correlationId,
            "submission-service",
            "1.0",
            now,
            env
        );

        CodeExecutionRequestedPayload payload = new CodeExecutionRequestedPayload(
            savedDocument.getId(),
            savedDocument.getStudentId(),
            savedDocument.getStudentEmail(),
            savedDocument.getFootholdId(),
            savedDocument.getCode(),
            savedDocument.getLanguage(),
            now
        );

        EventEnvelope<CodeExecutionRequestedPayload> envelope = new EventEnvelope<>(header, payload);

        // 4. Write the outbox entry atomically alongside the submission
        // The OutboxRelayScheduler will forward this to Kafka — no dual-write risk
        OutboxEventDocument outboxEvent = outboxEventAssembler.assemble(
            envelope,
            topicProperties.topics().submissionRequested(),
            "Submission",
            savedDocument.getId(),
            savedDocument.getId()
        );
        outboxEventRepository.save(outboxEvent);

        log.info("Submission id={} persisted with PENDING outbox event id={} for Kafka relay",
            savedDocument.getId(), outboxEvent.getId());

        return mapper.toResponse(savedDocument);
    }
}
