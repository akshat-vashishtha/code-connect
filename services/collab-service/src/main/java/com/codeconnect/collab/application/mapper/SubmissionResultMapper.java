package com.codeconnect.collab.application.mapper;

import com.codeconnect.collab.application.dto.response.SanitizedTestResultDto;
import com.codeconnect.collab.application.dto.response.SubmissionResultResponse;
import com.codeconnect.collab.domain.enums.ExecutionStatus;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.collab.domain.model.TestResultDetail;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper adapting internal execution results into sanitized, client-safe WebSocket payloads.
 * Protects hidden test cases by redacting inputs and expected outputs before broadcast.
 */
@Component
public class SubmissionResultMapper {

    private static final String HIDDEN_REDACTED_INPUT = "[Hidden Test Case]";
    private static final String HIDDEN_REDACTED_OUTPUT = "[Hidden]";

    public SubmissionResultResponse toResponse(CodeExecutionCompletedPayload payload) {
        if (payload == null) {
            return null;
        }

        List<TestResultDetail> rawResults = payload.testResults() != null ? payload.testResults() : Collections.emptyList();
        List<SanitizedTestResultDto> sanitizedList = rawResults.stream()
                .map(this::sanitizeTestResult)
                .toList();

        boolean allPassed = payload.status() == ExecutionStatus.PASSED &&
                !sanitizedList.isEmpty() &&
                sanitizedList.stream().allMatch(SanitizedTestResultDto::passed);

        String compilerOutput = payload.stderr() != null && !payload.stderr().isBlank()
                ? payload.stderr()
                : payload.stdout();

        return new SubmissionResultResponse(
                payload.submissionId(),
                payload.footholdId(),
                payload.studentId(),
                payload.status(),
                compilerOutput,
                sanitizedList,
                payload.durationMs(),
                allPassed,
                payload.completedAt()
        );
    }

    private SanitizedTestResultDto sanitizeTestResult(TestResultDetail raw) {
        if (raw == null) {
            return null;
        }

        if (raw.hidden()) {
            return new SanitizedTestResultDto(
                    raw.testCaseId(),
                    HIDDEN_REDACTED_INPUT,
                    HIDDEN_REDACTED_OUTPUT,
                    raw.passed() ? HIDDEN_REDACTED_OUTPUT : (raw.errorMessage() != null ? "[Error: Check constraints]" : HIDDEN_REDACTED_OUTPUT),
                    raw.passed(),
                    true,
                    raw.executionTimeMs(),
                    raw.passed() ? null : "Hidden test failed."
            );
        }

        return new SanitizedTestResultDto(
                raw.testCaseId(),
                raw.input(),
                raw.expectedOutput(),
                raw.actualOutput(),
                raw.passed(),
                false,
                raw.executionTimeMs(),
                raw.errorMessage()
        );
    }
}
