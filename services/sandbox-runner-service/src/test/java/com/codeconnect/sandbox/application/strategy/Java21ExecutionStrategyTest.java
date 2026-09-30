package com.codeconnect.sandbox.application.strategy;

import com.codeconnect.sandbox.application.strategy.impl.Java21ExecutionStrategy;
import com.codeconnect.sandbox.domain.enums.ExecutionStatus;
import com.codeconnect.sandbox.domain.event.CodeExecutionRequestedPayload;
import com.codeconnect.sandbox.domain.model.ExecutionResult;
import com.codeconnect.sandbox.infrastructure.config.properties.SandboxProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class Java21ExecutionStrategyTest {

    private Java21ExecutionStrategy strategy;

    @BeforeEach
    void setUp() {
        SandboxProperties properties = new SandboxProperties("code.submissions", "code.results", 2000, 128);
        strategy = new Java21ExecutionStrategy(properties);
    }

    @Test
    @DisplayName("Should compile and execute valid Java 21 code successfully")
    void shouldExecuteValidJavaCode() {
        String validCode = """
            public class Solution {
                public static void main(String[] args) {
                    System.out.println("Result: 42");
                }
            }
            """;

        CodeExecutionRequestedPayload payload = new CodeExecutionRequestedPayload(
            "sub-1", "student-1", "student@codeconnect.dev", "foothold-1", validCode, "JAVA", Instant.now()
        );

        ExecutionResult result = strategy.execute(payload);

        assertThat(result.status()).isEqualTo(ExecutionStatus.PASSED);
        assertThat(result.passedTests()).isEqualTo(1);
        assertThat(result.stdout()).contains("Result: 42");
    }

    @Test
    @DisplayName("Should detect and report compilation syntax errors")
    void shouldReportCompilationError() {
        String invalidSyntaxCode = """
            public class Solution {
                public static void main(String[] args) {
                    int x = ; // Missing expression
                }
            }
            """;

        CodeExecutionRequestedPayload payload = new CodeExecutionRequestedPayload(
            "sub-2", "student-1", "student@codeconnect.dev", "foothold-1", invalidSyntaxCode, "JAVA", Instant.now()
        );

        ExecutionResult result = strategy.execute(payload);

        assertThat(result.status()).isEqualTo(ExecutionStatus.COMPILATION_ERROR);
        assertThat(result.stderr()).contains("Line");
    }

    @Test
    @DisplayName("Should terminate infinite loop within timeout threshold and report TIMED_OUT")
    void shouldTerminateInfiniteLoop() {
        String infiniteLoopCode = """
            public class Solution {
                public static void main(String[] args) {
                    while (true) {
                        // Busy spin
                    }
                }
            }
            """;

        CodeExecutionRequestedPayload payload = new CodeExecutionRequestedPayload(
            "sub-3", "student-1", "student@codeconnect.dev", "foothold-1", infiniteLoopCode, "JAVA", Instant.now()
        );

        ExecutionResult result = strategy.execute(payload);

        assertThat(result.status()).isEqualTo(ExecutionStatus.TIMED_OUT);
        assertThat(result.stderr()).contains("TimeLimitExceeded");
    }

    @Test
    @DisplayName("Should handle runtime exception and report RUNTIME_ERROR")
    void shouldHandleRuntimeException() {
        String runtimeErrorCode = """
            public class Solution {
                public static void main(String[] args) {
                    throw new IllegalArgumentException("Invalid state in student code");
                }
            }
            """;

        CodeExecutionRequestedPayload payload = new CodeExecutionRequestedPayload(
            "sub-4", "student-1", "student@codeconnect.dev", "foothold-1", runtimeErrorCode, "JAVA", Instant.now()
        );

        ExecutionResult result = strategy.execute(payload);

        assertThat(result.status()).isEqualTo(ExecutionStatus.RUNTIME_ERROR);
        assertThat(result.stderr()).contains("Invalid state in student code");
    }
}
