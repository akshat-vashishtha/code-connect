package com.codeconnect.collab.application.service.impl;

import com.codeconnect.collab.application.dto.response.SubmissionResultResponse;
import com.codeconnect.collab.application.mapper.SubmissionResultMapper;
import com.codeconnect.collab.domain.enums.ExecutionStatus;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollaborationBroadcastServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SubmissionResultMapper submissionResultMapper;

    private CollaborationBroadcastServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CollaborationBroadcastServiceImpl(messagingTemplate, submissionResultMapper);
    }

    @Test
    @DisplayName("Should convert payload to response and broadcast to STOMP destination")
    void shouldBroadcastExecutionResult() {
        CodeExecutionCompletedPayload payload = new CodeExecutionCompletedPayload(
            "sub-123", "student-1", "student@codeconnect.dev", "foothold-1",
            ExecutionStatus.PASSED, 1, 1, Collections.emptyList(),
            "Output", "", 50, Instant.now()
        );

        SubmissionResultResponse response = new SubmissionResultResponse(
            "sub-123", "foothold-1", "student-1",
            ExecutionStatus.PASSED, "Output", Collections.emptyList(),
            50, true, Instant.now()
        );

        when(submissionResultMapper.toResponse(payload)).thenReturn(response);

        SubmissionResultResponse result = service.broadcastExecutionResult(payload);

        assertThat(result).isEqualTo(response);
        verify(messagingTemplate).convertAndSend("/topic/submissions.sub-123", response);
    }
}
