# Retrospective — Epic 2: Curriculum Engine, Reader & Authoring Studio

**Date:** 2026-09-27  
**Scope:** Epic 2 (`2-1-curriculum-tracks-modules-lessons-progression-data-model`, `2-2-distraction-free-text-story-reader-component`, `2-3-mentor-admin-curriculum-authoring-studio`)  
**Status:** `done` (3/3 stories completed)  
**Acceptance Verdict:** **`accepted`**

---

## 1. Epic Summary & Inventory

Epic 2 established the core curriculum domain, student learning progression, bilingual narrative reader, and mentor/admin authoring studio for CodeConnect.

| Story ID | Title | Status | Scope / Deliverables |
| :--- | :--- | :---: | :--- |
| **`2-1`** | Curriculum Tracks, Modules, Lessons & Progression Data Model | `done` | MongoDB schemas (`TrackDocument`, `ModuleDocument`, `LessonDocument`, `StudentProgressDocument`), Java 21 DTO records, REST APIs, TypeScript types. |
| **`2-2`** | Distraction-Free Text Story Reader Component | `done` | `StoryReaderView.tsx` with bilingual narrative toggle (`ENGLISH` \| `HINGLISH`), code snippet copy controls, non-blocking prerequisite recommendation banner, zero media clutter. |
| **`2-3`** | Mentor & Admin Curriculum Authoring Studio | `done` | `CurriculumStudioView.tsx`, `/curriculum/studio/page.tsx`, dual RBAC gatekeeping (`ROLE_ADMIN` & `ROLE_MENTOR`), test case manager, and solution template security. |

### Evidence Artifacts
- **Specs**: `spec-2-1.md`, `spec-2-2.md`, `spec-2-3.md`
- **Microservices**: `services/curriculum-service` (Spring Boot 3.3 / Java 21), `services/gateway-service`
- **Frontend Views**: `StoryReaderView.tsx`, `CurriculumStudioView.tsx`, `CurriculumReaderView.tsx`
- **Infrastructure**: Kubernetes manifests (`k8s/deployments/curriculum-service-deployment.yaml`, `services`, `configmaps`, `autoscaling`), `Jenkinsfile`

---

## 2. Key Achievements & Architectural Highlights

1. **Clean Architecture & SOLID Compliance**:
   - Strict segregation between Presentation (`views/`), Controller Logic (`controller/`), Domain Services (`service/`), and Data Mappers (`mapper/`).
   - Controllers act purely as thin delegates (`@Valid`), with 100% constructor injection (`private final`).

2. **Data Security & Solution Sanitization**:
   - `LessonResponse` solution template is sanitized (`null`) over public student APIs (`GET /api/v1/curriculum/lessons/{id}`) via `@JsonInclude(NON_NULL)`.
   - Dedicated admin authoring endpoint (`GET /api/v1/admin/curriculum/lessons/{id}`) exposes solution templates exclusively to authorized mentors/admins.

3. **Bilingual Concept Storytelling**:
   - Dual `ENGLISH` and `HINGLISH` story analogy map support with robust fallback chain to prevent blank narrative rendering.
   - Non-blocking prerequisite recommendations ensure students are never HTTP-forbidden from reading or attempting lessons.

4. **CI/CD & Cloud Native Infrastructure**:
   - Full Kubernetes manifest set (`deployments`, `services`, `configmaps`, `hpa`) aligned with cluster naming standards.
   - 1:1 Jenkinsfile pipeline integration (`services/curriculum-service/Jenkinsfile`) matching `user-service` and `gateway-service` build steps.

---

## 3. Code Review & Quality Audit

An adversarial code review (`bmad-code-review`) was conducted across 4 review lenses (**Blind Hunter**, **Edge Case Hunter**, **Verification Gap**, **Intent Alignment**):

- **Gateway RBAC for Mentors**: Updated `RbacAccessDecisionManager` to permit `ROLE_MENTOR` on `/api/v1/admin/curriculum/**` routes.
- **Admin Studio Lesson Retrieval**: Added `GET /api/v1/admin/curriculum/lessons/{id}` endpoint to allow mentors to edit existing solution code.
- **Language Mode Fallback**: Implemented safe fallback in `StoryReaderView.tsx` for missing language keys.
- **Integration Test Verification**: Added `shouldAllowMentorAccessToAdminCurriculumRoutes()` to `GatewayRbacIntegrationTest.java`.

### Test Results
- `gateway-service`: **6/6 tests passing**
- `curriculum-service`: **10/10 tests passing**
- `frontend`: **15/15 Next.js pages compiled successfully** (`npm run build`)

---

## 4. Action Items & Lessons Learned

| # | Action Item | Owner | Target |
| :- | :--- | :--- | :--- |
| **1** | Clean up `target/` build files from git index across microservices | Amelia (Dev) | Tech Debt |
| **2** | Configure `MongoTransactionManager` bean when multi-document transactions are enabled on production MongoDB replica sets | Winston (Arch) | Epic 7 Hardening |
| **3** | Proceed to Epic 3 Story 3-1 (`3-1-50-50-coding-cockpit-layout-with-monaco-editor-expand-mode`) | Amelia (Dev) | Epic 3 |

---

## 5. Acceptance Decision

**Final Verdict:** **`accepted`**

*Rationale:* All 3 stories in Epic 2 satisfy their acceptance criteria, pass Clean Architecture rules, compile with zero errors, pass unit/integration tests, and leave zero blocking defects.
