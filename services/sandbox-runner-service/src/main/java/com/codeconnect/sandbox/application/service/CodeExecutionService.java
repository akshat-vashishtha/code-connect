package com.codeconnect.sandbox.application.service;

import com.codeconnect.sandbox.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.event.EventEnvelope;

/**
 * Service facade contract for orchestrating sandboxed code execution workflows.
 * Decouples Kafka transport listeners from execution strategies and result publishing.
 */
public interface CodeExecutionService {

    EventEnvelope<CodeExecutionCompletedPayload> processExecution(EventEnvelope<CodeExecutionRequestedPayload> envelope);
}
