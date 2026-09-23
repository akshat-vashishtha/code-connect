---
name: CodeConnect
description: A clean, professional light-theme design system for structured Java & DSA learning.
created: 2026-09-23
updated: 2026-09-23
status: final
colors:
  bg-base: '#f8fafc'
  bg-surface: '#ffffff'
  bg-elevated: '#ffffff'
  border-subtle: '#e2e8f0'
  border-focus: '#0284c7'
  text-primary: '#0f172a'
  text-secondary: '#475569'
  text-muted: '#94a3b8'
  accent-cyan: '#0284c7'
  accent-emerald: '#059669'
  accent-rose: '#e11d48'
  accent-amber: '#d97706'
  accent-indigo: '#4f46e5'
typography:
  display:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '28px'
    fontWeight: '700'
    lineHeight: '36px'
    letterSpacing: '-0.02em'
  heading:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '20px'
    fontWeight: '600'
    lineHeight: '28px'
    letterSpacing: '-0.01em'
  subheading:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '16px'
    fontWeight: '600'
    lineHeight: '24px'
  body:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '15px'
    fontWeight: '400'
    lineHeight: '24px'
  body-sm:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    fontSize: '13px'
    fontWeight: '400'
    lineHeight: '20px'
  code:
    fontFamily: 'ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "JetBrains Mono", monospace'
    fontSize: '14px'
    fontWeight: '400'
    lineHeight: '22px'
rounded:
  sm: '4px'
  md: '8px'
  lg: '12px'
  xl: '16px'
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
    borderRadius: '{rounded.xl}'
    boxShadow: '0 10px 25px -5px rgba(15, 23, 42, 0.05), 0 8px 10px -6px rgba(15, 23, 42, 0.03)'
  cockpit-pane:
    background: '{colors.bg-surface}'
    border: '1px solid {colors.border-subtle}'
    borderRadius: '{rounded.lg}'
  btn-primary:
    background: '{colors.accent-cyan}'
    color: '#ffffff'
    fontWeight: '600'
    borderRadius: '{rounded.md}'
  btn-run:
    background: '#ffffff'
    color: '{colors.accent-cyan}'
    border: '1px solid {colors.accent-cyan}'
    borderRadius: '{rounded.md}'
  btn-submit:
    background: '{colors.accent-emerald}'
    color: '#ffffff'
    fontWeight: '600'
    borderRadius: '{rounded.md}'
  badge-online:
    background: '#ecfdf5'
    color: '{colors.accent-emerald}'
    border: '1px solid #a7f3d0'
    borderRadius: '{rounded.full}'
  badge-offline:
    background: '#f1f5f9'
    color: '{colors.text-muted}'
    border: '1px solid #cbd5e1'
    borderRadius: '{rounded.full}'
  chat-drawer:
    background: '{colors.bg-surface}'
    borderLeft: '1px solid {colors.border-subtle}'
    boxShadow: '-8px 0 24px rgba(15, 23, 42, 0.08)'
  table-admin:
    background: '{colors.bg-surface}'
    border: '1px solid {colors.border-subtle}'
    borderRadius: '{rounded.lg}'
---

# Design System: CodeConnect (Professional Light Theme)

## 1. Brand & Style
CodeConnect adopts a **crisp, clean, professional light theme** engineered for intellectual clarity, focus, and trust. 

Drawing from modern engineering tools (Stripe Dashboard, GitHub Light, Linear, and HackerRank), the interface provides generous whitespace, razor-sharp typography, and restrained borders that create a structured, academic yet approachable environment.

Key aesthetic principles:
* **Paper-White Clarity**: Soft off-white canvas (`#f8fafc`) paired with pure white cards (`#ffffff`) prevents harsh glare while delivering maximum readability.
* **Purposeful Color Anchors**: Vivid sky blue (`#0284c7`) drives action and navigation; forest emerald (`#059669`) celebrates code passing; warm amber (`#d97706`) guides Socratic insights; deep indigo (`#4f46e5`) denotes mentor authority.
* **Subtle Elevation**: Light, diffuse shadows and fine 1px borders replace heavy dark gradients, creating a refined SaaS posture suitable for students, universities, and enterprise mentors alike.

---

## 2. Colors
* `{colors.bg-base}` (`#f8fafc`): The foundational canvas backdrop.
* `{colors.bg-surface}` (`#ffffff`): Pure white surface for content cards, the 50/50 cockpit panels, and dashboard modules.
* `{colors.bg-elevated}` (`#ffffff`): Elevated surfaces (drawers, modals, dropdowns) paired with soft ambient shadows.
* `{colors.border-subtle}` (`#e2e8f0`): Light neutral divider lines between cockpit panes, table rows, and navigation headers.
* `{colors.border-focus}` (`#0284c7`): Vibrant blue focus ring on active inputs and code editor outlines.
* `{colors.text-primary}` (`#0f172a`): High-contrast slate-black for headings, code text, and body copy.
* `{colors.text-secondary}` (`#475569`): Muted charcoal for descriptions, story narrative text, and helper labels.
* `{colors.text-muted}` (`#94a3b8`): Timestamps, inactive tabs, and line numbers.
* `{colors.accent-cyan}` (`#0284c7`): Primary brand color for buttons, active navigation links, and "Run Code".
* `{colors.accent-emerald}` (`#059669`): Test pass badge, "Submit" button, and Online presence dot.
* `{colors.accent-rose}` (`#e11d48`): Test failure indicators and compiler error markers.
* `{colors.accent-amber}` (`#d97706`): Socratic AI coach prompts and pending approval status badges.
* `{colors.accent-indigo}` (`#4f46e5`): Mentor badges, admin tags, and curriculum studio tools.

---

## 3. Typography
* **Primary Sans Stack** (`{typography.display}`, `{typography.heading}`, `{typography.body}`): Clean, modern geometric sans (Inter / System UI).
  * Story text uses `{typography.body}` (15px / 24px line-height) set against a max-width of 680px for reading comfort.
* **Monospace Stack** (`{typography.code}`): High-clarity monospace (JetBrains Mono / SF Mono) with light background syntax highlighting for code snippets, input/output tests, and the in-browser editor.

---

## 4. Layout & Spacing
* **Top Navigation Bar**: 60px fixed header with 1px bottom border (`{colors.border-subtle}`), sticky across all views.
* **The 50/50 Cockpit**: Two independent 50% scrollable columns on desktop (≥ 1024px), with a one-click toggle to expand the editor to 100%.
* **Standard Grid Margins**: Max container width of 1200px for Dashboard and Admin views; full-width edge-to-edge for the Coding Cockpit.
* **Consistent Padding**: `{spacing.4}` (16px) for card interiors, `{spacing.6}` (24px) for page headers, and `{spacing.2}` (8px) for control gaps.

---

## 5. Elevation & Shadows
* **Level 0 (Flat)**: `{colors.bg-surface}` with `{colors.border-subtle}` border.
* **Level 1 (Card)**: `box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px -1px rgba(0, 0, 0, 0.05)`.
* **Level 2 (Drawer / Modal)**: `box-shadow: -4px 0 24px -2px rgba(15, 23, 42, 0.08)`.

---

## 6. Shapes & Radii
* **Buttons & Inputs**: `{rounded.md}` (8px).
* **Cards & Panels**: `{rounded.lg}` (12px) to `{rounded.xl}` (16px).
* **Badges & Pills**: `{rounded.full}` (9999px).

---

## 7. Component Visual Specifications Across All Surfaces

### 7.1 Login & Signup Surface
* Centered auth card (440px width) on `{colors.bg-base}`.
* Role Selector Tabs: Segmented pill control at the top: `Student (Instant Access)` | `Mentor (Requires Approval)`.
* Google OAuth Button: Clean white button with official Google "G" icon and `{colors.border-subtle}` border.
* Divider: Subtle "OR CONTINUE WITH EMAIL" line.
* Inputs: Clear labels, 40px height, light background with `{colors.border-subtle}`, focusing to `{colors.border-focus}`.

### 7.2 Student Dashboard (The Mountain Ascent)
* Header: Greeting, current streak counter (e.g. "🔥 5 Day Streak"), and quick resume CTA ("Continue Foothold 4: Two-Pointers →").
* Track Cards: Clean white cards showing progress bar (e.g., "7 / 15 Footholds Completed").
* Visual Ascent Path: Connected vertical roadmap showing:
  * Completed Footholds: Green checkmark icon with white badge.
  * Active Foothold: Highlighted blue card with "Start Coding" button.
  * Locked Footholds: Muted gray icon with padlock.

### 7.3 The Coding Cockpit (HackerRank Reference)
* Left Pane: Clean story reader with tinted blockquote for the real-world story, followed by the problem prompt and sample I/O boxes.
* Right Pane: Top toolbar with Java 17 badge + Reset button + Expand Editor toggle. Editor body uses a high-contrast light theme editor (e.g., GitHub Light style). Bottom test runner drawer with green/red test chips.

### 7.4 Dedicated Chat & Drawer Surface
* 3-Tier Header: Segmented tabs for `AI Guide`, `Peer Solvers`, and `Mentor`.
* Peer Directory: Student cards with user avatar, name, problem solved date, and presence badge:
  * **Online**: Solid green dot + "Online" badge (`{components.badge-online}`).
  * **Offline**: Gray dot + "Offline" badge (`{components.badge-offline}`).
* Thread View: Clean message bubbles (outbound light blue, inbound soft gray) with inline code snippet cards.

### 7.5 User Profile Surface
* Left Column: Avatar (80px), Display Name, Role Pill (`STUDENT`, `MENTOR`, or `ADMIN`), bio, and joined date.
* Right Column: Problem history table (Title, Track, Solved Date, View Solution button).

### 7.6 Admin Management Dashboard
* Metrics Bar: 4 top stat cards: Total Students, Approved Mentors, Pending Approvals, Total Solved Runs.
* Pending Mentor Approvals Section: Dedicated alert card showing pending applicant list with applicant name, email, credentials, and **Approve** / **Reject** action buttons.
* User Table: Filterable list with Role dropdown, Status badge, and Edit/Deactivate actions.

---

## 8. Do's and Don'ts

### Do
* **DO** keep backgrounds crisp, bright, and uncluttered. Whitespace is a feature.
* **DO** use clear green and gray badges so students immediately know if a peer solver is active right now.
* **DO** make the code editor easily expandable to full width with one click.

### Don't
* **DON'T** use jarring neon colors or dark heavy shadows that break the clean light aesthetic.
* **DON'T** hide the pending mentor approval count from the admin.
* **DON'T** place ads, banners, or irrelevant widgets in the student coding workspace.
