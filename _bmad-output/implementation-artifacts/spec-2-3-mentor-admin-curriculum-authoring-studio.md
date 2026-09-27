# Story Spec 2-3: Mentor & Admin Curriculum Authoring Studio

## Status: done
**Epic**: Epic 2 - Curriculum Engine, Modules, Lessons & Progression Services
**Service**: `services/curriculum-service` (Spring Boot 3.3) & `frontend/src/` (Next.js 14)

---

## 1. Overview & Business Intent
Implement the **Mentor & Admin Curriculum Authoring Studio** enabling authorized mentors (`ROLE_MENTOR`) and admins (`ROLE_ADMIN`) to author, sequence, and publish curriculum tracks, modules, lessons, bilingual story analogies, starter code, and test case suites.

---

## 2. Agreed Architectural Specifications

### 2.1 Dual-Layer RBAC Gatekeeping
* `gateway-service` rejects `ROLE_STUDENT` calls to `/api/v1/admin/curriculum/**` with `HTTP 403 Forbidden`.
* `AdminCurriculumController` in `curriculum-service` provides authoring REST endpoints under `/api/v1/admin/curriculum/**`.

### 2.2 Authoring Studio UI Architecture
* **Presentation View**: `frontend/src/presentation/views/CurriculumStudioView.tsx`
  - Form UI for creating/updating Tracks, Modules, and Lessons.
  - Tabbed bilingual story editor (`ENGLISH` | `HINGLISH`).
  - Interactive Test Case Manager (Input, Expected Output, Is Hidden toggle).
* **Route Shell**: `frontend/src/app/curriculum/studio/page.tsx`
  - Admin/Mentor shell rendering `CurriculumStudioView`.

---

## 3. Acceptance Criteria (Definition of Done)

1. **RBAC Access Control**:
   - `ROLE_STUDENT` attempting `POST /api/v1/admin/curriculum/**` is rejected with `HTTP 403 Forbidden`.
   - Mentors and Admins can create and edit tracks, modules, and lessons.
2. **Track, Module & Lesson Persistence**:
   - `POST /api/v1/admin/curriculum/tracks`, `/modules`, `/lessons` persist documents in MongoDB.
3. **Bilingual Story & Test Case Authoring**:
   - Supports authoring story content for both `ENGLISH` and `HINGLISH` modes alongside visible and hidden test cases.
4. **Publishing & Instant Availability**:
   - Lessons saved with `PUBLISHED` status immediately become queryable for students.
5. **Strict TypeScript & SOLID**:
   - 100% strict TypeScript types and DTO parity. Zero `any`.

---

## 4. Implementation Tasks

- [ ] Create `CurriculumStudioView.tsx` in `frontend/src/presentation/views/`
- [ ] Connect `AdminCurriculumClient` in frontend for authoring endpoints
- [ ] Update `/curriculum/studio/page.tsx` route shell
- [ ] Verify `curriculum-service` and `gateway-service` integration tests
- [ ] Verify frontend production build (`npm run build`)
