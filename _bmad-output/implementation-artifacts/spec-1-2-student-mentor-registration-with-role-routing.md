---
title: 'Story 1.2: Student & Mentor Registration with Role Routing'
type: 'feature'
created: '2026-09-23'
status: 'done'
baseline_commit: '664c358fa38b6d9568938f251d5115a3feb75516'
route: 'full'
route_source: 'auto'
review: 'quick'
review_source: 'auto'
lenses_ran: []
review_loop_iteration: 0
context:
  - '{project-root}/.agents/rules/coding-standards.md'
  - '{project-root}/_bmad-output/implementation-artifacts/epic-1-context.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Visitors need to register for CodeConnect as either Students or Mentors, with secure password hashing, session issuance, and differentiated onboarding flows (immediate activation for students, pending admin review for mentors).

**Approach:** Implement `POST /api/v1/auth/signup` in `gateway-service` with BCrypt password hashing, MongoDB persistence (`users` and `mentor_approval_requests`), distributed Redis reactive session creation with an opaque `APP_SESSION` cookie (`HttpOnly; Secure; SameSite=Strict`), RFC 7807 duplicate email handling (`HTTP 409`), and a Next.js 15 registration interface with student/mentor role routing.

## Boundaries & Constraints

**Always:**
- Hash all passwords using BCrypt (`BCryptPasswordEncoder`).
- Store student accounts with `status: ACTIVE` and mentor accounts with `status: PENDING_APPROVAL`.
- For mentor signups, record an entry in the MongoDB `mentor_approval_requests` collection with `status: PENDING`.
- Return `HTTP 409 Conflict` with RFC 7807 Problem Details when an email already exists in MongoDB `users`.
- Issue an opaque `APP_SESSION` cookie (`HttpOnly; Secure; SameSite=Strict; Path=/`) on successful registration; never send or expose JWTs to the client.
- Model all DTOs and value objects as Java 21 `record`s.
- Enforce pure `@ConfigurationProperties` with all defaults declared in `application.yml`.
- Build the Next.js signup page in strict TypeScript without `any`.

**Never:**
- Never expose plaintext passwords or password hashes in API responses or logs.
- Never grant mentor privileges immediately upon registration.
- Never write fallback or defaulting logic inside `@ConfigurationProperties` classes.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Student Signup | `POST /api/v1/auth/signup` with role `STUDENT`, valid email, password, displayName | `HTTP 201 Created`, user saved in `users` (`status: ACTIVE`), `Set-Cookie: APP_SESSION=...`, redirect to dashboard | Field validation errors return `400 Bad Request` |
| Mentor Signup | `POST /api/v1/auth/signup` with role `MENTOR`, linkedInUrl, bio | `HTTP 201 Created`, user saved in `users` (`status: PENDING_APPROVAL`), entry in `mentor_approval_requests`, redirect to Pending Review view | Returns `400 Bad Request` if `linkedInUrl` or `bio` missing for mentor |
| Duplicate Email | `POST /api/v1/auth/signup` with already registered email | `HTTP 409 Conflict`, RFC 7807 problem details: `{"type": ".../conflict", "title": "Email Conflict", "status": 409, "detail": "Email already registered"}` | No new user or session created |
| Invalid Email Format | `POST /api/v1/auth/signup` with email `notanemail` | `HTTP 400 Bad Request` with field validation errors | Rejected at controller boundary via `@Valid` |

</frozen-after-approval>

## Code Map

- `services/gateway-service/pom.xml` -- Add `spring-boot-starter-data-mongodb-reactive` and `spring-security-crypto`
- `services/gateway-service/src/main/resources/application.yml` -- Configure reactive MongoDB URI and session cookie properties
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/model/User.java` -- Reactive MongoDB `@Document(collection = "users")` model
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/model/MentorApprovalRequest.java` -- `@Document(collection = "mentor_approval_requests")` model
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/model/UserRole.java` -- Role enum (`ROLE_STUDENT`, `ROLE_MENTOR`, `ROLE_ADMIN`)
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/model/UserStatus.java` -- Status enum (`ACTIVE`, `PENDING_APPROVAL`, `BANNED`)
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/repository/ReactiveUserRepository.java` -- Spring Data Reactive Mongo repository
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/repository/ReactiveMentorApprovalRepository.java` -- Spring Data Reactive Mongo repository
- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/dto/request/SignupRequest.java` -- Java 21 request record with `@Valid` constraints
- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/dto/response/UserResponse.java` -- Java 21 response record
- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/service/AuthService.java` -- Authentication & registration business interface
- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/service/impl/AuthServiceImpl.java` -- Business logic implementation with BCrypt hashing and session creation
- `services/gateway-service/src/main/java/com/codeconnect/gateway/presentation/controller/AuthController.java` -- `@RestController` for `POST /api/v1/auth/signup`
- `services/gateway-service/src/main/java/com/codeconnect/gateway/presentation/exception/GlobalExceptionHandler.java` -- `@RestControllerAdvice` emitting RFC 7807 Problem Details
- `frontend/src/app/(auth)/signup/page.tsx` -- Next.js registration page with Student/Mentor tabs
- `frontend/src/app/(auth)/pending-approval/page.tsx` -- Mentor Pending Admin Review page
- `frontend/src/types/auth.ts` -- TypeScript interfaces for signup request and response

## Tasks & Acceptance

**Execution:**
- [x] `services/gateway-service/pom.xml` -- Add `spring-boot-starter-data-mongodb-reactive` and `spring-security-crypto` -- Reactive Mongo and BCrypt hashing.
- [x] `services/gateway-service/src/main/resources/application.yml` -- Add `spring.data.mongodb.uri` pointing to `${MONGODB_URI:mongodb://localhost:27017/codeconnect_db}` -- MongoDB config.
- [x] `services/gateway-service/` domain & application layer -- Implement `User`, `MentorApprovalRequest`, `SignupRequest`, `UserResponse`, and repository interfaces -- Data structures & repositories.
- [x] `services/gateway-service/` service & security layer -- Implement `AuthService`, `AuthServiceImpl` with BCrypt password hashing, role-based status routing, and Redis session hydration -- Core registration logic.
- [x] `services/gateway-service/` presentation layer -- Implement `AuthController` (`POST /api/v1/auth/signup`) and `GlobalExceptionHandler` with RFC 7807 problem details for `EmailAlreadyExistsException` -- REST boundary.
- [x] `services/gateway-service/src/test/java/com/codeconnect/gateway/` -- Implement comprehensive WebTestClient integration tests for student signup, mentor signup, duplicate email (409), and validation errors -- Verification suite.
- [x] `frontend/src/app/(auth)/signup/page.tsx` & `pending-approval/page.tsx` -- Build responsive Next.js registration views with role tabs, form validation, and routing -- UI implementation.

**Acceptance Criteria:**
- Given a valid registration form with role `STUDENT`, when submitting `POST /api/v1/auth/signup`, then password is hashed with BCrypt, user saved with `status: ACTIVE`, Redis session created with `APP_SESSION` cookie (`HttpOnly; Secure; SameSite=Strict`).
- Given a valid registration form with role `MENTOR` (with `linkedInUrl` and `bio`), when submitting `POST /api/v1/auth/signup`, then user is created with `status: PENDING_APPROVAL`, entry in `mentor_approval_requests` (`status: PENDING`), and client redirected to pending review.
- Given an existing email, when submitting `POST /api/v1/auth/signup`, then service returns `HTTP 409 Conflict` in RFC 7807 problem details format.

## Implementation Notes

- Added `spring-boot-starter-data-mongodb-reactive` and `spring-security-crypto` to `services/gateway-service/pom.xml`.
- Configured MongoDB reactive URI and removed `/api/v1/auth/**` from `user-service-route` in `application.yml` to prevent Gateway route collision with local AuthController.
- Adhered strictly to `@ConfigurationProperties` mandate: created `SessionProperties` as a pure data-holder record with defaults declared in `application.yml`.
- Configured `CookieWebSessionIdResolver` in `SessionConfig` with `APP_SESSION` cookie name and `SameSite=Strict`.
- Implemented `User` entity, `MentorApprovalRequest` entity, and respective Spring Data Reactive MongoDB repositories.
- Implemented `AuthService` and `AuthServiceImpl` with BCrypt password hashing, status routing (`ACTIVE` for students, `PENDING_APPROVAL` for mentors), mentor audit record insertion, WebSession attribute mutation, and `webSession.changeSessionId()` for immediate cookie generation.
- Implemented `GlobalExceptionHandler` using Spring's native `ProblemDetail` producing RFC 7807 payloads on 409 Conflict and 400 Bad Request.
- Verified backend via 7 passing integration tests (`AuthRegistrationIntegrationTest`, `GatewayHealthCheckTest`, `GatewayApplicationTests`).
- Built responsive Next.js 15 signup UI (`/signup`) with student/mentor tabs, password strength indicator, mentor-specific fields, and duplicate email warnings.
- Built Next.js 15 mentor waiting room (`/pending-approval`) with verification pipeline breakdown.
- Verified Next.js 15 build with 100% strict TypeScript (`npm run build`).

## Spec Change Log

## Review Triage Log

| Verdict | Lens | Route | Evidence |
|---------|------|-------|----------|
| passed | quick-verification | accept | All 3 Acceptance Criteria verified: Student signup persists ACTIVE user with BCrypt hash and APP_SESSION cookie (tested in `shouldRegisterStudentSuccessfully`), Mentor signup persists PENDING_APPROVAL user with audit entry in mentor_approval_requests (tested in `shouldRegisterMentorWithPendingApprovalAndCreateAuditRequest`), Duplicate email returns HTTP 409 Conflict with RFC 7807 problem details (tested in `shouldRejectDuplicateEmailWith409ConflictAndRfc7807ProblemDetail`). Maven tests passed (7/7), Next.js 15 build passed with zero TypeScript errors. |

## Design Notes

RFC 7807 Problem Detail response on duplicate email:
```json
{
  "type": "https://codeconnect.dev/errors/email-conflict",
  "title": "Email Conflict",
  "status": 409,
  "detail": "Email 'user@example.com' is already registered in CodeConnect",
  "timestamp": "2026-09-23T12:00:00Z"
}
```

Cookie configuration on `APP_SESSION`:
```
Set-Cookie: APP_SESSION=<session-id>; Path=/; HttpOnly; Secure; SameSite=Strict
```

## Verification

**Commands:**
- `mvn -f services/gateway-service/pom.xml test` -- expected: `BUILD SUCCESS` with all signup tests passing.
- `npm --prefix frontend run build` -- expected: Next.js compiles successfully with strict TypeScript.
