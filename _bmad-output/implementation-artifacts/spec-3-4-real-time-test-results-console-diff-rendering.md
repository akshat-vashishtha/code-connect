---
title: 'Story 3.4: Real-Time Test Results Console & Diff Rendering'
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

**Problem:** After submitting code asynchronously via Kafka and Docker sandbox runners, students need instant, clear feedback (<4s end-to-end) showing exactly which test cases passed, which failed, and a side-by-side diff between expected and actual output without exposing hidden test case internals.

**Approach:** 
1. Implement `collab-service` (Spring Boot 3.3, Java 21, WebSocket STOMP broker) listening to Kafka `code.results` and dispatching `ExecutionResultEvent` directly over STOMP WebSocket topic `/topic/submissions.{submissionId}`.
2. In `frontend`, integrate STOMP / WebSocket client to receive execution results in real-time.
3. Build a structured Test Results Console in the Coding Cockpit featuring test case accordion tabs, color-coded expected vs actual diff highlights, execution runtime, hidden test security redaction, and an animated green victory banner upon all tests passing unlocking progression to the next foothold.

## Boundaries & Constraints

**Always:**
- Use STOMP over WebSocket at `/ws-connect` for pushing evaluation results to `/topic/submissions.{submissionId}`.
- Deliver end-to-end test execution results in under 4 seconds (`NFR-1`).
- Display visible test cases with Input, Expected Output, Actual Output, and Execution Time with color-coded diff highlighting.
- Redact hidden test cases to show only `Pass` or `Fail` status without leaking secret inputs or expected outputs.
- Display an celebratory success banner when all tests pass, unlocking the next foothold navigation button.
- Follow Clean Architecture, GoF patterns, strict TypeScript, and zero dummy logic.

**Never:**
- Do not expose hidden test case inputs or expected values to the client DOM or API payload.
- Do not poll HTTP endpoints repeatedly when WebSockets are established.
- Do not hardcode topic names, endpoints, or CORS configurations in Java classes.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| All Tests Passing | `ExecutionResultEvent` with all tests `PASSED` | Green celebration banner ("Foothold Conquered! 🚀"), test table showing green checkmarks, "Next Foothold" CTA enabled | Full victory state |
| Some Tests Failing | `ExecutionResultEvent` with status `FAILED` and failing test diff | Test console highlights failing case with Red badge; displays Expected vs Actual character/line diff | Clear guidance |
| Compilation / Syntax Error | `ExecutionResultEvent` with `COMPILATION_ERROR` | Red alert console showing compiler error output, line number, and error token | Monaco error markers |
| Timeout / Memory Exceeded | `ExecutionResultEvent` with `TIMED_OUT` or `MEMORY_EXCEEDED` | Warning console explaining 3000ms CPU or 128MB RAM limit reached | Suggest algorithmic optimization |
| WebSocket Disconnect | Network glitch during execution | Automatic STOMP reconnection attempts; fallback gracefully to status query | Auto-reconnect |

</frozen-after-approval>

## Code Map

- `services/collab-service/pom.xml` -- Spring Boot 3.3 WebSocket & Kafka collaboration service.
- `services/collab-service/src/main/resources/application.yml` -- Configuration for STOMP broker, Kafka consumer group, and CORS.
- `services/collab-service/src/main/java/com/codeconnect/collab/infrastructure/config/WebSocketConfig.java` -- STOMP endpoint registration at `/ws-connect` with Simple Broker on `/topic`.
- `services/collab-service/src/main/java/com/codeconnect/collab/infrastructure/messaging/ExecutionResultKafkaConsumer.java` -- Kafka consumer for `code.results` forwarding to `SimpMessagingTemplate`.
- `frontend/package.json` -- Install `@stomp/stompjs` and `sockjs-client`.
- `frontend/src/lib/websocket/stompClient.ts` -- Clean STOMP client manager and subscription facade.
- `frontend/src/controller/useCockpitController.ts` -- Updated controller managing submission lifecycle, STOMP event subscriptions, and test results state.
- `frontend/src/presentation/organisms/CockpitTestResultsConsole.tsx` -- Structured test results console organism with diff rendering and celebration banner.
- `frontend/src/presentation/molecules/TestDiffViewer.tsx` -- Side-by-side or inline expected vs actual diff highlighter.
- `frontend/src/presentation/views/CodingCockpitView.tsx` -- Integration of test results drawer/bottom console in the Cockpit layout.
