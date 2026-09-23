---
title: Experience Specification — CodeConnect
created: 2026-09-23
updated: 2026-09-23
status: final
---

# Experience Specification: CodeConnect (Professional Light System)

## 1. Foundation & Layout System
CodeConnect is a responsive web application designed with a **clean, professional light theme** that delivers maximum clarity, focus, and trust.

The application operates across five core surface templates:
1. **Auth Surface**: Focused single-card container for frictionless login, registration, and role selection.
2. **Student Dashboard Surface**: Overview of the student's mountain ascent, active tracks, and recent breakthroughs.
3. **The Coding Cockpit Surface**: The primary learning workspace, featuring a **50/50 split** between the conceptual text story and the HackerRank-inspired Java code runner, complete with a one-click **Editor Expand Mode**.
4. **Collaboration & Chat Surface**: Available as an in-cockpit slide-out drawer or a full-page messaging view connecting students with the Socratic AI, online/offline Peer Solvers, and human Mentors.
5. **Admin Management Surface**: Administrative control center for reviewing mentor approval requests, managing user roles, and monitoring platform activity.

All visual styles, colors, and typography inherit from [DESIGN.md](file:///Users/Akshat.x.Vashishtha/Machine/Learnings/projects/git/idea/_bmad-output/planning-artifacts/ux-designs/ux-codeconnect-2026-09-23/DESIGN.md).

---

## 2. Information Architecture & Navigation

```
[ CodeConnect Root Navigation ]
│
├── 1. Auth Surface
│     ├── Sign In (Email/Password or Google OAuth)
│     └── Sign Up (Role Picker: Student [Instant] | Mentor [Pending Admin Review])
│
├── 2. Student Dashboard
│     ├── Ascent Progress Hero (Current Streak, Active Foothold, Next Milestone)
│     ├── Track Cards (Java Basics, OOP, Data Structures, Algorithms)
│     └── Visual Foothold Roadmap (Completed [Green], Active [Blue], Locked [Gray])
│
├── 3. The Coding Cockpit (50/50 Split + Expandable)
│     ├── Left Panel: Story Reader + Problem Prompt + Input/Output Constraints
│     └── Right Panel: Java Editor + Run/Submit Actions + Bottom Test Console
│
├── 4. Collaboration & Chat Center (Drawer & Dedicated View)
│     ├── Tab 1: Socratic AI Guide (Story-anchored diagnostic debugging)
│     ├── Tab 2: Peer Solver Directory (Online & Offline solver list + 1-on-1 thread)
│     └── Tab 3: Mentor Escalation (Pending tickets + direct coach conversation)
│
├── 5. User Profile Surface
│     ├── User Bio, Role Badge, Account Settings
│     └── Solved Problems History & Code Solutions
│
└── 6. Admin Management Portal (Admins Only)
      ├── Metrics Overview (Active Students, Verified Mentors, Pending Requests)
      ├── Mentor Approval Queue (Review applicant, Approve/Reject action)
      └── User Directory (Search, filter by role, edit/deactivate accounts)
```

---

## 3. Surface Specifications & User Experiences

### 3.1 Authentication & Role Selection (`/login`, `/signup`)
* **Layout**: Centered card (width: 440px) on `{colors.bg-base}` with subtle elevation.
* **Role Selection**:
  * On the Sign Up tab, the user encounters a clear segmented role toggle:
    * **Student**: Microcopy: *"Instant access to all tracks, coding sandbox, and AI guide."*
    * **Mentor**: Microcopy: *"Author tracks and mentor students. Requires Admin review and approval before activation."*
* **OAuth Integration**: One-click **"Continue with Google"** button with official branding.
* **Email Verification**: Clean confirmation feedback toast upon registration.
* **Pending Mentor Screen**: If a user signs up as a Mentor, upon submission they see a dedicated status card:
  * *"Thank you for applying to mentor on CodeConnect! Your application has been sent to the Admin team for review. You will receive an email once approved."*

---

### 3.2 Student Dashboard (`/dashboard`)
* **Header / Greeting**: *"Welcome back, Arjun. You are climbing the Java & DSA Track."*
* **Metrics Snapshot**:
  * Active Foothold: `Foothold 4: Two-Pointer Technique`
  * Progress: `7 of 18 Footholds Mastered (38%)`
  * Momentum: `🔥 4-Day Streak`
* **Resume Action**: Prominent primary button: **"Resume Ascent →"** which takes the student straight into their active foothold cockpit.
* **Track Roadmap Visualizer**:
  * Interactive vertical ascent path:
    * **Passed Nodes**: Green circle with checkmark, clickable to review past code.
    * **Current Node**: Pulsing cyan ring with "Current Foothold" label.
    * **Locked Nodes**: Muted padlock icon, displaying required prerequisites on hover.

---

### 3.3 The Coding Cockpit (`/track/{id}/foothold/{id}`)
* **Layout**: 50/50 split by default, filling the entire browser viewport beneath the 60px header.
* **Left Panel (Story & Problem)**:
  * Story Box: Formatted with clean typography and a light tinted background. Explains the real-world mental model (no jargon).
  * Problem Statement: Input/Output specifications, constraints, and sample test cases with copy buttons.
* **Right Panel (HackerRank-Inspired Editor & Console)**:
  * Top Bar: Language indicator (`Java 17`), Reset Starter Code button, and the **Expand Editor** (`⤢`) button.
  * In-Browser Editor: High-contrast light theme with line numbers, code completion, and syntax highlighting.
  * **Expand Mode**: Clicking `⤢` (or pressing `Cmd+B`) collapses the left story pane to a slim 48px vertical rail, expanding the code editor to ~95% width for undistracted coding.
  * Bottom Controls:
    * **"Run Code"** (Secondary outline button; shortcut: `Cmd+Enter`).
    * **"Submit"** (Primary green button; shortcut: `Cmd+Shift+Enter`).
  * Test Results Drawer:
    * Sits beneath the editor. Displays horizontal test chips (`Test 1`, `Test 2`, etc.).
    * Green badge for `PASS`, red badge for `FAIL`.
    * Clicking a failed test expands the expected vs. actual output and displays an amber action button: *"Ask AI what went wrong on this case"*.

---

### 3.4 Collaboration & Chat Center (Drawer & Dedicated View)
* **Drawer Behavior**: Sits docked on the right side of the Cockpit (width: 380px). Slides in smoothly without covering the editor. Can also be opened as a full-page view under `/messages`.
* **Tab 1: Socratic AI Guide**:
  * Interactive chat stream.
  * Automatically injects the active problem and story metaphor into prompt context.
  * AI messages are styled in subtle amber-bordered cards with clear Socratic prompts (English or Hinglish).
* **Tab 2: Peer Solver Directory**:
  * Lists students who have successfully passed this specific problem.
  * Displays: Avatar, Student Name, Solved Date, and Presence Badge:
    * **Online**: Solid green badge (`● Online`)
    * **Offline**: Light gray badge (`○ Offline`)
  * Clicking any peer opens a direct 1-on-1 messaging thread.
  * Message Input includes a **"Attach Code Snapshot"** toggle so the peer can see the exact blocker.
* **Tab 3: Mentor Escalation**:
  * One-click form: *"Describe where you are stuck for the mentor"*.
  * Shows ticket status: `Pending Admin/Mentor Claim` → `Claimed by Mentor Vikram` → `Active Discussion`.

---

### 3.5 User Profile Surface (`/profile`)
* **Profile Header**: Avatar, Display Name, Role Badge (`STUDENT` / `MENTOR` / `ADMIN`), Bio, and Account Settings link.
* **Learning Stats**: Total problems solved, favorite tracks, and current streak.
* **Solved Problems History Table**:
  * Columns: Foothold Name, Track, Date Completed, Execution Speed, and "View Solution" modal trigger.

---

### 3.6 Admin Management Dashboard (`/admin`)
* **Role Guard**: Accessible strictly to users authenticated with the `ADMIN` role.
* **Top Metric Cards**:
  * `Total Active Students` | `Verified Mentors` | `Pending Mentor Approvals` | `Daily Submissions`
* **Pending Mentor Approvals Section (High Priority)**:
  * Prominent review table showing pending mentor sign-ups.
  * Columns: Applicant Name, Email, Registration Date, Submitted Experience/Bio.
  * Actions:
    * **Approve** (Emerald button): Instantly assigns `MENTOR` role and triggers confirmation email.
    * **Reject** (Subtle red text button): Rejects application with optional feedback note.
* **User Management Table**:
  * Search bar (name/email) + Role filter dropdown (`All`, `Student`, `Mentor`, `Admin`).
  * Actions: Change Role, Reset Password, or Deactivate Account.
* **Curriculum Overview**: Quick link to the Curriculum Studio to add or edit footholds.

---

## 4. Voice and Tone (Microcopy)

* **Calm & Professional**: Clean, modern, respectful prose across all UI text.
* **Constructive Debugging**: Errors are treated as natural checkpoints on the climb.
  * *Instead of*: `Error 500: Execution timed out.`
  * *CodeConnect says*: `Your program ran past the 3-second limit. Check if your loop has an exit condition that never triggers.`
* **Personalized AI Tone**: The AI Socratic guide uses natural, encouraging language (adapting to English or Hinglish based on how the student writes).

---

## 5. Key Flow Narratives

### Flow 1: Mentor Sign-Up and Admin Approval
1. **Sign Up**: Vikram visits `/signup`, enters his credentials, and selects the **Mentor** role tab.
2. **Pending Notice**: Upon submitting, Vikram sees the friendly pending approval message.
3. **Admin Review**: Admin opens `/admin`, sees a red badge `"1 Pending Mentor Application"`.
4. **Accept**: Admin clicks **"Approve"**. Vikram's account is upgraded to `MENTOR`.
5. **Login**: Vikram receives an approval email, logs in, and his topbar now includes the **Curriculum Studio** and **Escalations Inbox** tabs.

### Flow 2: Student Codes, Expands Editor, and Solves with AI Hint
1. **Dashboard**: Arjun clicks **"Resume Ascent"** on `/dashboard`.
2. **50/50 View**: Arjun reads the story of the Two Guides on the left, then clicks into the editor on the right.
3. **Expand**: Arjun wants full screen width to write helper methods, so he presses `Cmd+B`. The story collapses to a slim rail, and the editor fills 95% of the screen.
4. **Run**: Arjun hits `Cmd+Enter`. Test 1 passes, but Test 2 fails.
5. **Hint**: Arjun clicks the failed chip's AI prompt. The right drawer slides out.
6. **Breakthrough**: The AI prompts him about pointer crossing. Arjun adjusts his condition, presses `Cmd+Shift+Enter` (Submit), and passes all tests with green confetti.

### Flow 3: Messaging an Offline Peer Solver
1. **Ask Peer**: Arjun gets stuck on a dynamic programming foothold and opens Tab 2 in the Help Drawer.
2. **Directory**: He sees **Rohan** is marked `Offline`, but Rohan solved this problem yesterday.
3. **Send Question**: Arjun clicks Rohan, attaches his code snippet, and sends: *"Hey Rohan, quick question on your base case when array size is 1."*
4. **Async Reply**: Rohan logs in 2 hours later, sees a message badge in his topbar, opens the thread, and leaves a helpful reply.
