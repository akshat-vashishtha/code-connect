---
title: 'Story 3.1: 50/50 Coding Cockpit Layout with Monaco Editor & Expand Mode'
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

**Problem:** Students need a professional, distraction-free environment to write Java code alongside problem statements, with syntax highlighting, indentation, and local persistence so work is never lost.

**Approach:** Implement a 50/50 split Coding Cockpit in Next.js 14 integrating `@monaco-editor/react`, with active foothold starter code pre-population, 1-click full-width expand mode, and debounced LocalStorage draft caching.

## Boundaries & Constraints

**Always:**
- Keep structural logic (`controller/`, `validator/`, `service/`) strictly decoupled from UI JSX (`presentation/`, `components/`).
- Use `@monaco-editor/react` with SSR disabled / dynamic import to prevent Next.js SSR hydration mismatches.
- Cache unsaved student code in `localStorage` under `codeconnect_draft_{footholdId}` with debouncing so page refreshes preserve code.
- Provide smooth 1-click toggle between 50/50 split mode and 100% full-width expanded editor mode while keeping the story reader tab-accessible.

**Never:**
- Do not execute raw HTTP `fetch` or WebSocket logic inside UI presentation components.
- Do not lose student typed code when toggling between 50/50 split and expanded mode.
- Do not hardcode student IDs, API URLs, or route patterns in UI components.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Load Unlocked Foothold | Foothold loaded, no local draft | Monaco editor pre-populates with `starterCodeJava` from foothold document | Fallback to default Java class template if starter code missing |
| Restore Local Draft | Foothold loaded, local draft exists in `localStorage` | Monaco editor restores student's previously edited draft code | If corrupted JSON/storage, fallback to starter code gracefully |
| Expand Mode Toggle | User clicks Expand button | Editor expands to full width (100%), story is minimized to a drawer/tab | Maintains cursor position and editor state seamlessly |
| Code Editing Autosave | Student types code in editor | Debounced write (500ms) to `localStorage` | Catches `QuotaExceededError` silently without breaking editor |
| Reset Starter Code | Student clicks "Reset to Starter" | Confirms action and replaces editor content with initial `starterCodeJava` | Clears local draft cache for this foothold |

</frozen-after-approval>

## Code Map

- `frontend/package.json` -- Add `@monaco-editor/react` dependency.
- `frontend/src/controller/useCockpitController.ts` -- Custom React controller hook managing editor state, local draft persistence, expand toggle, and starter code.
- `frontend/src/presentation/views/CodingCockpitView.tsx` -- Presentational assembly rendering 50/50 split desktop layout or full-width expanded mode.
- `frontend/src/presentation/organisms/CockpitEditorPane.tsx` -- Monaco editor wrapper with theme switching, font size controls, and expand toggle button.
- `frontend/src/presentation/organisms/CockpitStoryPane.tsx` -- Distraction-free problem statement and test case summary viewer.
- `frontend/src/app/(dashboard)/cockpit/page.tsx` -- Thin Next.js page shell.

## Tasks & Acceptance

**Execution:**
- [ ] `frontend/package.json` -- Install `@monaco-editor/react` -- Required for Monaco Java code editor in React.
- [ ] `frontend/src/controller/useCockpitController.ts` -- Implement controller hook for editor state, draft caching, and mode toggles -- Enforces clean architecture controller hook pattern.
- [ ] `frontend/src/presentation/organisms/CockpitEditorPane.tsx` -- Build Monaco editor organism with toolbar controls -- Modular presentational UI for code editing.
- [ ] `frontend/src/presentation/organisms/CockpitStoryPane.tsx` -- Build story & problem statement pane -- Clean 50% split companion pane.
- [ ] `frontend/src/presentation/views/CodingCockpitView.tsx` -- Assemble 50/50 split layout and full-width expand mode -- Connects presentation to controller.
- [ ] `frontend/src/app/(dashboard)/cockpit/page.tsx` -- Mount CodingCockpitView -- Route shell.

**Acceptance Criteria:**
- Given an active foothold, when the Coding Cockpit loads, then the screen renders a 50/50 split layout with Story on the left and Monaco Editor on the right.
- Given editor input, when the student types code, then changes are cached to LocalStorage and preserved across page refreshes.
- Given the expand toggle is clicked, when activated, then Monaco expands to 100% width without losing cursor position or code.

## Implementation Notes

## Spec Change Log

## Review Triage Log

## Verification

**Commands:**
- `npm --prefix frontend run build` -- expected: Next.js frontend builds with zero TypeScript or lint errors.
- `npm --prefix frontend run lint` -- expected: Clean ESLint check with zero warnings or errors.
