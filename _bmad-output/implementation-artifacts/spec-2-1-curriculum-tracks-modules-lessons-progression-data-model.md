# Story Spec 2-1: Curriculum Tracks, Modules, Lessons & Progression Data Model & APIs

## Status: in-progress
**Epic**: Epic 2 - Curriculum Engine, Modules, Lessons & Progression Services
**Service**: `services/curriculum-service` (Spring Boot 3.3 / Java 21) & `frontend/src/` (Next.js 14)

---

## 1. Overview & Business Intent
Implement the core domain data model, MongoDB repositories, Spring Boot 3.3 `curriculum-service` REST APIs, and Next.js 14 frontend components for **Curriculum Tracks, Modules, Lessons, and Student Ascent Progression**.

This establishes the primary learning structure:
`Track` -> `Module` -> `Lesson` -> `StudentProgress`

---

## 2. Agreed Architectural Specifications

### 2.1 Domain Vocabulary Migration
* **Track**: High-level learning pathway (e.g., *"Track 2: Data Structures & Algorithms"*).
* **Module**: Logical grouping of related concepts (e.g., *"Module 1: Array Fundamentals & Memory Layout"*).
* **Lesson**: Story-driven coding exercise with bilingual narrative, starter code, test cases, and Socratic hints (e.g., *"Lesson 3: Circular Queue Resolution"*).

### 2.2 Soft Prerequisite Policy (Non-Blocking)
* Uncompleted prerequisite lessons display a friendly recommendation badge (*"Prerequisite Lesson 2 Recommended"*).
* Students are **NOT BLOCKED** or HTTP-forbidden from opening or attempting any lesson.

### 2.3 Bilingual Story Analogies (EN | HINGLISH)
* `Lesson` documents store story analogies in both `en` and `hinglish`.
* Frontend `AppHeader.tsx` provides a seamless `EN | HINGLISH` language mode toggle.

---

## 3. MongoDB Schemas & Java Records

### 3.1 `TrackDocument` (`@Document(collection = "tracks")`)
```java
@Document(collection = "tracks")
public class TrackDocument {
    @Id
    private String id;
    private String title;
    private String slug;
    private String description;
    private Integer estimatedHours;
    private TrackStatus status; // PUBLISHED, DRAFT, ARCHIVED
    private List<ModuleSummary> modules;
    private Instant createdAt;
}
```

### 3.2 `ModuleDocument` (`@Document(collection = "modules")`)
```java
@Document(collection = "modules")
public class ModuleDocument {
    @Id
    private String id;
    private String trackId;
    private String title;
    private String slug;
    private Integer sequence;
    private String description;
    private String prerequisiteModuleId;
}
```

### 3.3 `LessonDocument` (`@Document(collection = "lessons")`)
```java
@Document(collection = "lessons")
public class LessonDocument {
    @Id
    private String id;
    private String moduleId;
    private String trackId;
    private String title;
    private Integer sequence;
    private StoryAnalogyMap storyAnalogy; // Map<LanguageMode, StoryContent>
    private String starterCode;
    private String solutionTemplate;
    private List<TestCase> testCases;
    private String prerequisiteLessonId;
}
```

### 3.4 `StudentProgressDocument` (`@Document(collection = "student_progress")`)
```java
@Document(collection = "student_progress")
public class StudentProgressDocument {
    @Id
    private String id; // prog-{userId}-{trackId}
    private String userId;
    private String trackId;
    private String currentModuleId;
    private String currentLessonId;
    private Set<String> completedLessonIds;
    private Integer ascentPoints;
    private Integer streakDays;
    private Instant lastCompletedAt;
}
```

---

## 4. REST API Endpoint Contracts (`curriculum-service` / Gateway)

### 4.1 Student APIs
* `GET /api/v1/curriculum/tracks` -> Returns all published tracks with active student progress summary.
* `GET /api/v1/curriculum/tracks/{trackId}/modules` -> Returns modules and lessons for a track.
* `GET /api/v1/curriculum/lessons/{lessonId}` -> Returns lesson story narrative, starter code template, and prerequisite recommendation status.

### 4.2 Admin / Mentor Authoring APIs
* `POST /api/v1/admin/curriculum/tracks` -> Create / update Track.
* `POST /api/v1/admin/curriculum/modules` -> Create Module under Track.
* `POST /api/v1/admin/curriculum/lessons` -> Create / update Lesson with story analogies and test cases.

---

## 5. Acceptance Criteria (Definition of Done)
1. **Curriculum Data Layer**: MongoDB collections (`tracks`, `modules`, `lessons`, `student_progress`) and Spring Data repositories created in `curriculum-service`.
2. **Business Services & DTO Records**: Java 21 `record` DTOs, mappers, validators, and `CurriculumService` / `CurriculumServiceImpl` implemented.
3. **REST Controllers & OpenAPI Specs**: `CurriculumController` and `AdminCurriculumController` exposed with standard `ApiResponse<T>` envelope.
4. **TypeScript Parity**: `frontend/src/dto/` and `frontend/src/types/` contracts updated with 1:1 parity for `TrackResponse`, `ModuleResponse`, `LessonResponse`, and `StudentProgressResponse`.
5. **Clean Architecture & SOLID**: Zero magic strings, full `enum` usage (`UserRole`, `LanguageMode`, `TrackStatus`), pure `@ConfigurationProperties`, thin controllers.

---

## Review Findings

- [x] [Review][Patch] Gateway missing student curriculum routes — Only `/api/v1/admin/curriculum/**` is routed; `/api/v1/curriculum/**` (student read endpoints) has no gateway route, making all student track/module/lesson/progress APIs unreachable through the gateway. [`services/gateway-service/src/main/resources/application.yml`]
- [x] [Review][Patch] `LessonResponse` leaks `solutionTemplate` over public student API — `solutionTemplate` is included in `LessonResponse` returned by `GET /api/v1/curriculum/lessons/{lessonId}`; students can trivially read the solution. [`services/curriculum-service/src/main/java/com/codeconnect/curriculum/application/dto/LessonResponse.java:12`]
- [x] [Review][Patch] `slug` field missing from `LessonDocument` but mapped in `CurriculumMapper` — `toLessonResponse` calls `doc.getSlug()` but `LessonDocument` has no `slug` field/getter; produces a compile error or NPE. [`services/curriculum-service/src/main/java/com/codeconnect/curriculum/application/mapper/CurriculumMapper.java:85`]
- [x] [Review][Patch] Mentors blocked from Curriculum Authoring Studio by Gateway RBAC — `/api/v1/admin/curriculum/**` path is restricted strictly to `ROLE_ADMIN`, returning 403 Forbidden for `ROLE_MENTOR`. [`services/gateway-service/src/main/java/com/codeconnect/gateway/infrastructure/security/RbacAccessDecisionManager.java:37`]
- [x] [Review][Patch] Admin Studio cannot fetch existing `solutionTemplate` for editing — `AdminCurriculumController` lacks GET lesson by ID endpoint, forcing Studio to use public API which sanitizes `solutionTemplate` to null. [`services/curriculum-service/src/main/java/com/codeconnect/curriculum/presentation/controller/AdminCurriculumController.java:30`]
- [x] [Review][Patch] Missing fallback language mode in `StoryReaderView` — Switching language mode when a lesson analogy is only available in one language renders an empty narrative. [`frontend/src/presentation/views/StoryReaderView.tsx:64`]
- [x] [Review][Patch] Missing Gateway RBAC integration test for `ROLE_MENTOR` curriculum access — No test asserts `ROLE_MENTOR` can access `/api/v1/admin/curriculum/**`. [`services/gateway-service/src/test/java/com/codeconnect/gateway/GatewayRbacIntegrationTest.java:45`]
- [ ] [Review][Patch] `target/classes/application.yml` committed to repository — Compiled build output file appears in diff; `target/` must be in `.gitignore` and must not be committed. [`services/curriculum-service/target/classes/application.yml`]
- [ ] [Review][Patch] `@Transactional` on `AdminCurriculumServiceImpl` is a no-op — No `MongoTransactionManager` bean or replica-set config; `@Transactional` is silently ignored, leaving partial writes (e.g. lesson saved but track summary update failed) unrolled back. [`services/curriculum-service/src/main/java/com/codeconnect/curriculum/application/service/impl/AdminCurriculumServiceImpl.java:17`]
- [x] [Review][Patch] `lessonCount` race condition on concurrent lesson creation — Two concurrent `createLesson` calls both read count=0 and write count=1 (lost update). Needs atomic increment or optimistic locking. [`services/curriculum-service/.../AdminCurriculumServiceImpl.java:61-76`]
- [x] [Review][Patch] `StudentProgressResponse.completedLessonIds` type mismatch — Java uses `Set<String>` (serializes to JSON array, non-deterministic order); TypeScript declares `string[]` (array); set-membership checks (`has()`) are unavailable. [`frontend/src/types/curriculum.ts:449` / `StudentProgressResponse.java:6`]
- [x] [Review][Patch] `CurriculumApplicationTests` requires live MongoDB — `@SpringBootTest` with a localhost Mongo URI will fail in CI; needs `@DataMongoTest` + Flapdoodle/Testcontainers or `@SpringBootTest` with mocked Mongo. [`services/curriculum-service/src/test/.../CurriculumApplicationTests.java`]
- [x] [Review][Patch] Silent swallow of progress errors in controller hook — `loadStudentProgress` catches all errors and discards them silently (`// Progress defaults gracefully`); failed loads are invisible to the user and untraceable. [`frontend/src/controller/useCurriculumController.ts:277-279`]
- [ ] [Review][Patch] `CurriculumControllerTest @WebMvcTest` may 401 if Spring Security auto-configures — No `@WithMockUser` or `@AutoConfigureMockMvc(addFilters = false)`; if Spring Security is on the classpath, all requests return 401 instead of 200. [`services/curriculum-service/src/test/.../CurriculumControllerTest.java:46`]
- [x] [Review][Defer] Manual builder/getter/setter boilerplate on all domain models — All domain models use hand-rolled builders instead of Lombok `@Builder`/`@Data`; Lombok is on the classpath and the annotation processor is configured. [`services/curriculum-service/src/main/java/com/codeconnect/curriculum/domain/model/`] — deferred: workaround for Maven annotation processor issue during initial build; to be cleaned up once pipeline build confirmed stable.
- [x] [Review][Defer] No `@CompoundIndex` on `student_progress` for `(userId, trackId)` — `findByUserIdAndTrackId` does a full collection scan at scale. [`StudentProgressDocument.java`] — deferred: no index strategy defined yet; Epic 7 performance hardening pass.
- [x] [Review][Defer] `createModule` uses bare `orElseThrow()` without message — Throws `NoSuchElementException` instead of `ResourceNotFoundException` with context. [`AdminCurriculumServiceImpl.java:38`] — deferred: validator runs first, so this path is unreachable in normal flow; clean up in Story 2.3.

### Rejected
- `false`: "AppHeader `footholdTitle→lessonTitle` rename breaks callers" — prop rename and all call-sites in the diff are consistent; no external callers changed.
- `false`: "`CurriculumValidator` does not check for duplicate lesson slugs" — spec does not require unique lesson slugs; only track and module slugs are required unique.
- `low` (rejected — fix adds complexity): "No Spring Data `@CreatedDate`/`@LastModifiedDate` auditing" — manual `Instant.now()` in mapper is functionally equivalent at this story's scope.
