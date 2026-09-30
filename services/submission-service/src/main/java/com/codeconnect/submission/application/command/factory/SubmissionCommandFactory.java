package com.codeconnect.submission.application.command.factory;

import com.codeconnect.submission.application.command.DomainCommand;
import com.codeconnect.submission.application.command.impl.CreateSubmissionCommand;
import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.mapper.SubmissionMapper;
import com.codeconnect.submission.application.validator.SubmissionValidator;
import com.codeconnect.submission.domain.repository.OutboxEventRepository;
import com.codeconnect.submission.domain.repository.SubmissionRepository;
import com.codeconnect.submission.infrastructure.config.properties.KafkaTopicProperties;
import com.codeconnect.submission.infrastructure.outbox.OutboxEventAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory responsible for instantiating DomainCommand instances with injected Spring singleton collaborators.
 * Wires the Transactional Outbox dependencies ({@link OutboxEventRepository}, {@link OutboxEventAssembler})
 * instead of the former direct {@link com.codeconnect.submission.infrastructure.messaging.SubmissionKafkaProducer} reference.
 */
@Component
@RequiredArgsConstructor
public class SubmissionCommandFactory {

    private final SubmissionValidator validator;
    private final SubmissionRepository submissionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventAssembler outboxEventAssembler;
    private final SubmissionMapper mapper;
    private final KafkaTopicProperties topicProperties;

    public DomainCommand<SubmissionResponse> createSubmissionCommand(
            CreateSubmissionRequest request,
            String studentId,
            String studentEmail) {

        return new CreateSubmissionCommand(
            request,
            studentId,
            studentEmail,
            validator,
            submissionRepository,
            outboxEventRepository,
            outboxEventAssembler,
            mapper,
            topicProperties
        );
    }
}
