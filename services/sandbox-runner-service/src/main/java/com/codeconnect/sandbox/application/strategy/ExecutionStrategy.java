package com.codeconnect.sandbox.application.strategy;

import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.model.ExecutionResult;

/**
 * Strategy pattern contract for multi-language sandboxed execution.
 */
public interface ExecutionStrategy {

    String getSupportedLanguage();

    ExecutionResult execute(CodeExecutionRequestedPayload submission);
}
