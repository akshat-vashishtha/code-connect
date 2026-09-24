---
title: Experience Specification — CodeConnect (SaaS Light Workbench)
created: 2026-09-23
updated: 2026-09-24
status: final
---

# Experience Specification: CodeConnect

## 1. Executive Product Architecture

CodeConnect is an engineering mastery platform built with an **enterprise SaaS workbench architecture**. Designed around Java 21 LTS and clean Data Structures & Algorithms (DSA), the system replaces passive tutorial consumption with active problem-solving anchored to **real-world physical mechanisms**.

### Core Platform Tenets
1. **The 3-Tier Resolution Ecosystem**: When a student is blocked, they never face an impenetrable wall. They progress through:
   - **Tier 1 (Instant Socratic AI)**: Clues and conceptual physical models without code spoilers.
   - **Tier 2 (Verified Peer Solvers)**: Live 1-on-1 discussion and code snapshot sharing with classmates who conquered the identical foothold.
   - **Tier 3 (Staff Mentor Escalation)**: Side-by-side diff review and personalized architectural coaching from staff engineers.
2. **Clear Role Segregation**:
   - **Student (Climber)**: Auth, Mountain Tracks, Monaco Cockpit, Socratic AI, Peer Solvers, Ascent Dashboard.
   - **Mentor (Guide)**: Mentor Resolution Desk (Ticket Queue + Side-by-side Diff Viewer), Curriculum Authoring Studio (Story 2.3 - Foothold Builder & Bulk Uploader).
   - **Admin (Governance)**: Platform User Directory, Role Lifecycle Management, Mentor Application Triage (Approve/Reject), Cluster Sandbox Health.
3. **Pure Vector Ergonomics**: 100% SVG mathematical vector paths across all UI components; zero comic emojis or non-standard glyphs.

---

## 2. Information Architecture & Navigation

The platform navigation is structured across 10 wireframe surfaces:

```
[ CodeConnect Master Application Shell ]
│
├── 1. Authentication & Onboarding (`01-epic1-auth-onboarding.svg`)
│     ├── Student Sign In / Sign Up (Instant Workspace Access)
│     ├── Mentor Application (Requires GitHub profile, Bio, Admin Approval)
│     └── Pending Approval Holding State (`/pending-approval`)
│
├── 2. Mountain Curriculum Explorer (`02-epic2-curriculum-reader.svg`)
│     ├── Progressive Ascent Routes (Track 1 ➔ Track 4)
│     ├── Real-World Physical Mechanism Diagram Card
│     └── Foothold Challenge Specifications & Constraints
│
├── 3. Monaco Interactive Coding Cockpit (`03-epic3-coding-cockpit.svg`)
│     ├── 50/50 Dual-Pane Viewport (Problem Spec vs Java 21 Editor)
│     ├── 100% Expand Mode Toggle (`Cmd+B`)
│     └── Test Evaluation Console Tray (Pass/Fail matrix, RAM, Duration, Diagnostics)
│
├── 4. Socratic AI Coach Drawer (`04-epic4-socratic-ai.svg`)
│     ├── Strict Anti-Spoiler Guardrail Banner
│     ├── 3-Tier Progressive Hint Ladder (Metaphor ➔ Formula ➔ Structural Model)
│     └── Hinglish / English Natural Language Toggle
│
├── 5. Peer Solver Network (`05-epic5-peer-solvers.svg`)
│     ├── Verified Solvers Directory (Online Presence & Altitude)
│     ├── 1-on-1 Discussion Tray with Code Snapshot Attachment
│     └── Tier-3 Mentor Escalation Link
│
├── 6. Mentor Resolution Desk (`06-epic6-mentor-desk.svg`)
│     ├── Student Ticket Triage Queue (Urgency, Foothold, Resolution State)
│     ├── Side-by-Side Diff Viewer (Student Attempt vs Canon Implementation)
│     └── Markdown Coaching Feedback Editor & "Mark Resolved" Action
│
├── 7. Mountain Ascent Dashboard (`07-epic7-ascent-dashboard.svg`)
│     ├── Topographic Elevation Ridgeline (Base Camp 0m to Summit 4,000m)
│     ├── 4 Load-Bearing Waypoint Checkpoint Drills
│     └── Momentum Streak & Spaced Repetition Retention Engine
│
├── 8. Curriculum Authoring Studio (`08-epic2-curriculum-authoring-studio.svg`)
│     ├── Track & Foothold Metadata Builder
│     ├── Physical Mental Model Story Editor & SVG Mechanism Uploader
│     ├── Dynamic Test-Case Matrix Builder (Public & Hidden Cases)
│     └── Bulk Curriculum Syllabus JSON/ZIP Package Uploader
│
└── 9. Admin Control Center & Governance (`09-admin-studio-user-governance.svg`)
      ├── Real-Time Platform Metrics Bar
      ├── Mentor Application Triage Queue (Approve / Reject Actions)
      ├── User & Role Governance Directory (Search, Role Filters, Status Actions)
      └── Cluster Sandbox Infrastructure Health Monitor
```

---

## 3. Surface Specifications & Interaction Details

### 3.1 Authentication & Role Onboarding (`01-epic1-auth-onboarding.svg`)
- **Route**: `/login`, `/register`
- **Container**: Centered 440px card on `{colors.bg-base}` with `{rounded.2xl}` and soft shadow.
- **Interactive Role Selector**:
  - Segmented radio card for **Student**: *"Instant access to all tracks, coding cockpit, and AI coach."*
  - Segmented radio card for **Mentor**: *"Author tracks & mentor students. Requires staff review & approval."*
- **OAuth & Credentials**: One-click Google OAuth button, standard email/password inputs with validation feedback.
- **Pending Approval Workflow**: When a user registers as a Mentor, their status is set to `PENDING_APPROVAL`. They are redirected to `/pending-approval` showing a clear holding banner while Admin reviews their application.

### 3.2 Mountain Curriculum Explorer (`02-epic2-curriculum-reader.svg`)
- **Route**: `/curriculum`, `/track/:trackId`
- **Left Column (340px)**: Ascent Route Tree showing tracks (Track 1: Java 21 Mental Models, Track 2: Data Structures) with conquered footholds and altitude progress.
- **Right Column (812px)**:
  - Header: Foothold title, status pill (`CONQUERED`, `ACTIVE`), altitude gain (`+150m`), estimated duration (`25 mins`).
  - Embedded Physical Mechanism Diagram Card: Interactive vector diagram illustrating the mechanical real-world analogy (e.g., Circular Queue airport baggage conveyor carousel with Front/Rear indices).
  - Key Mental Model Banner: Single-sentence structural rule converting abstract algorithms into concrete physical intuition.
  - Action Bar: Prominent primary CTA **"Step Into Code Cockpit (Java 21) →"**.

### 3.3 Monaco Interactive Coding Cockpit (`03-epic3-coding-cockpit.svg`)
- **Route**: `/cockpit/:footholdId`
- **Layout**: 50/50 split container filling the browser viewport.
  - **Left Pane (570px)**: Story narrative, method contract signatures, inputs/outputs, and edge-case constraints. Includes an "Expand" toggle to collapse this pane.
  - **Right Pane (575px)**:
    - Editor Toolbar: Tab with `CircularQueue.java`, `Java 21 LTS` pill, starter code Reset button, and Expand Mode button (`⤢`).
    - Monaco Code Editor: Dark slate theme (`#0f172a`), line numbers, bracket pair colorization, and Java 21 syntax support.
    - Action Buttons: `Run Visible Tests` (secondary button) and `Submit Solution →` (primary blue button).
  - **Bottom Test Console Tray (Height: 210px)**:
    - Tabbed test case results (`Case 1 [PASS]`, `Case 2 [PASS]`, `Case 3 [FAIL]`).
    - Real-time sandbox metrics: execution duration (`18ms`), memory footprint (`24MB`).
    - Detailed Diagnostic Diff: Displays input sequence, expected output, and actual stdout / stack trace.
    - Socratic Clue Recommendation Bar: When tests fail, suggests the relevant physical analogy with a direct CTA to open the Socratic AI drawer.

### 3.4 Socratic AI Clue Drawer (`04-epic4-socratic-ai.svg`)
- **Interaction**: Slides out from the right (width: 450px) over the cockpit without obscuring active editor code.
- **Anti-Spoiler Guardrail Banner**: Explicitly confirms that the coach will never write the solution code, protecting the student's learning ascent.
- **Natural Language Toggle**: Allows switching between English and Hinglish explanations.
- **3-Tier Progressive Hint Ladder**:
  - `Tier 1: Conceptual Metaphor`: Relates the issue back to the physical story (e.g., Baggage Carousel wrap-around).
  - `Tier 2: Formula & Constraints`: Provides the mathematical formula (e.g., `(tail + 1) % capacity`).
  - `Tier 3: Structural Model`: Outlines pseudocode structure, guarded behind a confirmation button to prevent accidental revelation.
- **Quick Prompt Chips**: Pre-canned prompts (*"Explain error in Hinglish"*, *"Why did modulo wrap fail?"*).

### 3.5 Peer Solver Network (`05-epic5-peer-solvers.svg`)
- **Interaction**: Slide-out drawer / dedicated modal (width: 450px) connecting the climber with peers who have already conquered the active foothold.
- **Climber Directory**: Filterable by `ONLINE` and `ALL`. Displays peer name, altitude achieved, and master badge.
- **1-on-1 Chat Tray**:
  - Synced code snippet card displaying student's line-specific blocker.
  - Message thread with syntax-highlighted suggestions.
  - Quick action to escalate to a human mentor if the peer discussion does not resolve the blocker within 10 minutes.

### 3.6 Mentor Resolution Desk (`06-epic6-mentor-desk.svg`)
- **Route**: `/mentor/desk`
- **Role Guard**: Accessible strictly to approved users with `MENTOR` role.
- **Left Column (400px)**:
  - Incoming ticket list with filter pills (`ACTIVE`, `RESOLVED`).
  - Ticket cards display student name, altitude, foothold ID, escalation tier (`TIER 3 ESCALATED`), and student problem summary.
- **Right Column (728px)**:
  - Student report details and failure symptoms.
  - **Side-by-Side Code Diff Viewer**: Compares student attempt (`CircularQueue.java`) directly against the canon reference implementation, highlighting exact logical discrepancies.
  - Mentor Feedback Editor: Markdown text area for entering structural guidance.
  - Action Bar: **"Mark Resolved & Notify Student"** (primary emerald button).

### 3.7 Mountain Ascent Dashboard (`07-epic7-ascent-dashboard.svg`)
- **Route**: `/dashboard`
- **Header**: Climber altitude (`1,420m / 4,000m`), active streak (`5 Days`), and next objective prompt.
- **Main Elevation Map (700px width)**:
  - Topographic ridgeline visualization showing 4 load-bearing camps:
    - Base Camp (0m) ➔ Camp 1 (1,000m) ➔ Camp 2 (2,200m) ➔ High Camp (3,100m) ➔ Summit (4,000m).
  - Waypoints indicate completed checkpoints (green), active challenge (blue pulse), and locked checkpoints (muted gray).
- **Right Column (428px)**:
  - Spaced Repetition Retention Ring: Visual retention score (`88% Peak Retention`) and schedule for reviewing previously conquered concepts.
  - Track Ascent Progress Cards: Progress percentages across Java Basics, OOP, Data Structures, and System Design.

### 3.8 Curriculum Authoring Studio (`08-epic2-curriculum-authoring-studio.svg`)
- **Route**: `/curriculum/studio`
- **Role Guard**: Accessible to `MENTOR` and `ADMIN`.
- **Foothold Authoring Builder (816px width)**:
  - Metadata row: Track selection, Foothold ID, Name, Target Altitude gain in meters.
  - Conceptual Story & Real-World Analogy Builder: Markdown editor requiring an attached physical mechanism.
  - Method Contract & Java 21 Starter Code Editor.
  - Dynamic Test-Case Matrix: Define inputs, expected return values, and toggle `Hidden Test Case` flag.
  - Action Bar: `Save Draft` and `Publish Foothold` (primary blue button).
- **Right Column (312px)**:
  - Bulk Syllabus Uploader: Drag-and-drop zone accepting `.json` curriculum manifests or `.zip` packages for batch track publishing.
  - Curriculum Quality Checklist: Verification gates ensuring every foothold contains a real-world story and test cases.

### 3.9 Admin Control Center & Governance (`09-admin-studio-user-governance.svg`)
- **Route**: `/admin/governance`
- **Role Guard**: Accessible strictly to `ADMIN` superusers.
- **Top Metrics Bar**: 4 stat cards showing Active Students, Verified Mentors, Pending Mentor Applications, and Cluster Sandbox Health (`100% OK, k3d, Redis, Mongo`).
- **Mentor Application Triage**:
  - List of pending mentor applicants showing name, employer, years of experience, primary technical domain, and verified GitHub profile link with external arrow vector.
  - Actions: **Approve Mentor** (emerald button, immediately grants `MENTOR` role) and **Reject** (muted button).
- **Platform User & Role Governance Directory**:
  - Search bar and role filters (`ALL`, `STUDENTS`, `MENTORS`, `ADMINS`).
  - User table displaying climber details, role badge, altitude progress, account status (`ACTIVE`, `SUSPENDED`), and `Manage User` action menu.

---

## 4. Key End-to-End User Journeys

### Journey 1: Student Ascent from Physical Analogy to Passing Sandbox
1. Arjun navigates to `/dashboard` and clicks **"Resume Foothold 2.3: Circular Queue"**.
2. On `/curriculum`, Arjun reads the story of the **Airport Baggage Conveyor Carousel** and examines the circular wrap-around diagram card.
3. Arjun clicks **"Step Into Code Cockpit"**, opening the 50/50 dual pane on `/cockpit/2.3`.
4. Arjun writes his initial queue implementation and hits `Ctrl+Enter` (`Run Visible Tests`).
5. Tests 1 & 2 pass, but Test 3 throws `ArrayIndexOutOfBoundsException: Index 5 out of bounds`.
6. Arjun clicks the **"Socratic Clue"** bar, which slides out the Socratic AI drawer.
7. The AI points out: *"When your carousel reaches capacity, how does the next bag wrap to slot 0?"*
8. Arjun updates his logic to `(rear + 1) % capacity`, hits `Ctrl+Shift+Enter` (`Submit Solution`), passes all visible and hidden tests, and gains `+150m` altitude.

### Journey 2: Mentor Registration and Admin Approval Flow
1. Dr. Priya Sen visits `/register`, selects the **Mentor** role card, enters her Swiggy work email, 10 years experience, and GitHub profile URL.
2. Upon submission, Dr. Priya sees the pending approval banner informing her that an Admin will review her credentials.
3. Admin logs into `/admin/governance`, sees `1 Urgent Pending Mentor App` in the yellow stat card.
4. Admin reviews Dr. Priya's GitHub link (`github.com/psen-java`) and clicks **"Approve Mentor"**.
5. Dr. Priya's role is updated to `MENTOR` in MongoDB. She receives an approval confirmation email, logs in, and gains immediate access to the **Mentor Desk** and **Curriculum Studio**.

### Journey 3: Mentor Resolves Student Tier-3 Escalation
1. Alex gets stuck on Foothold 2.3 and submits a Tier-3 mentor escalation ticket.
2. Mentor Vikram opens `/mentor/desk`, sees Alex's ticket in the triage queue marked `TIER 3 ESCALATED`.
3. Vikram clicks the ticket. The side-by-side diff viewer renders Alex's code on the left and the canon solution on the right, highlighting Alex's omitted count variable.
4. Vikram writes targeted architectural guidance: *"Maintain an explicit count integer so empty vs full buffer states can be disambiguated when head == tail."*
5. Vikram clicks **"Mark Resolved & Notify Student"**. Alex receives an instant notification in his cockpit tray with the mentor's feedback.

---

## 5. Keyboard Shortcuts & Power-User Ergonomics

| Shortcut (Mac) | Shortcut (Windows/Linux) | Action | Scope |
| :--- | :--- | :--- | :--- |
| `Cmd + Enter` | `Ctrl + Enter` | Run Visible Test Cases | Monaco Coding Cockpit |
| `Cmd + Shift + Enter` | `Ctrl + Shift + Enter` | Submit Solution (All Tests) | Monaco Coding Cockpit |
| `Cmd + B` | `Ctrl + B` | Toggle 50/50 Split to Full Editor | Monaco Coding Cockpit |
| `Cmd + K` | `Ctrl + K` | Open Global Command Palette | Platform Wide |
| `Esc` | `Esc` | Close Auxiliary Drawers (AI, Peers) | Cockpit & Drawers |
