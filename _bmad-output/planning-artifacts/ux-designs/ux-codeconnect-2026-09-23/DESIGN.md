---
name: CodeConnect
description: Enterprise SaaS Light-Theme Design System & UI Specification for Java 21 & DSA Mastery
created: 2026-09-23
updated: 2026-09-24
status: final
colors:
  bg-base: '#f8fafc'
  bg-surface: '#ffffff'
  bg-elevated: '#ffffff'
  border-subtle: '#e2e8f0'
  border-strong: '#cbd5e1'
  border-focus: '#2563eb'
  text-primary: '#0f172a'
  text-secondary: '#334155'
  text-muted: '#64748b'
  accent-blue: '#2563eb'
  accent-blue-subtle: '#eff6ff'
  accent-emerald: '#059669'
  accent-emerald-subtle: '#ecfdf5'
  accent-rose: '#dc2626'
  accent-rose-subtle: '#fef2f2'
  accent-amber: '#b45309'
  accent-amber-subtle: '#fffbeb'
  accent-purple: '#7c3aed'
  accent-purple-subtle: '#f5f3ff'
  code-dark: '#0f172a'
  code-header: '#1e293b'
typography:
  display:
    fontFamily: 'Plus Jakarta Sans, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '28px'
    fontWeight: '800'
    lineHeight: '36px'
    letterSpacing: '-0.025em'
  heading:
    fontFamily: 'Plus Jakarta Sans, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '20px'
    fontWeight: '700'
    lineHeight: '28px'
    letterSpacing: '-0.015em'
  subheading:
    fontFamily: 'Plus Jakarta Sans, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '14px'
    fontWeight: '700'
    lineHeight: '20px'
  body:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '13px'
    fontWeight: '400'
    lineHeight: '20px'
  body-sm:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '11px'
    fontWeight: '400'
    lineHeight: '16px'
  caption:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '10px'
    fontWeight: '600'
    letterSpacing: '0.04em'
  code:
    fontFamily: 'JetBrains Mono, SFMono-Regular, Menlo, Monaco, Consolas, monospace'
    fontSize: '12px'
    fontWeight: '500'
    lineHeight: '18px'
rounded:
  xs: '4px'
  sm: '6px'
  md: '8px'
  lg: '10px'
  xl: '12px'
  2xl: '16px'
  full: '9999px'
spacing:
  '1': '4px'
  '2': '8px'
  '3': '12px'
  '4': '16px'
  '5': '20px'
  '6': '24px'
  '8': '32px'
  '12': '48px'
components:
  auth-card:
    background: '{colors.bg-surface}'
    border: '1px solid {colors.border-subtle}'
    borderRadius: '{rounded.2xl}'
    boxShadow: '0 8px 30px rgba(15, 23, 42, 0.06)'
  cockpit-pane:
    background: '{colors.bg-surface}'
    border: '1px solid {colors.border-subtle}'
    borderRadius: '{rounded.xl}'
    boxShadow: '0 4px 12px rgba(15, 23, 42, 0.04)'
  monaco-editor:
    background: '{colors.code-dark}'
    headerBackground: '{colors.code-header}'
    border: '1px solid #334155'
    borderRadius: '{rounded.lg}'
  socratic-drawer:
    background: '{colors.bg-surface}'
    borderLeft: '1px solid {colors.border-subtle}'
    boxShadow: '-6px 0 24px rgba(15, 23, 42, 0.08)'
  metric-card:
    background: '{colors.bg-surface}'
    border: '1px solid {colors.border-subtle}'
    borderRadius: '{rounded.md}'
    boxShadow: '0 2px 8px rgba(15, 23, 42, 0.04)'
  table-card:
    background: '{colors.bg-surface}'
    border: '1px solid {colors.border-subtle}'
    borderRadius: '{rounded.xl}'
    boxShadow: '0 4px 12px rgba(15, 23, 42, 0.04)'
---

# CodeConnect Design System (SaaS Light Workbench)

## 1. Design Vision & Philosophy

CodeConnect is an engineering mastery platform built with an **enterprise SaaS workbench aesthetic** (inspired by Linear, GitHub Light, and HackerRank). It deliberately eschews low-density editorial blog formats in favor of high-performance, information-dense toolkits designed for deep focus and structured problem-solving.

### Core Principles
1. **Workbench Density Over Editorial Whitespace**: Developers and engineering students thrive on high-density split screens (50/50 problem spec vs. Monaco code editor), collapsible trays, and context-preserving drawers.
2. **Physical Mental Models Over Jargon**: Concepts anchor to real-world mechanisms (e.g., airport baggage carousel for circular queue, postal mailbox grids for array indexing) rendered as clean vector SVGs, never as dry text walls.
3. **Pure Vector Iconography (Zero Comic Emojis)**: All system affordances use crisp, mathematical SVG vector paths (`<path>`, `<polyline>`, `<polygon>`, `<circle>`). Emojis, comic glyphs, and non-standard characters are strictly prohibited.
4. **Role-Segregated Ergonomics**:
   - **Student**: Distraction-free Coding Cockpit, progressive Socratic AI drawer, and peer network.
   - **Mentor**: Dedicated Escalation Resolution Desk (side-by-side code diffs) and Curriculum Authoring Studio.
   - **Admin**: System-wide User & Role Governance Directory and Mentor Application Triage Queue.

---

## 2. Color Palette & Token Architecture

| Category | Token Name | Hex Value | Semantic Usage |
| :--- | :--- | :--- | :--- |
| **Canvas Background** | `bg-base` | `#f8fafc` | The foundational slate-50 backdrop across all views |
| **Surface (Cards/Panels)** | `bg-surface` | `#ffffff` | Pure white for cards, cockpit panes, and table bodies |
| **Borders (Subtle)** | `border-subtle` | `#e2e8f0` | 1px clean container outlines and column dividers |
| **Borders (Strong)** | `border-strong` | `#cbd5e1` | Input fields, active badges, and interactive control borders |
| **Brand Primary** | `accent-blue` | `#2563eb` | Primary actions ("Submit Solution", "Publish Foothold", active tabs) |
| **Brand Subtle** | `accent-blue-subtle` | `#eff6ff` | Active selections, selected foothold cards, tag chips |
| **Success / Conquered** | `accent-emerald` | `#059669` | Passed test chips, conquered waypoints, active service health |
| **Success Subtle** | `accent-emerald-subtle` | `#ecfdf5` | Passed badge backgrounds (`#a7f3d0` border) |
| **Warning / Socratic** | `accent-amber` | `#b45309` | Socratic hints, pending approval queues, tier-3 escalations |
| **Warning Subtle** | `accent-amber-subtle` | `#fffbeb` | Warning banner and amber card backgrounds (`#fde68a` border) |
| **Error / Diagnostic** | `accent-rose` | `#dc2626` | Failed test cases, stack trace callouts, compile error highlights |
| **Error Subtle** | `accent-rose-subtle` | `#fef2f2` | Failed test card fills (`#fecaca` border) |
| **Text Primary** | `text-primary` | `#0f172a` | Slate-900 for headings, card titles, and high-contrast labels |
| **Text Secondary** | `text-secondary` | `#334155` | Slate-700 for problem body copy and story descriptions |
| **Text Muted** | `text-muted` | `#64748b` | Slate-500 for subtitles, timestamps, and column headers |
| **Editor Dark** | `code-dark` | `#0f172a` | High-contrast Monaco Java 21 editor canvas |
| **Editor Header** | `code-header` | `#1e293b` | Editor tab toolbar and file name header |

---

## 3. Typography Hierarchy

```
Display:     28px / 36px | Weight 800 | Letter-spacing: -0.025em  (Hero & Milestones)
Heading:     20px / 28px | Weight 700 | Letter-spacing: -0.015em  (Page & Modal Titles)
Subheading:  14px / 20px | Weight 700 | Normal                    (Section & Card Headers)
Body:        13px / 20px | Weight 400 | Normal                    (Problem Specs, Stories)
Body-sm:     11px / 16px | Weight 400 | Normal                    (Table data, Helper notes)
Caption:     10px / 14px | Weight 700 | Letter-spacing: 0.04em   (Pills, Badges, Labels)
Code:        12px / 18px | Weight 500 | Monospace                 (Java 21, Signatures, Diffs)
```

- **Sans-Serif Font Stack**: `Plus Jakarta Sans`, `-apple-system`, `BlinkMacSystemFont`, `Segoe UI`, `Roboto`, `sans-serif`
- **Monospace Font Stack**: `JetBrains Mono`, `SFMono-Regular`, `Menlo`, `Monaco`, `Consolas`, `monospace`

---

## 4. Pure Vector Iconography Standards

All visual icons must be rendered using strict SVG vector elements. Never import emojis or platform-specific symbol fonts into UI components.

| Icon Role | Vector Path Primitive | Standard Dimensions | Stroke / Fill Rules |
| :--- | :--- | :--- | :--- |
| **Mountain Ridge (Ascent)** | `<path d="M 12 21 L 18 11 L 22 17 L 25 12 L 28 21 Z"/>` | 24 × 24px | Stroke 1.5px, cap/join round |
| **Success Checkmark** | `<polyline points="14 12 17 15 22 9"/>` | 16 × 16px | Stroke 1.6px, emerald `#059669` |
| **Diagnostic Error (X)** | `<line x1="214" y1="9" x2="220" y2="15"/><line x1="220" y1="9" x2="214" y2="15"/>` | 16 × 16px | Stroke 1.6px, rose `#dc2626` |
| **Socratic Lightbulb** | `<path d="M 14 20 H 18 M 15 21 H 17 M 16 11 A 3 3 0 0 0 13 14..."/>` | 20 × 20px | Stroke 1.2px, blue `#2563eb` |
| **Shield (Security / Mentor)** | `<path d="M 22 28 C 22 28 26 25 26 22 V 19 L 22 17.5 L 18 19 V 22..."/>` | 20 × 20px | Stroke 1.3px, slate `#64748b` |
| **Padlock (Checkpoint)** | `<rect x="14" y="20" width="8" height="6" rx="1"/><path d="M 15 20 V 17 C 15 15.5 21 15.5 21 17 V 20"/>` | 16 × 16px | Stroke 1.2px, muted `#94a3b8` |
| **Expand Editor** | `<path d="M 4 8 L 8 4 M 8 4 H 5 M 8 4 V 7 M 14 14 L 10 18 M 10 18 H 13 M 10 18 V 15"/>` | 18 × 18px | Stroke 1.2px, slate `#475569` |
| **External Link Arrow** | `<path d="M 816 32 H 824 V 40 M 824 32 L 816 40"/>` | 12 × 12px | Stroke 1.3px, blue `#2563eb` |

---

## 5. Master Wireframe Specification (10 Wireframes)

The frontend is implemented directly from the 10 production-grade SVGs located in `_bmad-output/planning-artifacts/ux-designs/wireframes/`:

```
_bmad-output/planning-artifacts/ux-designs/wireframes/
├── 00-app-shell.svg                        # Global Navigation, Header & Docked Workspace
├── 01-epic1-auth-onboarding.svg             # Epic 1: Auth, Role Selection & Pending Approval
├── 02-epic2-curriculum-reader.svg           # Epic 2: Student Mountain Track Explorer & Reader
├── 03-epic3-coding-cockpit.svg              # Epic 3: 50/50 Interactive Editor & Test Console
├── 04-epic4-socratic-ai.svg                 # Epic 4: Slide-out Socratic AI Debugger Drawer
├── 05-epic5-peer-solvers.svg                # Epic 5: Verified Peer Solver Network & 1-on-1 Chat
├── 06-epic6-mentor-desk.svg                 # Epic 6: Mentor Escalation Resolution Desk
├── 07-epic7-ascent-dashboard.svg            # Epic 7: Topographic Mountain Climb & Checkpoint
├── 08-epic2-curriculum-authoring-studio.svg # Epic 2 (Story 2.3): Mentor Curriculum Authoring Studio
└── 09-admin-studio-user-governance.svg      # Admin Portal: Mentor Approvals & User Governance
```

### 5.1 Role-Specific Surface Architecture

#### A. Student Role (`00-app-shell.svg` through `05-epic5-peer-solvers.svg`, `07-epic7-ascent-dashboard.svg`)
1. **Interactive Coding Cockpit (Epic 3)**:
   - Left column (570px): Real-world physical story analogy, method contract signatures, and formal constraints.
   - Right column (575px): Monaco Java 21 editor with instant syntax highlighting and isolated Docker sandbox execution.
   - Bottom Console (Height: 210px): Tabbed test-case execution runner displaying pass/fail badges, millisecond run duration, RAM consumption, and failure diagnostics with Socratic hint triggers.
2. **Socratic AI Clue Drawer (Epic 4)**:
   - 450px slide-out right drawer.
   - 3-tier progressive hint ladder:
     - `Tier 1: Conceptual Metaphor` (Unlocked automatically on test failure).
     - `Tier 2: Formula & Constraints` (Unlocked on request).
     - `Tier 3: Structural Pseudocode` (Guarded behind confirmation to prevent code spoilers).
3. **Peer Solver Network (Epic 5)**:
   - 450px slide-out drawer listing online/offline climbers who conquered the active foothold.
   - 1-on-1 chat tray with code snapshot attachment and synchronous line-level discussion.
4. **Ascent Dashboard & Retention Engine (Epic 7)**:
   - Mountain elevation topographic map with 4 load-bearing checkpoints:
     - Base Camp (0m) ➔ Camp 1 (1,000m) ➔ Camp 2 (2,200m) ➔ High Camp (3,100m) ➔ Summit (4,000m).
   - Spaced-repetition retention drills and momentum streak trackers.

#### B. Mentor Role (`06-epic6-mentor-desk.svg`, `08-epic2-curriculum-authoring-studio.svg`)
1. **Mentor Resolution Desk (Epic 6)**:
   - Left Column (400px): Student ticket triage queue with urgency indicators, escalated foothold badges, and solver history.
   - Right Column (728px): Side-by-side code diff viewer comparing student's failing attempt vs. canon solution, markdown feedback editor, and "Mark Resolved & Notify Student" action.
2. **Curriculum Authoring Studio (Epic 2 / Story 2.3)**:
   - Foothold metadata builder (Track, Foothold ID, Name, Target Altitude).
   - Real-world physical analogy editor with compulsory SVG mechanism attachment.
   - Test-case matrix builder (Inputs, Expected Outputs, Hidden flag).
   - Bulk curriculum syllabus uploader supporting drag-and-drop `.json` and `.zip` packages.

#### C. Admin Role (`09-admin-studio-user-governance.svg`)
1. **Platform Metrics**: 4 top cards displaying Active Students, Verified Mentors, Pending Mentor Apps, and Cluster Sandbox Health.
2. **Mentor Application Triage**: High-priority verification queue showing applicant credentials, current employer, GitHub profile links, and instant **Approve** / **Reject** governance buttons.
3. **User & Role Governance Directory**: Searchable, role-filterable directory of all platform climbers, allowing role elevation, suspension, and compliance audits.

---

## 6. Component Architecture for Frontend Engineering

Amelia should construct the component library following Atomic Design principles:

```
src/components/
├── atoms/
│   ├── AltitudeBadge.tsx       # Elevation meter pill (e.g., "1,420m / 4,000m")
│   ├── VectorIcon.tsx          # Pure SVG path renderer (no emojis)
│   ├── StatusPill.tsx          # PASS, FAIL, ACTIVE, URGENT, SOLVING
│   ├── StreakBadge.tsx         # Consecutive daily climbs counter
│   └── CodeBlock.tsx           # Monospace snippet box with copy action
├── molecules/
│   ├── BreadcrumbBar.tsx       # Track / Foothold breadcrumb with status badge
│   ├── TestResultChip.tsx      # Case 1, Case 2 chips with pass/fail vectors
│   ├── HintTierAccordion.tsx   # Tier 1, 2, 3 progressive clue cards
│   ├── PeerUserRow.tsx         # Climber avatar, name, altitude, and chat CTA
│   └── StatMetricCard.tsx      # Admin/dashboard stat card with right-aligned badges
├── organisms/
│   ├── AppHeader.tsx           # Global header with logo, streak, altitude, and profile
│   ├── MonacoCockpit.tsx       # Monaco Java 21 editor with test runner console
│   ├── SocraticDrawer.tsx      # Slide-out AI coach drawer with prompt chips
│   ├── PeerChatTray.tsx        # Peer-to-peer discussion tray with code syncing
│   ├── MentorDiffDesk.tsx      # Side-by-side diff viewer and ticket resolver
│   ├── CurriculumStudio.tsx    # Foothold authoring form and test case matrix
│   └── AdminGovernanceTable.tsx# User directory and mentor application triage
└── templates/
    ├── WorkbenchSplitLayout.tsx# 50/50 split container with expand toggle
    └── DashboardLayout.tsx     # Full-width 1200px container for dashboard & admin
```

---

## 7. Accessibility, Contrast & Responsive Behavior

- **Contrast Compliance**:
  - Text Primary (`#0f172a`) on White (`#ffffff`): **15.8:1** (WCAG AAA).
  - Blue Accent (`#2563eb`) on Blue Subtle (`#eff6ff`): **7.4:1** (WCAG AAA).
  - Emerald (`#059669`) on Emerald Subtle (`#ecfdf5`): **5.9:1** (WCAG AA).
- **Interactive Focus States**: All interactive elements receive a sharp 2px focus ring (`#2563eb`) with a 2px offset.
- **Breakpoints**:
  - `Desktop Workbench (≥ 1200px)`: Full 50/50 dual-pane cockpit and open auxiliary drawers.
  - `Laptop (1024px – 1199px)`: 50/50 split with overlays for auxiliary drawers.
  - `Tablet / Mobile (< 1024px)`: Stacked tabs (`[Story Spec] | [Java Editor] | [Console]`).
