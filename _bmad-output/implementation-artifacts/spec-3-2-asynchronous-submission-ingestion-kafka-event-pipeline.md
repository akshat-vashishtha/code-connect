---
title: 'Story 3.2: Asynchronous Submission Ingestion & Kafka Event Pipeline'
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

**Problem:** Code compilation and test execution take seconds. Synchronous HTTP execution causes browser freezing, gateway connection pool exhaustion, and poor user experience under concurrency.

**Approach:** Implement `submission-service` (Spring Boot 3.3, Java 21) exposing `POST /api/v1/submissions` which saves a `PENDING` submission to MongoDB, publishes a typed `CodeSubmissionEvent` to Apache Kafka topic `code.submissions`, and immediately returns `HTTP 202 Accepted { submissionId }` in under 50ms.

## Boundaries & Constraints

**Always:**
- Return `HTTP 202 Accepted` with `SubmissionResponse` record in < 50ms without blocking on container execution.
- Publish `CodeSubmissionEvent` (Java 21 record) to Kafka topic `code.submissions` using Spring Kafka `KafkaTemplate`.
- Enforce clean architecture: thin controller, `SubmissionService` interface, `SubmissionFacade` orchestrator, `CreateSubmissionCommand` with `SubmissionCommandFactory`, domain models and enums (`SubmissionStatus.PENDING`).
- Authenticate requests via internal security HMAC headers or session headers passed through `gateway-service`.
- Route `/api/v1/submissions/**` through `gateway-service` to `submission-service:8083`.

**Never:**
- Do not execute Docker containers or code compilation synchronously within `submission-service` (delegated to worker via Kafka in Story 3.3).
- Do not hardcode Kafka topics, bootstrap servers, Mongo URIs, or HMAC secrets; externalize via `@ConfigurationProperties` and `${ENV:default}`.
- Do not expose MongoDB `@Document` models directly through HTTP controllers.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Valid Code Submission | `POST /api/v1/submissions` with `{ footholdId, code }` + auth headers | HTTP 202 Accepted `{ submissionId, status: "PENDING", createdAt }` | No error; Kafka event emitted |
| Blank Code Submission | `POST /api/v1/submissions` with `{ footholdId: "f1", code: "" }` | HTTP 400 Bad Request ProblemDetail with validation error | Handled by GlobalExceptionHandler |
| Missing Foothold ID | `POST /api/v1/submissions` with `{ footholdId: null, code: "class X..." }` | HTTP 400 Bad Request ProblemDetail | Handled by GlobalExceptionHandler |
| Kafka Cluster Outage | Kafka unreachable during publish | Logs error; transactional failure with HTTP 503/500 ProblemDetail | Retried or rejected gracefully |

</frozen-after-approval>

## Code Map

- `services/submission-service/pom.xml` -- Spring Boot 3.3 microservice with Spring Web, MongoDB, Validation, Security, Actuator, Spring Kafka.
- `services/submission-service/src/main/resources/application.yml` -- Pure properties configuration for port 8083, Kafka bootstrap servers, MongoDB, and HMAC security.
- `services/submission-service/src/main/java/com/codeconnect/submission/domain/enums/SubmissionStatus.java` -- Status enum (`PENDING`, `RUNNING`, `PASSED`, `FAILED`, `TIMED_OUT`, `ERROR`).
- `services/submission-service/src/main/java/com/codeconnect/submission/domain/model/SubmissionDocument.java` -- MongoDB entity for student submissions.
- `services/submission-service/src/main/java/com/codeconnect/submission/domain/event/CodeSubmissionEvent.java` -- Immutable Kafka event record.
- `services/submission-service/src/main/java/com/codeconnect/submission/application/command/CreateSubmissionCommand.java` -- Transactional command encapsulating validation, persistence, and Kafka publishing.
- `services/submission-service/src/main/java/com/codeconnect/submission/application/service/impl/SubmissionServiceImpl.java` -- Service facade orchestrating submission ingestion.
- `services/submission-service/src/main/java/com/codeconnect/submission/presentation/controller/SubmissionController.java` -- Thin REST controller returning HTTP 202.
- `services/gateway-service/src/main/resources/application.yml` -- Gateway route for `/api/v1/submissions/**`.
- `k8s/services/submission-service.yaml` -- Kubernetes Deployment and ClusterIP service for port 8083.

## Tasks & Acceptance

**Execution:**
- [ ] `services/submission-service/pom.xml` -- Bootstrap submission-service Maven project -- Initializes independent microservice module.
- [ ] `services/submission-service/src/main/resources/application.yml` -- Configure port 8083, MongoDB, Kafka, and pure ConfigurationProperties -- Externalizes runtime configs.
- [ ] `services/submission-service/.../domain/` -- Implement `SubmissionDocument`, `SubmissionStatus`, `CodeSubmissionEvent`, and `SubmissionRepository` -- Domain core.
- [ ] `services/submission-service/.../application/` -- Implement `SubmissionProperties`, `CreateSubmissionCommand`, `SubmissionCommandFactory`, `SubmissionMapper`, and `SubmissionService` -- Business orchestrators and GoF patterns.
- [ ] `services/submission-service/.../presentation/` -- Implement `SubmissionController` and `GlobalExceptionHandler` -- Ultra-thin REST interface.
- [ ] `services/submission-service/.../infrastructure/` -- Implement `SecurityConfig`, `InternalSecurityValidator`, and Kafka Producer configuration -- Security & messaging infra.
- [ ] `services/gateway-service/src/main/resources/application.yml` -- Add route for `/api/v1/submissions/**` to gateway-service -- Routes traffic from frontend.
- [ ] `k8s/services/submission-service.yaml` -- Create Kubernetes deployment and service manifests -- Cluster integration.

**Acceptance Criteria:**
- Given a valid student code submission request, when `POST /api/v1/submissions` is invoked, then `submission-service` returns `HTTP 202 Accepted` with a generated `submissionId` in < 50ms.
- Given a submission is accepted, when persisted in MongoDB, then a `CodeSubmissionEvent` record is published to Kafka topic `code.submissions`.
- Given an invalid request (blank code or missing footholdId), when submitted, then HTTP 400 ProblemDetail is returned.

## Implementation Notes

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**
- `mvn -f services/submission-service/pom.xml clean test` -- expected: Spring Boot unit and integration tests compile and pass with 0 failures.
- `mvn -f services/gateway-service/pom.xml clean test` -- expected: Gateway tests pass with new submission route.
