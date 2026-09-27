# Story Spec 2-2: Distraction-Free Text Story Reader Component

## Status: done
**Epic**: Epic 2 - Curriculum Engine, Modules, Lessons & Progression Services
**Service**: `frontend/src/` (Next.js 14 / TypeScript) & `services/curriculum-service`

---

## 1. Overview & Business Intent
Build the student-facing **Distraction-Free Text Story Reader Component** on the Next.js 14 frontend.
The Story Reader displays story-driven coding concepts using rich Markdown formatting, bilingual story mode switching (`ENGLISH` | `HINGLISH`), inline code blocks with copy-to-clipboard buttons, and lightweight SVG diagrams, without video/audio media distractions.

---

## 2. Agreed Architectural Specifications

### 2.1 Component Architecture & Segregation
* **Presentation View**: `frontend/src/presentation/views/StoryReaderView.tsx`
  - Renders Markdown story narrative, analogy sections, prerequisite badges, and code blocks.
  - Zero business logic or raw network fetches.
* **Controller Hook**: `frontend/src/controller/useCurriculumController.ts`
  - Provides active lesson state, language mode toggle (`ENGLISH` | `HINGLISH`), and prerequisite recommendation metadata.
* **Route Shell**: `frontend/src/app/cockpit/[footholdId]/page.tsx`
  - Assembles `StoryReaderView` into the Cockpit layout.

### 2.2 Distraction-Free & Pure Media Guarantee
* Strictly **zero** `<video>`, `<iframe>`, or `<audio>` elements inside the Story Reader DOM.
* Clean visual focus: rich typography, structured headings, copyable code blocks, and SVG diagram containers.

### 2.3 Bilingual Story Analogy Toggle
* Supports switching between `ENGLISH` and `HINGLISH` versions from `LessonResponse.storyAnalogies`.
* Seamless language mode selector badge/toggle integrated in header/view.

---

## 3. Acceptance Criteria (Definition of Done)

1. **Story Fetch & Rendering**:
   - Given an active lesson ID, `StoryReaderView` renders the Markdown story narrative, title, module context, and starter code snippet.
2. **Copyable Code Snippets**:
   - Inline and block code snippets feature an interactive "Copy Code" button with visual feedback ("Copied!").
3. **Bilingual Narrative Switching**:
   - Toggling language mode (`ENGLISH` <-> `HINGLISH`) updates the displayed story narrative instantly.
4. **Soft Prerequisite Badge**:
   - Non-blocking recommendation badge ("Prerequisite [Title] Recommended") displays when an uncompleted prerequisite exists.
5. **Zero Clutter Rule**:
   - The Story Reader component renders zero `<video>`, `<iframe>`, or `<audio>` media elements.
6. **Strict TypeScript & SOLID**:
   - 100% strict TypeScript types matching `LessonResponse`, `StoryContent`, and `LanguageMode`. Zero `any`.

---

## 4. Implementation Tasks

- [ ] Create/update `StoryReaderView.tsx` in `frontend/src/presentation/views/`
- [ ] Implement Markdown renderer with code copy button and SVG diagram support
- [ ] Implement language mode toggle (`ENGLISH` | `HINGLISH`)
- [ ] Integrate non-blocking prerequisite recommendation banner
- [ ] Update Cockpit route `/cockpit/[footholdId]` to render `StoryReaderView`
- [ ] Verify frontend build (`npm run build`)
