# Epic 3 Context: Interactive Coding Cockpit & Asynchronous Sandbox Execution

<!-- Compiled from planning artifacts. Edit freely. Regenerate with compile-epic-context if planning docs change. -->

## Goal

Enable students to write Java code in a distraction-free 50/50 split Coding Cockpit with Monaco Editor, run their solutions asynchronously against test suites via an event-driven Kafka pipeline, and receive structured, real-time evaluation results compiled and executed within isolated, ephemeral Docker containers.

## Stories

- Story 3.1: 50/50 Coding Cockpit Layout with Monaco Editor & Expand Mode
- Story 3.2: Asynchronous Submission Ingestion & Kafka Event Pipeline
- Story 3.3: Ephemeral Docker Sandbox Execution Worker
- Story 3.4: Real-Time Test Results Console & Diff Rendering

## Requirements & Constraints

- **Split Workspace**: Responsive 50/50 desktop split layout with the Story Reader on the left and the Monaco Editor on the right.
- **Monaco Editor Integration**: Pre-populated with active foothold starter Java class and method signature; supports syntax highlighting, tab indentation, dark/light themes, and local storage autosave.
- **Full-Width Expansion**: 1-click expand toggle to transition Monaco Editor into maximized fullscreen/expanded mode while keeping story accessible via tab toggle.
- **Asynchronous Processing**: `POST /api/v1/submissions` returns `HTTP 202 Accepted { submissionId }` in under 50ms, dispatching job to Kafka topic `code.submissions`.
- **Ephemeral Sandbox Isolation**:
  - Unprivileged, network-less Docker execution: `docker run --rm --network none --memory 128m --cpus 1.0 codeconnect-runner:java21`.
  - CPU timeout: 3000ms hard cap (`TimeLimitExceeded`).
  - RAM limit: 128MB hard cap (`MemoryLimitExceeded`).
- **Real-Time Result Push**: Results published to Kafka `code.results` and streamed to the student via STOMP WebSocket over `/topic/submissions.{submissionId}`.

## Technical Decisions

- **Frontend Architecture**:
  - `src/presentation/cockpit/`: Coding Cockpit component assembly.
  - `src/controller/useCockpitController.ts`: Custom hook managing editor state, local storage sync, expand toggle, and submission dispatch.
  - `@monaco-editor/react`: Standard Monaco editor component for Next.js.
- **Backend Services**:
  - `submission-service` (Port 8083): Ingestion endpoint, Redis session validation, MongoDB persistence, Kafka producer.
  - `sandbox-runner-service` (Worker): Kafka consumer, Docker process manager, Java 21 test runner, Kafka producer.
- **Messaging Contracts**:
  - Kafka Topics: `code.submissions`, `code.results`.
  - STOMP WebSocket: `/ws-connect`, destination `/topic/submissions.{submissionId}`.

## UX & Interaction Patterns

- **50/50 Split**: Equal screen real estate between problem description/story and coding editor.
- **Expand Toggle**: Icon button in editor toolbar smoothly toggles full-width coding mode without losing cursor position or unsaved code.
- **Autosave**: Debounced local storage caching under `codeconnect_draft_{footholdId}`.
- **Theme & Font Controls**: Visual controls for dark/light themes and editor font size.

## Cross-Story Dependencies

- **Story 3.1** provides the frontend editor and cockpit layout required by Story 3.2 (Run button) and Story 3.4 (Test results display).
- **Story 3.2** (`submission-service`) depends on the Kafka infrastructure (`kafka:9092`) established in the cluster.
- **Story 3.3** (`sandbox-runner-service`) consumes from `code.submissions` produced by Story 3.2.
- **Story 3.4** consumes from `/topic/submissions.{id}` fed by Story 3.3's output.
