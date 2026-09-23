---
title: Product Requirements Document — CodeConnect
created: 2026-09-23
updated: 2026-09-23
status: final
---

# PRD: CodeConnect

## 0. Document Purpose
This Product Requirements Document (PRD) establishes the definitive functional specifications, user journeys, system boundaries, and non-functional requirements for the **CodeConnect MVP**. It translates the product vision established in the [Product Brief](file:///Users/Akshat.x.Vashishtha/Machine/Learnings/projects/git/idea/_bmad-output/planning-artifacts/briefs/brief-codeconnect-2026-09-23/brief.md) and [Brainstorm Intent](file:///Users/Akshat.x.Vashishtha/Machine/Learnings/projects/git/idea/_bmad-output/brainstorming/brainstorm-codeconnect-learning-platform-2026-09-22/brainstorm-intent.md) into concrete, testable requirements (`FR-1` through `FR-15`) that downstream architecture (`bmad-architecture`), UX design (`bmad-ux`), and implementation sprints (`bmad-create-epics-and-stories`) can execute without ambiguity.

---

## 1. Vision
**CodeConnect** is a structured, fear-free web learning platform for Java and Data Structures & Algorithms (DSA). It replaces the intimidating, vertical cliffs of traditional competitive programming sites with an empathetic **"Solo Mountain Climb."** 

Instead of opening with sterile academic definitions or syntax tables, CodeConnect introduces every concept through an imaginative **real-world text story** (KISS principle; strictly no video or audio). The programming language syntax is presented solely as the natural tool that resolves the tension in the story.

When a student stumbles on a failing test case, CodeConnect prevents frustration through a **Three-Tier Support Hierarchy**:
1. **Tier 1 (Socratic AI Assistant)**: Instant, conversational hints delivered in English or Hinglish that diagnose the bug within the concept's original story world.
2. **Tier 2 (Peer Solver Doubt Chat)**: Direct, friendly 1-on-1 messaging with a fellow student who recently solved that exact problem.
3. **Tier 3 (Human Mentor Escalation)**: Contextual intervention from an expert mentor who receives the student's code, test failure traces, and prior chat history.

CodeConnect is an authentic, mission-driven product designed to turn an intuitive educational vision into a reliable, distributed micro-services application.

---

## 2. Target User & Personas

### 2.1 Jobs To Be Done (JTBD)
* **Functional**: "When I am learning a new Java/DSA concept, I want a relatable story mental model and immediate browser-based coding practice so that I can write working code without getting stuck in tutorial hell."
* **Emotional**: "When my code fails a test case, I want guidance that doesn't make me feel stupid, so that I can experience the 'aha' breakthrough myself and feel proud of solving it."
* **Social / Relational**: "When I am stuck and automated hints aren't enough, I want to connect with a classmate or mentor like a friend after school, without having to post my code on public forums."
* **Mentor's Job**: "When my students have coding doubts, I want automated Tier-1 AI and Tier-2 peer triage so that I only spend my time providing high-impact coaching on genuine conceptual roadblocks."

### 2.2 Non-Users (v1)
* **Competitive Speed Coders**: CodeConnect is not a speed-contest or rating platform for competitive rank chasing.
* **Multi-Language Generalists**: In MVP, this is explicitly not for Python, C++, or JavaScript learners; it is laser-focused on Java & DSA.
* **Cohort/Batch Synchronized Classrooms**: This is not an LMS that forces rigid weekly homework schedules. Progression is strictly asynchronous and self-paced.

### 2.3 Key User Journeys

#### UJ-1: Arjun Learns a New Concept and Gets Socratic AI Help
* **Persona + Context**: Arjun, a college sophomore struggling with recursion in his university classes.
* **Entry State**: Authenticated as a Student on the web dashboard.
* **Path**:
  1. Arjun selects the **Java & DSA Track** and navigates to the next unlocked foothold: *"Recursive Backtracking"*.
  2. The left pane presents a 3-paragraph text story: *"The Labyrinth of Breadcrumbs"* (pure text, no video). The story explains how marking visited crossroads prevents infinite loops.
  3. The problem statement asks him to write a Java method `boolean hasPath(int[][] maze)`.
  4. Arjun enters his code in the web editor and clicks **Submit**.
  5. The test runner evaluates 5 test cases: 4 Pass, but Test 5 fails with an `InfiniteRecursion / StackOverflowError`.
  6. The chat drawer slides open automatically. The **Tier-1 AI Assistant** asks in friendly Hinglish: *"Arjun, look back at the breadcrumbs story: what happens when your explorer steps into a room they already marked? Did your code check if the current tile is already visited before taking the next step?"*
* **Climax**: Arjun spots his missing visited check, adds two lines of code, and clicks **Submit**. All 5 tests pass with a green celebration badge.
* **Resolution**: CodeConnect quietly performs a load-bearing prerequisite check, marks the foothold as complete, and unlocks the next peak.

#### UJ-2: Arjun Connects with a Peer Solver
* **Persona + Context**: Later in the track, Arjun is solving a tricky Two-Pointer problem (*"Container With Most Water"*).
* **Entry State**: Arjun has failed Test Case 8 twice. The AI hints narrowed down the issue, but he still cannot visualize the two-pointer inward movement.
* **Path**:
  1. In the chat drawer, Arjun clicks **"Ask a Peer Solver"**.
  2. The platform displays a list of 4 students who recently passed this problem, with 2 currently marked "Active Online".
  3. Arjun clicks on **Priya (Online)** and sends a pre-populated message: *"Hey Priya! I'm stuck on Test 8 of Container With Most Water. Could you give me a quick hint on how you handled moving the shorter pointer?"*
  4. Priya receives a notification in her chat bar, opens Arjun's shared read-only code snapshot, and types: *"Hey Arjun! Remember that the height is constrained by whichever wall is shorter. If you move the taller wall, the area can only get smaller!"*
* **Climax**: Arjun immediately grasps the mathematical intuition from a peer's explanation.
* **Resolution**: Arjun fixes his pointer condition, submits successfully, and types *"Thanks Priya!"* before moving on.

#### UJ-3: Escalation to Mentor
* **Persona + Context**: Arjun faces a nuanced edge case in Dynamic Programming where neither the AI hints nor peer advice resolve his confusion.
* **Path**:
  1. Arjun clicks **"Escalate to Mentor"**.
  2. The system packages Arjun's code, the failing test case inputs, and his prior chat log, routing it to his assigned Mentor, **Vikram**.
  3. Vikram opens his Mentor Dashboard, clicks Arjun's pending escalation ticket, and reviews the exact line where Arjun's memoization table is overwritten.
  4. Vikram initiates a direct 1-on-1 chat and sends an annotated code pointer with a conceptual explanation.
* **Resolution**: Arjun replies, confirms understanding, and clears the problem. Vikram marks the ticket as resolved.

#### UJ-4: Vikram the Mentor Authors a Concept & Practice Problem
* **Persona + Context**: Vikram, a senior engineer and mentor, wants to add a custom problem on Circular Queues.
* **Entry State**: Authenticated as a Mentor (approved by Admin).
* **Path**:
  1. Vikram navigates to the **Curriculum Studio** tab.
  2. He creates a new Foothold: enters the title, writes a real-world text story (*"The Round-Robin Printer Queue"*), defines the problem prompt, sets starter Java boilerplate, and inputs 4 visible and 2 hidden test cases.
  3. He clicks **Publish**.
* **Resolution**: The new concept immediately appears on the track for all enrolled students.

---

## 3. Glossary

* **Foothold**: A discrete, bite-sized unit of learning consisting of one Real-World Text Story, one linked coding problem, and automated test cases.
* **Load-Bearing Prerequisite Check**: A quiet background assessment that tests only the foundational concepts the upcoming move strictly depends on before unlocking higher peaks.
* **Story Analogy**: The real-world narrative mental model used to explain a concept without technical jargon.
* **Tier-1 AI Assistant**: The automated LLM-powered coach that provides multilingual (English/Hinglish) Socratic debugging anchored in the Story Analogy.
* **Tier-2 Peer Solver**: A student who has successfully cleared a specific problem and is available to answer peer questions via lightweight 1-on-1 chat.
* **Tier-3 Mentor**: A vetted educator or senior guide with authoring and escalation resolution privileges.
* **Sandbox Runner**: The isolated backend service responsible for compiling and executing untrusted Java code securely against test suites.

---

## 4. Features & Functional Requirements

```
                                  ┌───────────────────────────────┐
                                  │      API Gateway / Auth       │
                                  └───────────────┬───────────────┘
                                                  │
                 ┌────────────────────────────────┼────────────────────────────────┐
                 ▼                                ▼                                ▼
    ┌─────────────────────────┐      ┌─────────────────────────┐      ┌─────────────────────────┐
    │  Curriculum & Progress  │      │   Code Execution Engine │      │ Collaboration & Chat    │
    │  - Tracks & Footholds   │      │   - Secure Sandbox      │      │ - Tier 1: AI Assistant │
    │  - Story Reader         │      │   - Java Test Runner    │      │ - Tier 2: Peer Chat     │
    │  - Load-Bearing Engine  │      │   - Pass/Fail Analytics │      │ - Tier 3: Mentor Ticket │
    └─────────────────────────┘      └─────────────────────────┘      └─────────────────────────┘
```

### 4.1 Authentication & User Management (RBAC)
**Description:** Manages secure onboarding, authentication, profile identities, and role-based permissions across Students, Mentors, and Admins.

#### FR-1: Account Registration & Authentication
* The system shall allow users to register and authenticate via Email/Password.
* The system shall optionally support **Sign in with Google** via OAuth 2.0.
* Realizes UJ-1, UJ-4.

#### FR-2: Role Selection & Access Control (RBAC)
* The system shall support three distinct roles: `STUDENT`, `MENTOR`, and `ADMIN`.
* During registration, the user shall explicitly choose between the `STUDENT` or `MENTOR` role.
* If `STUDENT` is selected, the account shall be activated immediately with no approval required.
* If `MENTOR` is selected, the account shall enter a pending review state and dispatch an approval notification to the `ADMIN`. The user shall not have mentor privileges until an Admin reviews and accepts the request. Once approved, the user can log in with full `MENTOR` access.
* The `ADMIN` role shall have complete access to user approvals, role management, curriculum management, and platform telemetry.
* Realizes UJ-4.

#### FR-3: User Profile & Preferences
* The system shall maintain user profiles containing display name, avatar, bio, and role.
* The system shall dynamically adapt conversational language (English vs. Hinglish) based on the user's chat input without requiring manual language dropdown selection.

---

### 4.2 Progressive Curriculum & Story Reader
**Description:** Presents the "Mountain Climb" curriculum where concepts are organized as an ordered sequence of interconnected footholds.

#### FR-4: Track & Foothold Navigation
* The system shall organize concepts into progressive tracks (Java Basics → Object-Oriented Java → Data Structures → Algorithms).
* Each foothold must clearly state its prerequisite concepts.
* Unlocked footholds shall be visually distinct from locked higher-altitude peaks.
* Realizes UJ-1.

#### FR-5: Text-Only Story Reader
* The system shall render a dedicated, distraction-free reading pane for each concept story.
* The reader shall display formatted Markdown text, inline code snippets, and lightweight SVG diagrams.
* **Constraint**: The reader shall strictly exclude audio and video players in the MVP.
* Realizes UJ-1.

#### FR-6: Curriculum Authoring (Mentors & Admins)
* Mentors and Admins shall have access to a rich-text curriculum editor to create, edit, and publish footholds (Title, Story Text, Problem Statement, Starter Code, Test Cases).
* Realizes UJ-4.

---

### 4.3 In-Browser Code Runner & Automated Test Sandbox
**Description:** Provides an in-browser code editor and a secure micro-service execution engine that compiles and runs student Java code against test cases.

#### FR-7: Code Editor Interface
* The system shall provide an in-browser code editor with Java syntax highlighting, automatic indentation, line numbering, and bracket matching.
* The editor shall come pre-populated with starter class/method boilerplate for the active problem.
* Realizes UJ-1.

#### FR-8: Secure Code Execution & Test Evaluation
* Upon the user clicking **Submit**, the system shall send the code to an isolated execution sandbox.
* The sandbox shall compile and run the Java code against defined test cases with strict resource limits:
  * Maximum execution timeout: **3 seconds per run**.
  * Maximum memory limit: **128 MB**.
  * No outbound network access.
* The system shall return a structured evaluation payload:
  * Overall status: `PASS` (all tests passed) or `FAIL` (at least one test failed).
  * Per-test result: Input, Expected Output, Actual Output, and Execution Time for visible test cases.
  * For hidden test cases: Pass/Fail status without leaking the hidden inputs.
* Realizes UJ-1.

---

### 4.4 Tier-1 Socratic AI Story Debugger
**Description:** An integrated conversational assistant on standby that provides multilingual, story-anchored debugging assistance when a student is stuck or fails a test.

#### FR-9: Narrative-Anchored Context Injection
* When a student fails a test or requests help, the system shall assemble a contextual prompt for the LLM containing:
  1. The current Foothold's Story Analogy.
  2. The Problem Statement.
  3. The Student's current Java code.
  4. The compiler error or failing test case details.
* The LLM prompt shall strictly instruct the AI to:
  * Never give away the raw solution code directly.
  * Explain the logical gap using the story analogy.
  * Respond in the student's conversational language (English, Hinglish, or mixed).
* Realizes UJ-1.

#### FR-10: Socratic Chat Interface
* The system shall provide an inline slide-out chat drawer beside the code editor.
* The assistant must respond with streaming text within **3 seconds** of user submission.
* Realizes UJ-1.

---

### 4.5 Tier-2 School-Friend Peer Solver Doubt Chat
**Description:** Connects a stuck student with a fellow student who has already solved the exact problem.

#### FR-11: Peer Solver Discovery & Messaging
* When a student clicks "Ask a Peer Solver", the system shall query all users who:
  1. Hold the `STUDENT` role.
  2. Have passed all test cases for this specific Foothold.
* The UI shall display a list of available peers with their display names and presence status badges (e.g., Green = Online, Gray = Offline).
* The student shall be permitted to initiate a 1-on-1 direct message with any solver on the list, regardless of whether that peer is online or offline.
* Realizes UJ-2.

#### FR-12: 1-on-1 Peer Chat & Context Sharing
* The system shall enable direct real-time messaging between the two students.
* When initiating the chat, the system shall provide an optional toggle allowing the asker to share a read-only snapshot of their failing code and the failing test error.
* The chat shall remain lightweight and friendly without point deductions, karma tokens, or rating penalties.
* Realizes UJ-2.

---

### 4.6 Tier-3 Human Mentor Portal & Escalation
**Description:** Enables stuck students to escalate hard conceptual blocks to vetted mentors, and equips mentors with direct problem context.

#### FR-13: Mentor Escalation Dispatch
* The student shall be able to click **"Escalate to Mentor"** from the chat drawer.
* The system shall generate an Escalation Ticket containing:
  * Student Profile & Track ID.
  * Active Foothold & Problem ID.
  * Student's current Java code snapshot.
  * Student's doubt description or message.
* *(Note: To keep mentor triage clean and lightweight, raw test execution logs and chat transcripts are omitted from the ticket).*
* Realizes UJ-3.

#### FR-14: Mentor Resolution Dashboard & Direct Chat
* Mentors shall have a dedicated **Escalations Inbox** displaying active student tickets sorted by wait time.
* Mentors shall be able to claim a ticket, review the code diff and logs, and open a direct 1-on-1 chat with the student.
* When the student clears the problem, the mentor can mark the ticket as `RESOLVED`.
* Realizes UJ-3.

---

### 4.7 Progress Tracking & Load-Bearing Progression Engine
**Description:** Manages the student's ascent up the mountain and enforces prerequisite validation.

#### FR-15: Load-Bearing Prerequisite Checks & Progress Reporting
* When a student passes all tests for a Foothold, the system shall check whether the upcoming Foothold has specific load-bearing dependency prerequisites.
* If a prerequisite check is triggered, the system presents a quick 1-question verification problem before opening the next peak.
* The Student Dashboard shall display a visual mountain climb ascent: total problems solved, current active foothold, and streak history.
* Realizes UJ-1.

---

## 5. Cross-Cutting Non-Functional Requirements (NFRs)

### 5.1 Performance & Latency
* **NFR-1 (Code Execution)**: End-to-end code compilation and test execution must complete and render in under **4 seconds** for standard algorithmic programs under normal server load.
* **NFR-2 (AI First Token)**: The Tier-1 AI Assistant must begin streaming response tokens within **2.5 seconds** of a prompt request.
* **NFR-3 (Chat Delivery)**: Real-time peer-to-peer and mentor-to-student messages must be delivered with a latency of less than **300ms** via WebSockets.

### 5.2 Security & Sandbox Isolation
* **NFR-4 (Untrusted Code Execution)**: All user-submitted Java code must run inside ephemeral, unprivileged, isolated execution sandboxes with restricted permissions.
* **NFR-5 (Resource Caps)**: Code processes must be killed immediately if they exceed 3 seconds of CPU time or 128MB of RAM, returning a clean `TimeLimitExceeded` or `MemoryLimitExceeded` error.
* **NFR-6 (Network Isolation)**: The code execution container must have zero network interface access to prevent outbound malicious requests.

### 5.3 Scalability & Micro-services Resilience
* **NFR-7 (Independent Scaling)**: The Code Execution Service, Chat Service, and Core Curriculum Service must run as independent micro-services communicating via REST/gRPC and message brokers (e.g., RabbitMQ/Kafka/Redis).
* **NFR-8 (Graceful Degradation)**: If the Tier-1 AI service is temporarily unavailable, the system must gracefully notify the user and offer direct Tier-2 Peer Chat or Tier-3 Mentor escalation without blocking the code runner or reading pane.

---

## 6. Non-Goals (Explicit)

* **No Video/Audio Streaming**: We are not building or hosting video lectures, YouTube embeds, or podcast clips in this MVP.
* **No Gamification Economies**: We are explicitly not building coin stores, leaderboard badges, or pay-to-unlock hints.
* **No Native Mobile Apps**: MVP is strictly a responsive desktop/mobile Web Application.
* **No Multi-Language IDE**: We are not supporting Python, C++, Go, or Rust in V1.
* **No Paid Tutor Marketplace**: Mentorship in MVP is purely educational guidance; no payment processing, billing, or automated scheduling calendars will be built.

---

## 7. MVP Scope Matrix

| Component | In-Scope for MVP | Explicitly Out of Scope |
|---|---|---|
| **Curriculum** | Java & core DSA tracks, real-world text stories, structured footholds | Video lectures, audio tracks, multi-language curriculum |
| **Code Runner** | In-browser editor, Java test sandbox, 3s timeout, Pass/Fail results | Full terminal access, GUI Java testing, file upload/download |
| **AI Assistant** | Story-anchored Socratic hints, English/Hinglish auto-adaptation | Voice chat, auto-writing full solutions, automated grading |
| **Peer Chat** | List of online/offline solvers for active problem, 1-on-1 text messaging | Group study rooms, voice calls, screen sharing |
| **Mentor System** | Admin approval workflow, escalation tickets, student code view, 1-on-1 chat | Automated billing, hourly booking calendar, video office hours |
| **Auth & Profiles** | Email/Password, optional Google OAuth, Student/Mentor/Admin RBAC | Enterprise SSO (SAML), phone OTP login, public resume profiles |

---

## 8. Success Metrics

### Primary Metrics
* **SM-1 (Core Loop Completion)**: Percentage of enrolled students who successfully complete at least 3 consecutive progressive footholds without dropping off (Target: ≥ 65%). Validates FR-4, FR-8, FR-15.
* **SM-2 (Socratic Hint Breakthrough Rate)**: Percentage of test failures resolved by the student within 3 AI chat turns without needing mentor escalation (Target: ≥ 70%). Validates FR-9, FR-10.
* **SM-3 (Peer Chat Resolution)**: Percentage of Tier-2 peer chats that result in a successful test pass within 15 minutes (Target: ≥ 60%). Validates FR-11, FR-12.

### Counter-Metrics (Do Not Optimize)
* **SM-C1 (Hint Overuse)**: Do not optimize for total number of AI messages sent per user. If students are chatting with the AI 50 times per problem, the hints are too vague or confusing.
* **SM-C2 (Mentor Ticket Volume)**: Do not optimize for high mentor ticket volume. A healthy system resolves 80%+ of issues at Tier 1 (AI) and Tier 2 (Peer), keeping mentor escalations reserved for genuine conceptual bottlenecks.

---

## 9. Assumptions Index

* `[ASSUMPTION 1]`: Mentors will access the platform via the same Web Application interface with role-gated navigation tabs (Curriculum Studio & Escalations Inbox).
* `[ASSUMPTION 2]`: A lightweight WebSocket server handles real-time delivery for both Tier-2 Peer Chat and Tier-3 Mentor Chat.
* `[ASSUMPTION 3]`: The LLM inference service can be powered by an enterprise API (e.g., Gemini Flash / OpenAI) with prompt templates injecting story analogy context.
* `[ASSUMPTION 4]`: In MVP, the initial Java track will launch with a curated seed set of 10–15 footholds covering fundamental loops, arrays, two-pointers, and recursion.
