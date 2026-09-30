package com.codeconnect.collab.application.mapper;

import com.codeconnect.collab.application.dto.response.SanitizedTestResultDto;
import com.codeconnect.collab.application.dto.response.SubmissionResultResponse;
import com.codeconnect.collab.domain.enums.ExecutionStatus;
import com.codeconnect.collab.domain.event.CodeExecutionCompletedPayload;
import com.codeconnect.collab.domain.model.TestResultDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubmissionResultMapperTest {

    private SubmissionResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SubmissionResultMapper();
    }

    @Test
    @DisplayName("Should map and sanitize visible and hidden test results correctly for CodeExecutionCompletedPayload")
    void shouldSanitizeVisibleAndHiddenTestResultsForPayload() {
        TestResultDetail visiblePass = new TestResultDetail(
                "tc-1", "input(1, 2)", "3", "3", true, false, 15, null
        );
        TestResultDetail hiddenPass = new TestResultDetail(
                "tc-2", "secret_input(999, 1)", "1000", "1000", true, true, 20, null
        );
        TestResultDetail hiddenFail = new TestResultDetail(
                "tc-3", "secret_input(0, 0)", "0", "-1", false, true, 18, "Assertion failed"
        );

        CodeExecutionCompletedPayload payload = new CodeExecutionCompletedPayload(
                "sub-123",
                "user-456",
                "user@codeconnect.dev",
                "foothold-abc",
                ExecutionStatus.FAILED,
                3,
                2,
                List.of(visiblePass, hiddenPass, hiddenFail),
                "Standard output",
                "Error output",
                120,
                Instant.now()
        );

        SubmissionResultResponse response = mapper.toResponse(payload);

        assertThat(response).isNotNull();
        assertThat(response.submissionId()).isEqualTo("sub-123");
        assertThat(response.footholdId()).isEqualTo("foothold-abc");
        assertThat(response.status()).isEqualTo(ExecutionStatus.FAILED);
        assertThat(response.allPassed()).isFalse();
        assertThat(response.testResults()).hasSize(3);

        // Visible test check
        SanitizedTestResultDto visibleDto = response.testResults().get(0);
        assertThat(visibleDto.input()).isEqualTo("input(1, 2)");
        assertThat(visibleDto.expectedOutput()).isEqualTo("3");
        assertThat(visibleDto.actualOutput()).isEqualTo("3");
        assertThat(visibleDto.passed()).isTrue();
        assertThat(visibleDto.hidden()).isFalse();

        // Hidden test checks (redacted)
        SanitizedTestResultDto hiddenPassDto = response.testResults().get(1);
        assertThat(hiddenPassDto.input()).isEqualTo("[Hidden Test Case]");
        assertThat(hiddenPassDto.expectedOutput()).isEqualTo("[Hidden]");
        assertThat(hiddenPassDto.actualOutput()).isEqualTo("[Hidden]");
        assertThat(hiddenPassDto.passed()).isTrue();
        assertThat(hiddenPassDto.hidden()).isTrue();

        SanitizedTestResultDto hiddenFailDto = response.testResults().get(2);
        assertThat(hiddenFailDto.input()).isEqualTo("[Hidden Test Case]");
        assertThat(hiddenFailDto.expectedOutput()).isEqualTo("[Hidden]");
        assertThat(hiddenFailDto.passed()).isFalse();
        assertThat(hiddenFailDto.hidden()).isTrue();
    }

    @Test
    @DisplayName("Should mark allPassed as true when all tests pass and status is PASSED for payload")
    void shouldMarkAllPassedAsTrueForPayload() {
        TestResultDetail visiblePass = new TestResultDetail(
                "tc-1", "input(1, 2)", "3", "3", true, false, 15, null
        );
        CodeExecutionCompletedPayload payload = new CodeExecutionCompletedPayload(
                "sub-999",
                "user-456",
                "user@codeconnect.dev",
                "foothold-xyz",
                ExecutionStatus.PASSED,
                1,
                1,
                List.of(visiblePass),
                "Output",
                "",
                45,
                Instant.now()
        );

        SubmissionResultResponse response = mapper.toResponse(payload);

        assertThat(response).isNotNull();
        assertThat(response.allPassed()).isTrue();
        assertThat(response.status()).isEqualTo(ExecutionStatus.PASSED);
    }
}
