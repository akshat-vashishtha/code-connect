---
title: 'Story 3.3: Ephemeral Docker Sandbox Execution Worker'
type: 'feature'
created: '2026-09-30'
status: 'done'
baseline_commit: '5b1ae685f2a7b9edd423b56feecd409833de0f9f'
route: 'full'
route_source: 'auto'
review: 'thorough'
review_source: 'auto'
lenses_ran: []
review_loop_iteration: 0
context:
  - '.agents/rules/coding-standards.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Student code can be infinite-looping, memory-hogging, or malicious (attempting host access or network requests). Running unverified code directly on microservice hosts risks cluster crashes and security breaches.

**Approach:** Implement `sandbox-runner-service` (Spring Boot 3.3 / Java 21 worker) which listens to Kafka topic `code.submissions`, compiles and executes code inside isolated sandboxes with strict limits (3000ms timeout, 128MB RAM, zero network), and publishes structured `ExecutionResultEvent` records to Kafka topic `code.results`.

## Boundaries & Constraints

**Always:**
- Use the **Strategy Pattern** for language execution (`ExecutionStrategy` with `Java21ExecutionStrategy`) and a **Strategy Registry** (`ExecutionStrategyRegistry`).
- Enforce strict resource limits: 3000ms timeout cap (`TimeLimitExceeded`) and 128MB memory cap (`MemoryLimitExceeded`).
- Sanitize and block network calls (`--network none`).
- Publish an immutable Java 21 `ExecutionResultEvent` record to Kafka topic `code.results` upon completion of every job.
- Configure Dead-Letter Queue (DLQ) topic `code.submissions.dlq` for unprocessable poison messages.
- Externalize all limits and topics via pure `@ConfigurationProperties` data holders.

**Never:**
- Do not hardcode timeouts, memory limits, topic names, or docker image names in Java code.
- Do not let a single long-running or crashing student script hang the worker thread indefinitely.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Valid Passing Code | `CodeSubmissionEvent` with correct Java solution | Runs against test cases; emits `ExecutionResultEvent` with status `PASSED`, all tests passed | Log success |
| Syntax / Compile Error | `CodeSubmissionEvent` with broken syntax | Compilation failure detected; emits `ExecutionResultEvent` with status `COMPILATION_ERROR` and compiler output in stderr | No execution |
| Infinite Loop | `CodeSubmissionEvent` containing `while(true)` | Execution terminated after 3000ms; emits `ExecutionResultEvent` with status `TIMED_OUT` | Worker thread reclaimed |
| Memory Exhaustion | `CodeSubmissionEvent` allocating huge byte arrays | Execution terminated upon exceeding 128MB; emits status `MEMORY_EXCEEDED` | Worker thread reclaimed |
| Unhandled Exception | Code throws `NullPointerException` or `ArrayIndexOutOfBoundsException` | Emits `ExecutionResultEvent` with status `RUNTIME_ERROR` and stack trace snippet | Structured result captured |

</frozen-after-approval>

## Code Map

- `services/sandbox-runner-service/pom.xml` -- Spring Boot 3.3 worker project with Spring Kafka, Actuator, Micrometer.
- `services/sandbox-runner-service/src/main/resources/application.yml` -- Worker configuration for Kafka consumer group, topics, timeouts, and memory limits.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/domain/enums/ExecutionStatus.java` -- Status enum (`PASSED`, `FAILED`, `COMPILATION_ERROR`, `RUNTIME_ERROR`, `TIMED_OUT`, `MEMORY_EXCEEDED`).
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/domain/event/CodeSubmissionEvent.java` -- Inbound Kafka event record.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/domain/event/ExecutionResultEvent.java` -- Outbound Kafka event record.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/domain/model/TestResultDetail.java` -- Value object record for single test case outcomes.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/application/strategy/ExecutionStrategy.java` -- Strategy interface for code execution.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/application/strategy/Java21ExecutionStrategy.java` -- Concrete Java 21 compilation and sandboxed runner strategy.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/application/strategy/ExecutionStrategyRegistry.java` -- Strategy resolver registry.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/infrastructure/messaging/SubmissionKafkaConsumer.java` -- Kafka listener on `code.submissions`.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/infrastructure/messaging/ExecutionResultKafkaProducer.java` -- Kafka producer for `code.results`.
- `services/sandbox-runner-service/src/main/java/com/codeconnect/sandbox/infrastructure/config/SandboxProperties.java` -- Pure ConfigurationProperties data holder.
- `k8s/deployments/sandbox-runner-service-deployment.yaml` -- Kubernetes Deployment manifest for the worker.

## Tasks & Acceptance

**Execution:**
- [ ] `services/sandbox-runner-service/pom.xml` -- Initialize sandbox-runner-service module -- Project skeleton.
- [ ] `services/sandbox-runner-service/src/main/resources/application.yml` -- Setup Kafka consumer/producer and timeout properties -- Externalized configuration.
- [ ] `services/sandbox-runner-service/.../domain/` -- Implement `ExecutionStatus`, `CodeSubmissionEvent`, `ExecutionResultEvent`, and `TestResultDetail` records -- Domain contracts.
- [ ] `services/sandbox-runner-service/.../application/strategy/` -- Implement `ExecutionStrategy`, `Java21ExecutionStrategy`, and `ExecutionStrategyRegistry` -- GoF Strategy pattern for execution.
- [ ] `services/sandbox-runner-service/.../infrastructure/messaging/` -- Implement `SubmissionKafkaConsumer` and `ExecutionResultKafkaProducer` -- Event-driven pipeline.
- [ ] `services/sandbox-runner-service/Dockerfile` and `k8s/deployments/sandbox-runner-service-deployment.yaml` -- Containerize and deploy worker -- Kubernetes deployment.
- [ ] Unit & Integration Tests -- Test `Java21ExecutionStrategyTest`, `ExecutionStrategyRegistryTest`, `SubmissionKafkaConsumerTest`.

**Acceptance Criteria:**
- Given a `CodeSubmissionEvent` on Kafka topic `code.submissions`, when `SubmissionKafkaConsumer` receives the message, then it executes the code via `ExecutionStrategyRegistry` and `Java21ExecutionStrategy`.
- Given valid code, when executed, then an `ExecutionResultEvent` with `PASSED` status is published to Kafka topic `code.results`.
- Given an infinite loop (`while(true)`), when executed, then execution times out within 3000ms and emits `TIMED_OUT` status without crashing the worker.
- Given syntax error, when compiled, then emits `COMPILATION_ERROR` with compilation diagnostic logs.

## Implementation Notes

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**
- `mvn -f services/sandbox-runner-service/pom.xml clean test` -- expected: Unit and runner strategy tests pass with 0 failures.
