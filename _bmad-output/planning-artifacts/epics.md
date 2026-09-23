---
stepsCompleted:
  - step-01-validate-prerequisites
  - step-02-design-epics
  - step-03-create-stories
  - step-04-final-validation
inputDocuments:
  - _bmad-output/planning-artifacts/prds/prd-codeconnect-2026-09-23/prd.md
  - _bmad-output/planning-artifacts/architecture/architecture-codeconnect-2026-09-23/ARCHITECTURE-SPINE.md
  - _bmad-output/planning-artifacts/ux-designs/ux-codeconnect-2026-09-23/DESIGN.md
  - _bmad-output/planning-artifacts/ux-designs/ux-codeconnect-2026-09-23/EXPERIENCE.md
---

# CodeConnect - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for CodeConnect, decomposing the requirements from the PRD, UX Design Contract, and Architecture Spine into implementable, developer-ready user stories.

## Requirements Inventory

### Functional Requirements

* **FR-1**: The system shall allow users to register and authenticate via Email/Password using BCrypt password hashing.
* **FR-2**: The system shall support three distinct roles: `STUDENT`, `MENTOR`, and `ADMIN`. Student registrations activate immediately. Mentor registrations enter a pending approval state requiring explicit Admin review and approval before mentor privileges are granted.
* **FR-3**: The system shall maintain user profiles (display name, avatar, bio, role) and dynamically adapt conversational language (English vs. Hinglish) based on student chat input.
* **FR-4**: The system shall organize concepts into progressive tracks (Java Basics → Object-Oriented Java → Data Structures → Algorithms) with prerequisite locking separating unlocked footholds from higher-altitude peaks.
* **FR-5**: The system shall render a dedicated, distraction-free reading pane for concept stories supporting Markdown text, inline code snippets, and SVG diagrams (strictly excluding audio and video).
* **FR-6**: Mentors and Admins shall have access to a curriculum authoring studio to create, edit, and publish footholds (Title, Story Text, Problem Statement, Starter Code, Test Cases).
* **FR-7**: The system shall provide an in-browser code editor with Java syntax highlighting, automatic indentation, line numbering, bracket matching, starter class/method boilerplate, and expand mode.
* **FR-8**: Upon user submission, the system shall execute student Java code inside an isolated execution sandbox against defined test cases with a 3-second timeout and 128MB RAM limit, returning structured Pass/Fail and test diff outputs.
* **FR-9**: The system shall assemble contextual Socratic AI prompts containing the foothold's story analogy, problem statement, student code, and test errors, strictly coaching via analogy without emitting direct solution code.
* **FR-10**: The system shall provide an inline slide-out Socratic AI chat drawer beside the code editor with streaming token responses beginning within 2.5 seconds.
* **FR-11**: The system shall discover peer solvers who hold the `STUDENT` role and have passed the active foothold, displaying online/offline presence badges and enabling 1-on-1 direct message initiation.
* **FR-12**: The system shall support real-time 1-on-1 peer messaging with an optional toggle to share a read-only snapshot of failing code and test errors without penalties or point deductions.
* **FR-13**: The student shall be able to escalate hard conceptual blocks to mentors, creating an Escalation Ticket containing student profile, track ID, foothold ID, code snapshot, and doubt description.
* **FR-14**: Mentors shall have an Escalations Inbox sorted by wait time, with capabilities to claim tickets, review student code diffs, open a 1-on-1 direct chat, and mark tickets as `RESOLVED`.
* **FR-15**: When a student passes all tests for a foothold, the system shall validate load-bearing prerequisite constraints (triggering a 1-question verification check if required) and display visual mountain climb ascent progress.

### NonFunctional Requirements

* **NFR-1 (Code Execution Latency)**: End-to-end code compilation and test execution must complete and render in under 4 seconds under normal server load.
* **NFR-2 (AI First-Token Latency)**: The Tier-1 Socratic AI assistant must begin streaming response tokens within 2.5 seconds of a prompt submission.
* **NFR-3 (Chat Delivery Latency)**: Real-time peer-to-peer and mentor-to-student messages must be delivered with a latency of less than 300ms via WebSockets.
* **NFR-4 (Untrusted Code Execution)**: All user-submitted Java code must run inside ephemeral, unprivileged, isolated execution sandboxes with restricted permissions.
* **NFR-5 (Resource Caps & OOM Guard)**: Code execution processes must be killed immediately if they exceed 3 seconds of CPU time or 128MB of RAM, returning clean `TimeLimitExceeded` or `MemoryLimitExceeded` status.
* **NFR-6 (Network Isolation)**: The code execution container must have zero outbound network access (`--network none`) to prevent malicious network operations.
* **NFR-7 (Microservices Resilience)**: Independent microservices running on Java 21 LTS communicating via asynchronous Kafka event streams and REST APIs.
* **NFR-8 (Graceful Degradation)**: If the Tier-1 AI service is temporarily unreachable, the system must gracefully offer Tier-2 Peer Chat and Tier-3 Mentor escalation without blocking the code runner or reading pane.

### Additional Requirements

* **ARCH-1 (Modular Monorepo with Standalone POMs)**: Repository layout inside `codeconnect/` where each Spring Boot microservice has its own standalone `pom.xml` (`services/<service-name>/pom.xml`) inheriting from `spring-boot-starter-parent`, eliminating root reactor coupling while keeping all code in one monorepo.
* **ARCH-2 (Dedicated Gateway Auth & Shared Redis Session)**: `gateway-service` acts as the dedicated authentication entry point and edge gatekeeper, issuing an opaque `APP_SESSION` cookie (`HttpOnly; Secure; SameSite=Strict`). All microservices share `spring-session-data-redis` for distributed session hydration.
* **ARCH-3 (Asynchronous Kafka Code Pipeline)**: `submission-service` returns immediate `HTTP 202 Accepted { submissionId }` and publishes to Kafka topic `code.submissions`. Worker evaluates and publishes to `code.results`, delivered to client via STOMP WebSocket.
* **ARCH-4 (Ephemeral Docker Sandbox Worker)**: `sandbox-runner-service` executes Java 21 containers with `--rm --network none --memory 128m --cpus 1.0` and read-only `tmpfs` mounts.
* **ARCH-5 (Unified Document Persistence)**: Pure MongoDB 7.0 for all collections (`users`, `mentor_approval_requests`, `tracks`, `footholds`, `user_progress`, `chat_messages`, `escalation_tickets`).
* **ARCH-6 (Spring AI 2.x.x Integration)**: `collab-service` leverages Spring AI 2.x with OpenAI `gpt-4o-mini` for story-anchored Socratic coaching.
* **ARCH-7 (Redis Presence Engine)**: Active clients emit WebSocket heartbeats every 30 seconds maintaining Redis key `presence:{userId}` with 60s TTL.
* **ARCH-8 (Portable Local Infrastructure Stack)**: Standalone Docker Compose (`infra/docker-compose.infra.yml`) runs MongoDB 7.0, Redis 7.2, and Kafka in KRaft mode in under 900MB RAM, alongside local k3d Kubernetes Helm charts.
* **ARCH-9 (Dual-Layer RBAC & Instant Invalidation)**: Gateway path matching + method-level `@PreAuthorize` security; instant session mutation via `sessionRepository.findByPrincipalName` on admin approval or user ban.

### UX Design Requirements

* **UX-DR1 (Professional Light Design System)**: Cohesive design tokens (`--bg-primary: #F8FAFC`, `--brand-primary: #2563EB`, Plus Jakarta Sans typography, high-contrast borders, WCAG 2.1 AA compliant).
* **UX-DR2 (50/50 Coding Cockpit with Expand Mode)**: Split view between Text Story Reader (left) and Monaco Code Editor / Console (right), with seamless single-click expand toggles.
* **UX-DR3 (Text Story Reader Component)**: Distraction-free reader pane with Markdown rendering, inline code blocks, and SVG story diagrams (zero audio/video).
* **UX-DR4 (Interactive Test Evaluation Console)**: Test tab tray displaying Pass/Fail indicators, execution time, and diff for visible test cases.
* **UX-DR5 (Socratic AI Slide-Out Chat Drawer)**: Collapsible contextual AI tutor drawer anchored beside the code editor with streaming markdown rendering.
* **UX-DR6 (Peer Solver Discovery & Direct Chat Modal)**: Solver list tray showing online/offline status indicators and optional code-snapshot sharing toggle.
* **UX-DR7 (Mentor Escalation & Resolution Desk)**: Mentor ticket queue, code diff viewer, and 1-on-1 thread resolution interface.
* **UX-DR8 (Student Ascent Dashboard & Admin Studio)**: Visual mountain climb progression map, streak counters, and Admin Mentor approval table.

### FR Coverage Map

| Functional Requirement | Assigned Epic | Description |
|---|---|---|
| **FR-1** | **Epic 1** | Account Registration & Direct Credential Auth |
| **FR-2** | **Epic 1** | Role Selection & RBAC (Student / Mentor / Admin) |
| **FR-3** | **Epic 1** | User Profile & Dynamic English/Hinglish Adaptation |
| **FR-4** | **Epic 2** | Track & Foothold Mountain Climb Navigation |
| **FR-5** | **Epic 2** | Text-Only Story Reader (Markdown & SVG diagrams) |
| **FR-6** | **Epic 2** | Curriculum Authoring Studio for Mentors/Admins |
| **FR-7** | **Epic 3** | In-Browser Monaco Editor & 50/50 Split Cockpit |
| **FR-8** | **Epic 3** | Secure Code Execution & Automated Test Sandbox |
| **FR-9** | **Epic 4** | Narrative-Anchored Socratic AI Context Injection |
| **FR-10** | **Epic 4** | Socratic Chat Interface (Slide-out drawer) |
| **FR-11** | **Epic 5** | Peer Solver Discovery & Online/Offline Presence |
| **FR-12** | **Epic 5** | 1-on-1 Peer Chat & Failing Code Context Sharing |
| **FR-13** | **Epic 6** | Tier-3 Mentor Escalation Ticket Dispatch |
| **FR-14** | **Epic 6** | Mentor Resolution Dashboard & Direct Chat |
| **FR-15** | **Epic 7** | Load-Bearing Prerequisite Checks & Climb Ascent |

## Epic List

### Epic 1: Foundation, User Authentication & Role Governance
Students, mentors, and administrators can register, log in securely via server-side Redis sessions with an opaque cookie, manage their profile with auto-language adaptation, and access role-gated platform zones (with mentors held in pending approval until admin verification).
**FRs covered:** FR-1, FR-2, FR-3

### Epic 2: Curriculum Exploration & Text Story Reader
Students can navigate the "Mountain Climb" curriculum across progressive tracks (Java Basics to DSA), view locked/unlocked footholds, and read distraction-free, real-world text stories with inline diagrams before writing code. Mentors and Admins can author and publish footholds via the Curriculum Studio.
**FRs covered:** FR-4, FR-5, FR-6

### Epic 3: Interactive Coding Cockpit & Asynchronous Sandbox Execution
Students can write Java code inside a 50/50 split Coding Cockpit with Monaco Editor, run their code against test suites without browser blocking, and receive structured Pass/Fail results evaluated inside isolated, ephemeral Docker execution sandboxes.
**FRs covered:** FR-7, FR-8

### Epic 4: Socratic AI Story Debugger (Tier 1 Support)
When students get stuck or fail test cases, they can open an inline Socratic AI chat drawer to receive conversational debugging guidance anchored in the foothold's story analogy (in English or Hinglish) without the AI giving away the direct solution.
**FRs covered:** FR-9, FR-10

### Epic 5: Peer Solver Network & 1-on-1 Chat (Tier 2 Support)
Students struggling on a foothold can discover fellow students who have already conquered the problem, see real-time online/offline presence indicators, initiate 1-on-1 direct messaging, and share failing code snapshots for collaborative assistance.
**FRs covered:** FR-11, FR-12

### Epic 6: Mentor Escalation Desk & Verification (Tier 3 Support)
Students facing intractable conceptual blocks can escalate to vetted human mentors. Approved mentors can view active tickets sorted by wait time, inspect student code diffs, provide direct 1-on-1 chat guidance, and mark issues resolved. Admins can review pending mentor applications and approve/reject them.
**FRs covered:** FR-13, FR-14

### Epic 7: Load-Bearing Progression & Mountain Climb Dashboard
As students solve problems, the platform validates critical prerequisite dependencies (triggering quick 1-question verification checks before opening major peaks) and rewards progress with a visual mountain ascent map, streak counters, and mastery milestones.
**FRs covered:** FR-15

---

## Epic 1: Foundation, User Authentication & Role Governance

Students, mentors, and administrators can register, log in securely via server-side Redis sessions with an opaque cookie, manage their profile with auto-language adaptation, and access role-gated platform zones (with mentors held in pending approval until admin verification).

### Story 1.1: Project Monorepo Skeleton & Local Infrastructure Bootstrapping

As a developer,
I want the monorepo initialized with standalone Maven Spring Boot services and the local Docker backing infrastructure,
So that I can boot MongoDB, Redis, Kafka, and backend microservices independently with zero build coupling.

**Acceptance Criteria:**

**Given** the repository root,
**When** running `docker compose -f infra/docker-compose.infra.yml up -d`,
**Then** containers for MongoDB 7.0 (`:27017`), Redis 7.2 (`:6379`), and Apache Kafka in KRaft mode (`:9092`) start in a healthy state under 900MB total RAM.
**And** `services/gateway-service` and `services/user-service` each contain an independent `pom.xml` inheriting directly from `spring-boot-starter-parent` (Java 21 LTS, Spring Boot 3.3.x) with no root Maven parent.
**And** running `npm run dev` in `frontend/` starts the Next.js 15 application on `http://localhost:3000` with light theme tokens configured.

### Story 1.2: Student & Mentor Registration with Role Routing

As a new visitor,
I want to register with email, password, display name, and select my role (`STUDENT` vs. `MENTOR`),
So that I can join CodeConnect with appropriate account permissions.

**Acceptance Criteria:**

**Given** a valid registration form with role `STUDENT`,
**When** submitting `POST /api/v1/auth/signup`,
**Then** `gateway-service` hashes the password with BCrypt, saves the user in MongoDB `users` with `status: ACTIVE`, establishes a Redis session, and sets `Set-Cookie: APP_SESSION=...; HttpOnly; Secure; SameSite=Strict`.
**And** given a registration form with role `MENTOR` (including LinkedIn URL and bio), the user is created with `status: PENDING_APPROVAL`, an entry is recorded in MongoDB `mentor_approval_requests`, and the client is routed to a "Pending Admin Review" screen without mentor privileges.
**And** given an email that already exists in MongoDB, the service returns `HTTP 409 Conflict` formatted as RFC 7807 problem details.

### Story 1.3: Direct Credential Login, Redis Session Issuance & Logout

As a registered user,
I want to log in with my email and password and receive an opaque session cookie,
So that I can access the platform securely without exposing tokens to browser JavaScript.

**Acceptance Criteria:**

**Given** valid registered credentials,
**When** submitting `POST /api/v1/auth/login`,
**Then** `gateway-service` verifies the BCrypt hash against MongoDB, creates a session in Redis (`spring:session:sessions:<uuid>`) storing `user_id` and `role`, and sets cookie `APP_SESSION`.
**And** given an active `APP_SESSION` cookie, calling `GET /api/v1/auth/me` returns `HTTP 200 OK` with user details (`id`, `email`, `displayName`, `role`, `status`).
**And** given invalid credentials or an account with `status: BANNED`, the system returns `HTTP 401 Unauthorized` or `HTTP 403 Forbidden` respectively.
**And** given an authenticated user calling `POST /api/v1/auth/logout`, the Redis session is deleted immediately in $O(1)$ time and the browser cookie is cleared (`Max-Age=0`).

### Story 1.4: Dual-Layer RBAC Gatekeeping & Admin Mentor Verification

As an administrator,
I want to review pending mentor applications and approve or reject them,
So that only vetted educators can access mentor capabilities.

**Acceptance Criteria:**

**Given** an unauthenticated request or a user with `ROLE_STUDENT`,
**When** accessing `/api/v1/admin/**` or `/api/v1/mentor/**`,
**Then** `gateway-service` rejects the request with `HTTP 403 Forbidden`.
**And** given an authenticated Admin calling `GET /api/v1/admin/mentors/pending`, `user-service` returns all applications from `mentor_approval_requests` with `status: PENDING`.
**And** given an Admin calling `POST /api/v1/admin/mentors/{id}/approve`, `user-service` updates MongoDB `users.role = "ROLE_MENTOR"`, updates `status = ACTIVE`, and mutates active Redis sessions for that user in $O(1)$ time via `sessionRepository.findByPrincipalName`.

**And** given a student submitting chat messages containing Hindi/Hinglish markers (e.g., "bhai", "samajh nahi aaya", "kaise kare"), the language detector tags the user's active session attribute `languagePreference` as `HINGLISH`.

---

## Epic 2: Curriculum Exploration & Text Story Reader

Students can navigate the "Mountain Climb" curriculum across progressive tracks (Java Basics to DSA), view locked/unlocked footholds, and read distraction-free, real-world text stories with inline diagrams before writing code. Mentors and Admins can author and publish footholds via the Curriculum Studio.

### Story 2.1: Curriculum Tracks & Footholds Progression Data Model & Navigation API

As a student,
I want to browse the "Mountain Climb" curriculum tracks (Java Basics, OOP, Data Structures, Algorithms) and see which footholds are unlocked versus locked,
So that I understand my progression and know which concept to tackle next.

**Acceptance Criteria:**

**Given** `curriculum-service` backed by MongoDB `tracks` and `footholds` collections,
**When** calling `GET /api/v1/curriculum/tracks`,
**Then** the service returns all published tracks ordered by progression index.
**And** given an authenticated student with completed footholds in MongoDB `user_progress`, calling `GET /api/v1/curriculum/tracks/{trackId}/footholds` returns each foothold payload including `id`, `title`, `orderIndex`, `prerequisites`, and an `isUnlocked` boolean calculated from prior completions.
**And** on the frontend curriculum map, unlocked footholds display vibrant interactive cards while locked higher-altitude peaks render muted with lock badges.

### Story 2.2: Distraction-Free Text Story Reader Component

As a student,
I want to read a concept's real-world text story with clear typography, code snippets, and SVG diagrams,
So that I develop visual intuition before writing any code, without multimedia distractions.

**Acceptance Criteria:**

**Given** an unlocked foothold ID,
**When** calling `GET /api/v1/curriculum/footholds/{id}/story`,
**Then** the service returns the story Markdown content, problem statement, and diagram metadata.
**And** the left-hand Story Reader pane on the Next.js frontend renders Markdown formatting with custom Plus Jakarta Sans styling, inline code blocks with copy buttons, and lightweight SVG diagrams.
**And** when the Story Reader DOM is inspected, it contains strictly zero `<video>`, `<iframe>` (e.g. YouTube), or `<audio>` elements.

### Story 2.3: Mentor & Admin Curriculum Authoring Studio

As a mentor or administrator,
I want an authoring studio to create, edit, and publish footholds with their text stories, starter code, and test cases,
So that our educational curriculum can expand with high-quality content.

**Acceptance Criteria:**

**Given** a user with `ROLE_STUDENT` attempting `POST /api/v1/curriculum/author/**`,
**Then** `gateway-service` rejects the request with `HTTP 403 Forbidden`.
**And** given an authorized Mentor or Admin calling `POST /api/v1/curriculum/author/footholds`, `curriculum-service` validates and saves the document in MongoDB `footholds` with `title`, `trackId`, `orderIndex`, `storyMarkdown`, `problemStatement`, `starterCodeJava`, `visibleTestCases` (input, expectedOutput), and `hiddenTestCases`.
**And** given a foothold published with status `PUBLISHED`, it becomes immediately queryable and unlockable for students whose prerequisites are satisfied.

---

## Epic 3: Interactive Coding Cockpit & Asynchronous Sandbox Execution

Students can write Java code inside a 50/50 split Coding Cockpit with Monaco Editor, run their code against test suites without browser blocking, and receive structured Pass/Fail results evaluated inside isolated, ephemeral Docker execution sandboxes.

### Story 3.1: 50/50 Coding Cockpit Layout with Monaco Editor & Expand Mode

As a student,
I want a 50/50 split workspace featuring Monaco Editor pre-loaded with Java boilerplate alongside the text story reader, with a 1-click expand toggle,
So that I can write code comfortably with professional syntax highlighting, autocompletion, and maximized screen real estate.

**Acceptance Criteria:**

**Given** an unlocked foothold,
**When** the Coding Cockpit loads,
**Then** the screen renders a 50/50 split desktop layout between the Story Reader (left) and Monaco Editor (right).
**And** Monaco pre-populates the editor with the starter Java class and method signature from the active foothold document.
**And** clicking the "Expand Editor" toggle smoothly expands the editor to full width while keeping the story accessible via a tab toggle without losing typed code state.
**And** client local storage caches unsubmitted code so progress is not accidentally lost upon browser refresh.

### Story 3.2: Asynchronous Submission Ingestion & Kafka Event Pipeline

As a student,
I want to press "Run Foothold" and have my submission accepted immediately without freezing my browser,
So that the application remains snappy and responsive under high concurrent load.

**Acceptance Criteria:**

**Given** a student clicking "Run Foothold",
**When** `POST /api/v1/submissions` sends `{ footholdId, code }`,
**Then** `submission-service` returns `HTTP 202 Accepted { submissionId }` in under 50ms.
**And** `submission-service` publishes a `CodeSubmissionEvent` containing `submissionId`, `studentId`, `footholdId`, `code`, and test suite to Kafka topic `code.submissions`.
**And** the client successfully connects to STOMP WebSocket at `/ws-connect` and subscribes to `/topic/submissions.{submissionId}` awaiting evaluation.

### Story 3.3: Ephemeral Docker Sandbox Execution Worker

As a platform engineer,
I want the worker service to compile and run student Java code inside an isolated, unprivileged, network-less Docker container,
So that student code cannot breach host security, exhaust RAM, or make outbound malicious requests.

**Acceptance Criteria:**

**Given** `sandbox-runner-service` listening to Kafka topic `code.submissions`,
**When** a job arrives,
**Then** it spins up an isolated container: `docker run --rm --network none --memory 128m --cpus 1.0 codeconnect-runner:java21`.
**And** if execution exceeds 3000ms CPU time, the container process is killed and returns `TimeLimitExceeded` (`NFR-5`).
**And** if memory exceeds 128MB, the container process is OOM-killed and returns `MemoryLimitExceeded` (`NFR-5`).
**And** any network call fails immediately due to `--network none` (`NFR-6`).
**And** upon execution completion, the worker publishes an `ExecutionResultEvent` to Kafka topic `code.results` and destroys the container (`--rm`).

### Story 3.4: Real-Time Test Results Console & Diff Rendering

As a student,
I want to see a structured test results console displaying which test cases passed, which failed, and the exact diff between expected and actual output,
So that I know exactly what logical mistake I made and how to resolve it.

**Acceptance Criteria:**

**Given** `collab-service` consuming Kafka `code.results`,
**When** an event arrives,
**Then** it pushes the payload over STOMP WebSocket to `/topic/submissions.{submissionId}`.
**And** total elapsed time from clicking "Run" to test rendering is under 4 seconds (`NFR-1`).
**And** visible test cases display Input, Expected Output, Actual Output, and Execution Time with color-coded diff highlights.
**And** hidden test cases display only Pass/Fail status without leaking the hidden inputs or expected values.
**And** when all test cases pass, a green celebration banner appears unlocking progression to the next foothold.

---

## Epic 4: Socratic AI Story Debugger (Tier 1 Support)

When students get stuck or fail test cases, they can open an inline Socratic AI chat drawer to receive conversational debugging guidance anchored in the foothold's story analogy (in English or Hinglish) without the AI giving away the direct solution.

### Story 4.1: Socratic Narrative Prompt Assembly & Streaming AI Engine

As a student debugging a failing test or compilation error,
I want the platform to send my failing code, error output, and the story narrative context to an AI model that guides me using the story's analogy without giving the direct answer,
So that I develop problem-solving intuition and overcome fear of failure.

**Acceptance Criteria:**

**Given** a student on a foothold with current code and test failure output,
**When** the student sends a query in the Socratic AI drawer (`POST /api/v1/ai/socratic-hint`),
**Then** `ai-service` (Spring AI 2.x) retrieves the foothold story context, student code, and error trace from MongoDB.
**And** system prompt explicitly instructs the LLM: (1) Answer in the user's preferred language (English or Hinglish) (`FR-3`), (2) Anchor hints in the story analogy (`FR-9`), (3) Never output complete working code or direct syntax replacements (`FR-9`).
**And** the AI response streams back via Server-Sent Events (SSE) with first token arriving in under 2 seconds (`NFR-2`).
**And** student can toggle the output language dynamically between English and Hinglish (`NFR-8`).

### Story 4.2: Inline Socratic Slide-Out Chat Drawer Component

As a student working in the Coding Cockpit,
I want an inline slide-out chat drawer that opens beside my editor without obscuring my code or story,
So that I can converse with the AI Guide while actively editing my Java solution.

**Acceptance Criteria:**

**Given** the Coding Cockpit at `frontend/src/app/learn/[trackId]/[footholdId]`,
**When** the student clicks the "Ask AI Guide" action button in the cockpit header,
**Then** a slide-out drawer transitions smoothly from the right side (`UX-DR5`), occupying 35% of the viewport width.
**And** the Monaco Editor automatically resizes smoothly via `automaticLayout: true` without code clipping.
**And** incoming streamed tokens render in real time with markdown formatting and syntax-highlighted code snippets.
**And** conversation history for the current foothold session is persisted in local session storage and retrievable upon drawer reopen.
**And** an explicit "Reset AI Conversation" button clears the session history for that foothold.

---

## Epic 5: Peer Solver Network & 1-on-1 Chat (Tier 2 Support)

Students struggling on a foothold can discover fellow students who have already conquered the problem, see real-time online/offline presence indicators, initiate 1-on-1 direct messaging, and share failing code snapshots for collaborative assistance.

### Story 5.1: Peer Solver Discovery & Real-Time Presence Service

As a student stuck on a challenging foothold after trying the Socratic AI,
I want to view a list of fellow students who have already solved this foothold, complete with their current online/offline status,
So that I can reach out to an active peer who has firsthand experience overcoming this obstacle.

**Acceptance Criteria:**

**Given** a student on a foothold (`footholdId`),
**When** the student navigates to the "Peer Solvers" tab in the Coding Cockpit (`GET /api/v1/footholds/{footholdId}/solvers`),
**Then** `collab-service` queries MongoDB `user_progress` for users who have completed this foothold with `status: SOLVED`.
**And** `collab-service` batch checks active user IDs against Redis presence keys (`presence:user:{userId}`) having a 60-second TTL updated via 30-second client heartbeats (`ARCH-7`).
**And** the API returns a list of peer solvers tagged with `isOnline: true/false`, display name, avatar, and completion timestamp, sorted with online peers at the top.
**And** offline peers display their last active timestamp (e.g., "Active 2h ago").

### Story 5.2: 1-on-1 Real-Time STOMP Peer Chat with Code Snapshot Sharing

As a student chatting with an online peer solver,
I want to send real-time text messages and optionally attach my current code snippet and failing test diff,
So that my peer can inspect my logic and provide targeted hints without me leaving the cockpit.

**Acceptance Criteria:**

**Given** an authenticated student initiating a chat with an online peer solver,
**When** the student clicks "Message Peer" (`UX-DR6`),
**Then** a 1-on-1 chat panel opens in the cockpit, creating or resuming a thread in MongoDB `peer_chats`.
**And** the client connects to `collab-service` WebSocket endpoint `/ws-collab` and subscribes to `/topic/peer.{chatId}`.
**And** sending a text message broadcasts to the recipient with message delivery latency under 300ms (`NFR-3`).
**And** the student can click "Share Failing Code Snapshot" which attaches a read-only syntax-highlighted code block + compiler error snippet into the message bubble (`FR-12`).
**And** both users see real-time typing indicators and message delivery checkmarks.

---

## Epic 6: Mentor Escalation Desk & Verification (Tier 3 Support)

Students facing intractable conceptual blocks can escalate to vetted human mentors. Approved mentors can view active tickets sorted by wait time, inspect student code diffs, provide direct 1-on-1 chat guidance, and mark issues resolved. Admins can review pending mentor applications and approve/reject them.

### Story 6.1: Tier-3 Mentor Escalation Ticket Dispatch

As a student facing an intractable block after trying AI and peer assistance,
I want to escalate an issue to a vetted human mentor with my notes, failing code, and test execution history,
So that an expert mentor can provide high-touch diagnostic guidance.

**Acceptance Criteria:**

**Given** an authenticated student in the Coding Cockpit on an unsolved foothold,
**When** the student selects "Escalate to Mentor" (`FR-13`),
**Then** an escalation modal opens prompting for: (1) What did you expect? (2) What went wrong? (3) Confirmation to attach current code and test failure log.
**And** submitting `POST /api/v1/tickets/escalate` stores a ticket in MongoDB `escalation_tickets` with `status: PENDING`, FIFO timestamp, and references to `footholdId`, `studentId`, and code snapshot.
**And** student receives real-time confirmation showing ticket ID and live queue position.
**And** a new ticket event is broadcast over WebSocket topic `/topic/mentor.desk.queue` to active mentors.

### Story 6.2: Mentor Resolution Desk, Code Diff Review & Live Guidance Chat

As an approved mentor,
I want to view a live queue of escalated tickets sorted by wait time, inspect the student's code diff against the starter code, and launch a 1-on-1 resolution chat,
So that I can efficiently diagnose the student's conceptual blocker and guide them to mastery.

**Acceptance Criteria:**

**Given** an authenticated mentor accessing `/mentor/desk` (`UX-DR7`),
**When** the page loads,
**Then** a table displays pending tickets sorted FIFO (longest wait time first) with student name, foothold title, and elapsed wait time.
**And** mentor clicks "Claim Ticket", updating ticket in MongoDB to `status: CLAIMED`, `mentorId: <id>`, preventing race conditions from duplicate claims.
**And** clicking "Inspect Diff" opens a side-by-side Monaco Diff Viewer comparing the foothold starter code with the student's failing attempt.
**And** a 1-on-1 chat window connects mentor and student over WebSocket (`/topic/tickets.{ticketId}`), allowing live messaging.
**And** clicking "Mark Resolved" updates ticket status to `RESOLVED`, logs resolution duration, and prompts the student for optional 1-5 star feedback.

### Story 6.3: Admin Mentor Review & Instant Redis Session Elevation

As an administrator,
I want to review pending mentor applications, inspect their credentials/LinkedIn, and approve or reject them with instant permission activation,
So that qualified mentors can immediately begin resolving tickets without logging out and back in.

**Acceptance Criteria:**

**Given** an authenticated administrator at `/admin/mentors`,
**When** the page loads,
**Then** it lists all applicants with `status: PENDING_APPROVAL`, showing bio, LinkedIn URL, and application date.
**And** clicking "Approve Mentor" calls `POST /api/v1/admin/mentors/{userId}/approve`, which updates MongoDB `users.role = ROLE_MENTOR` and `users.status = ACTIVE`.
**And** the service mutates the applicant's existing Redis session in-place (`spring:session:sessions:<id>`) so elevated permissions apply instantly on their next HTTP/WebSocket request (`ARCH-9`).
**And** clicking "Reject" prompts for a mandatory reason, sets `status: REJECTED` in MongoDB, and triggers an email notification to the applicant.

---

## Epic 7: Load-Bearing Progression & Mountain Climb Dashboard

As students solve problems, the platform validates critical prerequisite dependencies (triggering quick 1-question verification checks before opening major peaks) and rewards progress with a visual mountain ascent map, streak counters, and mastery milestones.

### Story 7.1: Load-Bearing Prerequisite Engine & 1-Question Verification Challenge

As a student attempting an advanced track or high-altitude foothold,
I want the platform to verify my foundational prerequisites or present a quick 1-question verification challenge,
So that I never get blindsided by advanced concepts without necessary mental scaffolding.

**Acceptance Criteria:**

**Given** a student attempting to access an advanced foothold with prerequisite requirements (`foothold.prerequisites`),
**When** the student navigates to the foothold URL or clicks it on the ascent map,
**Then** `curriculum-service` checks MongoDB `user_progress`; if all prerequisites are completed, the foothold opens directly.
**And** if prerequisite footholds are incomplete, an informative prerequisite modal opens showing missing concepts and offering: (1) "Go to Prerequisite Foothold", or (2) "Take 1-Question Verification Challenge" (`FR-15`).
**And** taking the 1-question verification presents a rapid single-concept coding challenge in a focused modal cockpit.
**And** passing the verification test suite marks the prerequisite verified in `user_progress` and unlocks the destination foothold immediately.
**And** failing the verification directs the student with friendly guidance to the recommended foundation foothold.

### Story 7.2: Student Mountain Climb Ascent Dashboard & Streak Tracking

As a student on CodeConnect,
I want a visual Mountain Climb ascent map illustrating my learning journey, elevation milestones, and consecutive daily streak,
So that I feel a tangible sense of accomplishment and stay motivated to climb higher.

**Acceptance Criteria:**

**Given** an authenticated student accessing `/learn/dashboard` (`UX-DR8`),
**When** the page renders,
**Then** it displays an interactive SVG/Canvas Mountain Climb trail with footholds rendered as elevation nodes.
**And** nodes reflect live user status: Conquered (green summit flag/check), Current (pulsing brand-primary `#2563EB` border), Locked (subtle slate lock icon).
**And** clicking any unlocked node navigates directly to that foothold's story and cockpit.
**And** an active streak counter displays consecutive days active with a flame badge, persisted and synchronized within 500ms of foothold completion (`NFR-4`).
**And** a progress summary card displays total footholds solved, current altitude rank (e.g., *Basecamp Pioneer*, *Ridge Climber*, *Summit Master*), and peer solver badges.




