# CodeConnect SaaS UI & Wireframe Specification
**Authors:** Sally (🎨 Lead UX Designer) &amp; John (📋 Product Manager)  
**Target Consumer:** Amelia (💻 Senior Software Engineer)  
**Standard:** Enterprise SaaS Workbench (Linear / LeetCode / Raycast grade)  
**Base URL:** `http://localhost/`  
**Status:** Approved for Frontend Engineering Implementation  

---

## 1. Executive Summary & Design Transformation

### 1.1 The Identified Gap
The initial iteration of the web frontend leaned heavily into marketing-style informational copy and long-form narrative text. For an engineering mastery platform, this felt like reading an editorial blog rather than interacting with a high-performance **developer workbench**.

### 1.2 The Transformation Mandate
We have redesigned the user experience across all 7 Epics from the ground up:
- **Cockpit-First Density:** Information is organized into high-density split viewports (50/50 editor and spec panes, slide-out contextual drawers, live trays).
- **Physical Mental Models over Text Walls:** Long narrative text is replaced with structural callouts, method contracts, and real-world system architecture diagrams (e.g., airport baggage conveyor for circular queues, postal mailbox grids for array indexing).
- **3-Tier Support Triaging:** Seamless escalation from Socratic AI hints (Tier 1) ➔ Verified Peer Network (Tier 2) ➔ Staff Mentor Escalation Desk (Tier 3).
- **Visual Progression:** Topographic altitude meters, waypoints, and load-bearing checkpoint challenges replace simple checklist items.

---

## 2. Design System Tokens & Foundations

| Token Category | Token Name | Value | Usage |
| :--- | :--- | :--- | :--- |
| **Canvas Background** | `surface-bg` | `#f8fafc` (Slate 50) | Platform body canvas |
| **Elevated Surfaces** | `surface-card` | `#ffffff` (Pure White) | Cards, panels, viewports, tables |
| **Borders & Dividers** | `border-subtle` | `#e2e8f0` (Slate 200) | 1px clean container outlines |
| **Primary Accent** | `brand-blue` | `#2563eb` (Blue 600) | Primary actions, climb highlights, active tabs |
| **Brand Soft Blue** | `brand-blue-subtle` | `#eff6ff` (Blue 50) | Active selected cards, breadcrumbs |
| **Success / Conquered** | `status-success` | `#059669` (Emerald 600) | Passed tests, conquered footholds, online |
| **Warning / Pending** | `status-warning` | `#b45309` (Amber 700) | Pending approval, locked checkpoints |
| **Code Editor Slate** | `code-dark` | `#0f172a` (Slate 900) | Monaco editor background, diff windows |
| **Typography Family** | `font-sans` | `Plus Jakarta Sans`, `-apple-system`, `sans-serif` | Clean geometric UI typography |
| **Code Typography** | `font-mono` | `JetBrains Mono`, `Fira Code`, `monospace` | Method signatures, editor, diffs |
| **Drop Shadow** | `elevation-1` | `0 2px 4px rgba(15, 23, 42, 0.05)` | Elevated cards and floating trays |
| **Drawer Shadow** | `elevation-drawer`| `-4px 0 12px rgba(15, 23, 42, 0.15)` | Slide-out drawers and modals |

---

## 3. Master Wireframe Suite (10 Publication-Grade SVGs)

All wireframes are drawn to exact pixel grids at **1200 × 750px** viewport dimensions, cleanly segregated by user role:

```
_bmad-output/planning-artifacts/ux-designs/wireframes/
├── 00-app-shell.svg                        # Global SaaS Navigation & Header Shell
├── 01-epic1-auth-onboarding.svg             # Epic 1: Auth, Role Selection & Pending Approval
├── 02-epic2-curriculum-reader.svg           # Epic 2: Student Mountain Track Explorer & Reader
├── 03-epic3-coding-cockpit.svg              # Epic 3: 50/50 Interactive Editor & Test Console
├── 04-epic4-socratic-ai.svg                 # Epic 4: Slide-out Socratic AI Debugger Drawer
├── 05-epic5-peer-solvers.svg                # Epic 5: Verified Peer Solver Network & 1-on-1 Chat
├── 06-epic6-mentor-desk.svg                 # Epic 6: Mentor Escalation Resolution Desk
├── 07-epic7-ascent-dashboard.svg            # Epic 7: Topographic Mountain Climb & Checkpoint
├── 08-epic2-curriculum-authoring-studio.svg # Epic 2 (Story 2.3): Mentor & Admin Curriculum Studio
└── 09-admin-studio-user-governance.svg      # Admin Portal: Mentor Approvals & User Governance
```

---

## 4. Epic-by-Epic Layout & Interaction Specifications

### 4.0 Global App Shell (`00-app-shell.svg`)
👉 **Wireframe Asset:** [`00-app-shell.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/00-app-shell.svg)

- **Structure:**
  - **Top App Header (64px):** Brand logo (`CC`), breadcrumb hierarchy (`Track 2 / Foothold 3 / Circular Queue`), Altitude meter (`1,420m / 4km`), Streak counter (`5 Days`), Language switcher (`Hinglish / EN`), User avatar with menu.
  - **Left Navigation Rail (72px):** Compact vertical icon rail featuring:
    1. `CLIMB` (Ascent Dashboard & Tracks)
    2. `CODE` (Interactive Coding Cockpit)
    3. `COACH` (Socratic AI Story Debugger)
    4. `PEERS` (Peer Solver Network with live presence badge)
    5. `MENTOR` (Staff Mentor Escalations)
    6. `ADMIN` (Admin Verification Studio)
  - **Main Viewport (1128px fluid):** Dynamic routing canvas.

---

### 4.1 Epic 1: Foundation, User Authentication & Role Governance (`01-epic1-auth-onboarding.svg`)
👉 **Wireframe Asset:** [`01-epic1-auth-onboarding.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/01-epic1-auth-onboarding.svg)

- **Core User Flow:**
  - **Left Brand Card (460px):** Night-sky mountain ridge narrative emphasizing *"Master Core Engineering. No Hand-Waving."* with clear visual callouts for the 3-Tier support ecosystem.
  - **Right Auth Form (680px):**
    - Seamless tab toggle: `Create Account` vs `Sign In`.
    - **Interactive Role Selector:** Radio cards comparing `Climber / Student` vs `Technical Mentor`.
    - Inputs: Full Name, Email Address, Strong Password with 4-bar strength indicator.
    - Code of Honor agreement checkbox.
    - **Role Guard Warning Banner:** Explains that Mentor sign-ups automatically enter `/pending-approval` state until credentials (GitHub/LinkedIn) are vetted in the Admin Studio.

---

### 4.2 Epic 2: Mountain Curriculum Exploration & Text Story Reader (`02-epic2-curriculum-reader.svg`)
👉 **Wireframe Asset:** [`02-epic2-curriculum-reader.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/02-epic2-curriculum-reader.svg)

- **Core User Flow:**
  - **Left Rail (340px): Ascent Route & Track Tree:**
    - Track 1 (Java 21 Models, 500m) - Conquered (Green checkmarks).
    - Track 2 (Data Structures, 1,500m) - Active track with Footholds 2.1 through 2.5.
    - Track 3 (Concurrency, 2,800m) & Track 4 (Distributed, 4,000m) - Locked with prerequisite indicator.
  - **Right Canvas (800px): Distraction-Free Conceptual Story Reader:**
    - Real-world physical conveyor mechanism illustration (Airport Baggage Carousel) replacing walls of text.
    - Clean callout comparing naive arrays vs circular wrap-around modulo indices.
    - "Conquer Foothold in Cockpit ->" direct action button.

---

### 4.3 Epic 3: Interactive Coding Cockpit & Asynchronous Sandbox Execution (`03-epic3-coding-cockpit.svg`)
👉 **Wireframe Asset:** [`03-epic3-coding-cockpit.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/03-epic3-coding-cockpit.svg)

- **Core User Flow:**
  - **50/50 Vertical Split:**
    - **Left Spec Pane (50%):** Problem contract (`CircularQueue`), constraints (`O(1)` runtime, bounded memory), story analogy refresher box, and pre-conditions.
    - **Right Editor & Sandbox Pane (50%):**
      - Full Monaco code editor with Java 21 syntax highlighting.
      - Action Bar: `Run Tests` (Sandbox execution) and `Submit Foothold ->`.
      - **Bottom Test Diagnostic Console Tray (Height: 220px):** Displays automated test results (`Case 1: Passed`, `Case 2: Passed`, `Case 3: Failed IndexOutOfBoundsException`), showing input/expected/actual diffs.
      - One-click trigger: `"Stuck? Ask Socratic AI (Tier 1) ->"`.

---

### 4.4 Epic 4: Socratic AI Story Debugger (`04-epic4-socratic-ai.svg`)
👉 **Wireframe Asset:** [`04-epic4-socratic-ai.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/04-epic4-socratic-ai.svg)

- **Core User Flow:**
  - **Slide-out Right Drawer (450px):** Anchored seamlessly beside the Monaco editor over a subtle backdrop.
  - **Language Mode Toggle:** Instant switch between `EN` and `Hinglish` (e.g., *"Socho airport ke circular belt ke baare mein..."*).
  - **Progressive Disclosure (3 Hint Tiers):**
    - Tier 1: Real-world physical analogy (Baggage carousel wrap).
    - Tier 2: Mathematical formula hint (`(tail + 1) % capacity`).
    - Tier 3: Structural code pseudocode (locked behind an explicit user confirmation button to preserve learning integrity).
  - Quick action chips and direct escalation trigger to Tier 2 Peer Network.

---

### 4.5 Epic 5: Peer Solver Network & 1-on-1 Chat (`05-epic5-peer-solvers.svg`)
👉 **Wireframe Asset:** [`05-epic5-peer-solvers.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/05-epic5-peer-solvers.svg)

- **Core User Flow:**
  - **Strict Access Gate:** Unlocked only for students who have attempted the foothold.
  - **Left Table (680px): Verified Solvers Directory:**
    - Shows only students who have *already conquered this specific foothold* (prevents misinformation).
    - Presence status (`🟢 Online in Cockpit`), altitude score, and conquered strategy tag (`Explicit Count Buffer`, `Modulo Ring Trick`).
  - **Right Panel (456px): 1-on-1 Collaborative Chat:**
    - Peer direct messaging with anti-plagiarism honor banner.
    - Real-time **Code Snapshot Sync** card showing the student's problem snippet to the peer for pinpoint debugging.

---

### 4.6 Epic 6: Mentor Escalation Resolution Desk (`06-epic6-mentor-desk.svg`)
👉 **Wireframe Asset:** [`06-epic6-mentor-desk.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/06-epic6-mentor-desk.svg)

- **Role:** `ROLE_MENTOR`
- **Core User Flow:**
  - **Left Queue (380px): Incoming Student Blockers:**
    - Triage queue of escalated tickets (e.g. Alex V. on Foothold 2.3, Kavita M. on Foothold 1.5).
    - Status badges: `TIER 3 ESCALATED`, `IN REVIEW`, `RESOLVED`.
    - Automated pre-flight audit: Confirms Tier 1 AI hints (3/3 used) and Tier 2 peer attempts were exhausted.
    - Mentor Resolution Metrics: Avg resolution time (8.4m), 128 footholds verified.
  - **Right Resolution Center (756px): Deep Inspection & Clue Dispatch:**
    - Student blocker summary note.
    - **Synchronized Side-by-Side Code Diff Window:** Student working copy vs expected invariant assertions with line highlighting (missing modulo wrapping on `tail`).
    - Mentor Socratic Dispatch Editor: Composes architectural guidance without copy-paste code.
    - Action Toolbar: `Dispatch Clue to Alex →`, `Start 5m Voice Huddle`, and `Mark Resolved & Unlock Foothold`.

---

### 4.7 Epic 7: Load-Bearing Progression & Mountain Climb Dashboard (`07-epic7-ascent-dashboard.svg`)
👉 **Wireframe Asset:** [`07-epic7-ascent-dashboard.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/07-epic7-ascent-dashboard.svg)

- **Role:** `ROLE_STUDENT`
- **Core User Flow:**
  - **Top Row (90px): 4 Key Ascent Metric Tiles:**
    1. Current Altitude (`1,420m / 4,000m`)
    2. Footholds Conquered (`9 / 20`)
    3. Ascent Streak (`5 Days`)
    4. Retention Verification Index (`100%`)
  - **Main View (700px): Topographic Ascent Elevation Map:**
    - Visual mountain contour with 4 camps: Base Camp (0m), Camp 1 (500m), Camp 2 (1,500m), Camp 3 (2,800m), Summit (4,000m).
    - Dynamic climber radar node displaying *"YOU ARE HERE (1,420m)"*.
  - **Right View (436px): Load-Bearing Verification Modal/Card:**
    - Prerequisite gate before advancing between major tracks.
    - High-retention conceptual question with radio answers and physical story analogy clue.
    - Action: `Verify Retention & Unlock Track 3 →`.

---

### 4.8 Epic 2 (Story 2.3): Mentor & Admin Curriculum Authoring & Upload Studio (`08-epic2-curriculum-authoring-studio.svg`)
👉 **Wireframe Asset:** [`08-epic2-curriculum-authoring-studio.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/08-epic2-curriculum-authoring-studio.svg)

- **Role:** `ROLE_MENTOR` and `ROLE_ADMIN`
- **Core User Flow:**
  - **MVP Requirement:** Core capability for Mentors and Admins to author, upload, test, and publish new mountain curriculum.
  - **Left Rail (320px): Curriculum Track Catalog & Bulk Importer:**
    - Tree view of tracks and existing published footholds.
    - Drafts in progress (`DRAFT`, `PUBLISHED`).
    - **Bulk Curriculum Uploader:** Drag-and-drop zone for `.yaml` or `.md` batch curriculum modules.
  - **Right Canvas (816px): Foothold Authoring & Test Suite Builder:**
    - Metadata: Target Track, Foothold Title, Altitude Target (`1,600m`).
    - Physical Story Analogy Builder: Real-world mechanism narrative (e.g. Hospital ER Triage for Min-Heap) + diagram attachment.
    - Java 21 Starter Code Editor & Method Contract (`public void insert(int val)`, `public int pollMin()`).
    - Automated Test Suite Builder: Visible Test Cases + Hidden Load-Bearing Boundary Tests (`O(log N)` runtime constraint).
    - Pre-flight Sandbox Execution: `Pre-flight Test in Sandbox` against reference solution.
    - Action Toolbar: `Save Draft`, `Import YAML`, and `Publish Foothold →`.

---

### 4.9 Epic 1 & 6: Admin Control Center & User Governance (`09-admin-studio-user-governance.svg`)
👉 **Wireframe Asset:** [`09-admin-studio-user-governance.svg`](file:///_bmad-output/planning-artifacts/ux-designs/wireframes/09-admin-studio-user-governance.svg)

- **Role:** `ROLE_ADMIN` (Superuser only)
- **Core User Flow:**
  - **Top Metrics Row (74px):** Active Students (4,290), Verified Mentors (86), Pending Mentor Applications (2 Urgent), Sandbox Cluster Health (100% OK).
  - **Main Section 1 (240px): Mentor Application Triage Queue:**
    - Applicants in `/pending-approval` state with credentials, company, years of Java experience, and GitHub/LinkedIn links.
    - One-click actions: `Approve Mentor` (promotes role to `ROLE_MENTOR`) or `Reject`.
  - **Main Section 2 (310px): Platform User Directory & Role Governance:**
    - Live search input + filter pills (`ALL`, `STUDENTS`, `MENTORS`, `ADMINS`).
    - User list with role badge, altitude, account status (`ACTIVE`, `SUSPENDED`), and `Manage User` dropdown.
    - Security & compliance audit strip.

---

## 5. Amelia's Developer Implementation Guide

Amelia can implement the React/Next.js components matching the wireframes using this component hierarchy:

```
src/
├── components/
│   ├── atoms/
│   │   ├── Badge.tsx               # Status pills (ACTIVE, PENDING, LOCKED, PUBLISHED)
│   │   ├── AltitudeMeter.tsx       # Mountain elevation badge (1,420m)
│   │   ├── StreakBadge.tsx         # Streak indicator (5 Days)
│   │   ├── LanguageToggle.tsx      # Hinglish / English switcher
│   │   └── CodeBlock.tsx           # Syntax highlighted snippet viewer
│   ├── molecules/
│   │   ├── BreadcrumbBar.tsx       # Platform context trail
│   │   ├── RoleSelector.tsx        # Student vs Mentor radio cards
│   │   ├── TestResultCard.tsx      # Unit test result row with diffs
│   │   ├── TestCaseBuilderRow.tsx  # Visible/Hidden test input row
│   │   └── HintAccordion.tsx       # 3-tier progressive hint disclosure
│   ├── organisms/
│   │   ├── AppHeader.tsx           # Top global header
│   │   ├── AppNavRail.tsx          # Left 72px vertical icon rail
│   │   ├── MonacoCockpit.tsx       # 50/50 Split editor & spec pane
│   │   ├── SocraticDrawer.tsx      # Slide-out AI debugger drawer
│   │   ├── PeerChatTray.tsx        # 1-on-1 collaborative chat modal
│   │   ├── MentorDiffDesk.tsx      # Mentor side-by-side code diff review
│   │   ├── CurriculumStudio.tsx    # Mentor foothold creator & test builder
│   │   ├── AdminGovernanceTable.tsx# Admin mentor approvals & user table
│   │   └── TopoElevationMap.tsx    # Stylized mountain waypoint map
│   └── views/
│       ├── AuthView.tsx            # Epic 1 Onboarding & Login
│       ├── CurriculumView.tsx      # Epic 2 Track Explorer & Reader
│       ├── CockpitView.tsx         # Epic 3 Interactive Coding Cockpit
│       ├── PeerNetworkView.tsx     # Epic 5 Verified Solvers Directory
│       ├── MentorDeskView.tsx      # Epic 6 Escalations Resolution Desk
│       ├── CurriculumStudioView.tsx# Epic 2 (Story 2.3) Authoring Studio
│       ├── AdminStudioView.tsx     # Epic 1 & 6 Admin Governance Portal
│       └── DashboardView.tsx       # Epic 7 Mountain Ascent Dashboard
```

---

## 6. Approvals & Sign-Off

- **Sally (🎨 UX Designer):** *"Mentors and Admins now each have clean, dedicated environments tailored to their distinct workflows, and the Curriculum Studio provides a first-class authoring experience."*
- **John (📋 Product Manager):** *"Confirming that Curriculum Authoring (Story 2.3 / FR-6) is a vital MVP requirement. Without it, mentors cannot seed the mountain footholds that students climb."*
- **Winston (🏗️ System Architect):** *"Architecturally, this maps cleanly to `curriculum-service` on port 8082 (`POST /api/v1/curriculum/author/footholds`) and `user-service` admin APIs (`POST /api/v1/admin/mentors/{id}/approve`)."*
- **Amelia (💻 Senior Developer):** *"Wireframe specs and component breakdown received. The UI structure is unambiguous, role-segregated, and ready for strict TypeScript implementation."*

