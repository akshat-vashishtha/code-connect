package com.codeconnect.collab.application.service.impl;

import com.codeconnect.collab.application.dto.response.SubmissionResultResponse;
import com.codeconnect.collab.application.mapper.SubmissionResultMapper;
import com.codeconnect.collab.application.service.CollaborationBroadcastService;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service facade implementation orchestrating test result sanitization and STOMP broadcast.
 * Operates at a Single Level of Abstraction (SLAP).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollaborationBroadcastServiceImpl implements CollaborationBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SubmissionResultMapper submissionResultMapper;

    @Override
    public SubmissionResultResponse broadcastExecutionResult(CodeExecutionCompletedPayload payload) {
        SubmissionResultResponse response = submissionResultMapper.toResponse(payload);
        if (response == null) {
            log.warn("Null response generated from execution payload for submissionId={}", payload != null ? payload.submissionId() : "unknown");
            return null;
        }

        String destination = "/topic/submissions." + payload.submissionId();
        messagingTemplate.convertAndSend(destination, response);

        log.info("Broadcasted submission result to STOMP destination: {} (allPassed={}, duration={}ms)",
                destination, response.allPassed(), response.totalDurationMs());
        return response;
    }
}
