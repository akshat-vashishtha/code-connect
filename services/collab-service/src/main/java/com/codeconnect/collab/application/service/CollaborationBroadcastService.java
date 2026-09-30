package com.codeconnect.collab.application.service;

import com.codeconnect.collab.application.dto.response.SubmissionResultResponse;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;

/**
 * Service facade contract for broadcasting real-time collaboration events.
 * Decouples transport consumers from WebSocket presentation relay logic.
 */
public interface CollaborationBroadcastService {

    SubmissionResultResponse broadcastExecutionResult(CodeExecutionCompletedPayload payload);
}
