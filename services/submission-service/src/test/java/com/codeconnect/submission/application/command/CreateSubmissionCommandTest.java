package com.codeconnect.submission.application.command;

import com.codeconnect.submission.application.command.impl.CreateSubmissionCommand;
import com.codeconnect.submission.application.dto.request.CreateSubmissionRequest;
import com.codeconnect.submission.application.dto.response.SubmissionResponse;
import com.codeconnect.submission.application.mapper.SubmissionMapper;
import com.codeconnect.submission.application.validator.SubmissionValidator;
import com.codeconnect.submission.domain.enums.OutboxEventStatus;
import com.codeconnect.submission.domain.enums.SubmissionStatus;
import com.codeconnect.submission.domain.model.OutboxEventDocument;
import com.codeconnect.submission.domain.model.SubmissionDocument;
import com.codeconnect.submission.domain.repository.OutboxEventRepository;
import com.codeconnect.submission.domain.repository.SubmissionRepository;
import com.codeconnect.submission.infrastructure.config.properties.KafkaTopicProperties;
import com.codeconnect.submission.infrastructure.outbox.OutboxEventAssembler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubmissionCommandTest {

    @Mock
    private SubmissionValidator validator;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    private KafkaTopicProperties topicProperties;
    private SubmissionMapper mapper;
    private OutboxEventAssembler outboxEventAssembler;
    private CreateSubmissionCommand command;

    @BeforeEach
    void setUp() {
        mapper = new SubmissionMapper();
        outboxEventAssembler = new OutboxEventAssembler(new ObjectMapper().findAndRegisterModules());
        topicProperties = new KafkaTopicProperties(
            "test",
            new KafkaTopicProperties.Topics(
                "test.codeconnect.submission.code-execution.requested.v1",
                "test.codeconnect.submission.code-execution.dlq.v1"
            )
        );
        CreateSubmissionRequest request = new CreateSubmissionRequest("foothold-123", "public class Solution {}", "JAVA");
        command = new CreateSubmissionCommand(
            request,
            "student-456",
            "student@codeconnect.dev",
            validator,
            submissionRepository,
            outboxEventRepository,
            outboxEventAssembler,
            mapper,
            topicProperties
        );
    }

    @Test
    @DisplayName("Should execute validation, save SubmissionDocument with PENDING status, and write atomic OutboxEventDocument")
    void shouldExecuteSubmissionSuccessfully() {
        SubmissionDocument savedDoc = SubmissionDocument.builder()
            .id("sub-999")
            .studentId("student-456")
            .studentEmail("student@codeconnect.dev")
            .footholdId("foothold-123")
            .code("public class Solution {}")
            .language("JAVA")
            .status(SubmissionStatus.PENDING)
            .createdAt(Instant.now())
            .build();

        when(submissionRepository.save(any(SubmissionDocument.class))).thenReturn(savedDoc);
        when(outboxEventRepository.save(any(OutboxEventDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmissionResponse response = command.execute();

        verify(validator).validate(any(CreateSubmissionRequest.class));
        verify(submissionRepository).save(any(SubmissionDocument.class));

        ArgumentCaptor<OutboxEventDocument> outboxCaptor = ArgumentCaptor.forClass(OutboxEventDocument.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        OutboxEventDocument capturedOutbox = outboxCaptor.getValue();
        assertThat(capturedOutbox.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(capturedOutbox.getAggregateType()).isEqualTo("Submission");
        assertThat(capturedOutbox.getAggregateId()).isEqualTo("sub-999");
        assertThat(capturedOutbox.getPartitionKey()).isEqualTo("sub-999");
        assertThat(capturedOutbox.getEventType()).isEqualTo("CodeExecutionRequestedEvent");
        assertThat(capturedOutbox.getTopic()).isEqualTo("test.codeconnect.submission.code-execution.requested.v1");
        assertThat(capturedOutbox.getPayloadJson()).isNotBlank();
        assertThat(capturedOutbox.getRetryCount()).isZero();
        assertThat(capturedOutbox.getCreatedAt()).isNotNull();

        assertThat(response.submissionId()).isEqualTo("sub-999");
        assertThat(response.status()).isEqualTo(SubmissionStatus.PENDING);
    }
}
