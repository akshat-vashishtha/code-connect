---
title: 'Story 1.4: Dual-Layer RBAC Gatekeeping & Admin Mentor Verification'
type: 'feature'
created: '2026-09-23'
status: 'review'
baseline_commit: 'e2443747a5c335b3341044403fa983d03eb0d500'
route: 'full'
route_source: 'auto'
review: ''
review_source: ''
lenses_ran: []
review_loop_iteration: 0
context: []
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Sensitive administrative and mentor capabilities lack perimeter gatekeeping at the API gateway and domain microservice boundaries, while pending mentor applications cannot be reviewed, approved, or elevated in real time. Additionally, the platform lacks conversational language preference detection to automatically tag sessions as Hinglish.

**Approach:** Implement dual-layer RBAC by enforcing route-level role gatekeeping in `gateway-service` and domain-level authorization in `user-service`. Expose admin verification endpoints to list, approve (with real-time Redis session elevation), and reject mentor applications, accompanied by a heuristic conversational language detector tagging session attributes.

## Boundaries & Constraints

**Always:**
- Use immutable Java 21 `record`s for all DTOs and event payloads.
- Enforce perimeter gatekeeping in `gateway-service`: `/api/v1/admin/**` requires `ROLE_ADMIN`; `/api/v1/mentor/**` requires `ROLE_MENTOR` or `ROLE_ADMIN`.
- Return standard RFC 7807 Problem Details (`application/problem+json`) with status `403 Forbidden` (`https://codeconnect.dev/errors/forbidden`) for unauthorized role access.
- When an admin approves a mentor, mutate the user's role to `ROLE_MENTOR` and status to `ACTIVE` in MongoDB `users`, and elevate active Redis session attributes (`USER_ROLE=ROLE_MENTOR`) in $O(1)$ time.
- Tag active session attribute `languagePreference = "HINGLISH"` when conversational text contains characteristic Hindi/Hinglish markers ("bhai", "samajh nahi aaya", "kaise kare", "yaar", "kya", "batao").

**Never:**
- Never expose JWT tokens in browser requests or cookies; retain opaque `APP_SESSION` cookies only.
- Never write sprawling private helper methods inside service classes; adhere strictly to the "Service as Facade" pattern delegating to validators, mappers, and session managers.
- Never store raw passwords or bypass BCrypt.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Student accesses Admin route | `GET /api/v1/admin/mentors/pending` with student session | `HTTP 403 Forbidden`, RFC 7807 Problem Detail (`errors/forbidden`) | Gateway rejects before downstream routing |
| Unauthenticated accesses Admin/Mentor route | `GET /api/v1/admin/**` or `/api/v1/mentor/**` with no cookie | `HTTP 401 Unauthorized` or `HTTP 403 Forbidden`, RFC 7807 | No downstream dispatch |
| Admin lists pending mentors | `GET /api/v1/admin/mentors/pending` with valid admin session | `HTTP 200 OK`, `ApiResponse<List<MentorApprovalResponse>>` | Returns empty list if none pending |
| Admin approves pending mentor | `POST /api/v1/admin/mentors/{id}/approve` with valid admin session | `HTTP 200 OK`, updates Mongo `users` & `mentor_approval_requests`, updates Redis session role | `404 Not Found` if application doesn't exist |
| Admin rejects pending mentor | `POST /api/v1/admin/mentors/{id}/reject` with valid admin session | `HTTP 200 OK`, updates `mentor_approval_requests.status = REJECTED` | `404 Not Found` if application doesn't exist |
| Hinglish marker detected | Text containing "bhai kaise kare samajh nahi aaya" | Language detector returns `HINGLISH`, updates session attribute `languagePreference` | Unmatched text defaults to `ENGLISH` |

</frozen-after-approval>

## Code Map

- `services/gateway-service/src/main/resources/application.yml` -- Route configuration mapping `/api/v1/admin/**` to `user-service` (:8081)
- `services/gateway-service/src/main/java/com/codeconnect/gateway/infrastructure/security/RbacGatewayFilter.java` -- Gateway filter enforcing path prefix role constraints (`ROLE_ADMIN` for `/admin/**`, `ROLE_MENTOR`/`ROLE_ADMIN` for `/mentor/**`)
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/exception/ForbiddenException.java` -- Domain exception mapped to RFC 7807 403 Forbidden
- `services/gateway-service/src/main/java/com/codeconnect/gateway/presentation/exception/GlobalExceptionHandler.java` -- Handler for `ForbiddenException`
- `services/gateway-service/src/test/java/com/codeconnect/gateway/GatewayRbacIntegrationTest.java` -- Gateway RBAC integration test suite
- `services/user-service/src/main/java/com/codeconnect/user/domain/model/MentorApprovalRequest.java` -- MongoDB entity for mentor applications
- `services/user-service/src/main/java/com/codeconnect/user/domain/model/User.java` -- MongoDB entity for user accounts
- `services/user-service/src/main/java/com/codeconnect/user/domain/model/LanguagePreference.java` -- Enum `ENGLISH`, `HINGLISH`
- `services/user-service/src/main/java/com/codeconnect/user/application/service/LanguageDetector.java` -- Heuristic Hinglish pattern recognizer
- `services/user-service/src/main/java/com/codeconnect/user/application/service/AdminMentorService.java` -- Service interface for mentor verification and session elevation
- `services/user-service/src/main/java/com/codeconnect/user/application/service/impl/AdminMentorServiceImpl.java` -- Facade implementation for mentor approval and Redis session mutation
- `services/user-service/src/main/java/com/codeconnect/user/presentation/controller/AdminMentorController.java` -- REST controller for `/api/v1/admin/mentors/**`
- `services/user-service/src/test/java/com/codeconnect/user/AdminMentorVerificationIntegrationTest.java` -- Integration test for mentor approval and session elevation

## Tasks & Acceptance

**Execution:**
- [x] `services/gateway-service/src/main/java/com/codeconnect/gateway/infrastructure/security/RbacGatewayFilter.java` -- Implement reactive RBAC filter -- Reject unauthorized roles with 403.
- [x] `services/gateway-service/src/main/resources/application.yml` -- Configure `/api/v1/admin/**` routing to `user-service` -- Forward admin traffic.
- [x] `services/gateway-service/src/test/java/com/codeconnect/gateway/GatewayRbacIntegrationTest.java` -- Implement gateway RBAC tests -- Validate student, mentor, admin route permissions.
- [x] `services/user-service/` domain and repository layer -- Setup `User`, `MentorApprovalRequest`, and Spring Data Mongo repositories -- Persistence foundation.
- [x] `services/user-service/src/main/java/com/codeconnect/user/application/service/LanguageDetector.java` -- Implement Hinglish marker detector and session tagger -- Conversational language adaptation.
- [x] `services/user-service/src/main/java/com/codeconnect/user/application/service/AdminMentorService.java` & `impl/` -- Implement mentor approval, rejection, and Redis session role elevation -- Verification business logic.
- [x] `services/user-service/src/main/java/com/codeconnect/user/presentation/controller/AdminMentorController.java` -- Expose `/api/v1/admin/mentors/pending`, `/approve`, `/reject` -- Admin verification REST API.
- [x] `services/user-service/src/test/java/com/codeconnect/user/AdminMentorVerificationIntegrationTest.java` -- Integration test for mentor verification and session mutation -- Backend automated verification.
- [x] `frontend/src/app/admin/mentors/page.tsx` -- Admin verification dashboard for reviewing and approving pending mentors -- Administrative UI.

**Acceptance Criteria:**
- Given a user with `ROLE_STUDENT` attempting to access `/api/v1/admin/**` or `/api/v1/mentor/**`, gateway rejects with `HTTP 403 Forbidden`.
- Given an Admin user calling `GET /api/v1/admin/mentors/pending`, returns all pending applications.
- Given an Admin approving an application, the user's role in Mongo is elevated to `ROLE_MENTOR`, status to `ACTIVE`, and the active Redis session attribute is updated to `ROLE_MENTOR`.
- Given text input containing Hindi/Hinglish markers ("bhai", "samajh nahi aaya", "kaise kare"), `LanguageDetector` classifies it as `HINGLISH`.

## Implementation Notes

1. **Perimeter RBAC Gatekeeper (`gateway-service`)**:
   - Implemented `RbacGatewayFilter` (`Ordered.HIGHEST_PRECEDENCE + 50`) intercepting `/api/v1/admin/**` (requires `ROLE_ADMIN`) and `/api/v1/mentor/**` (requires `ROLE_MENTOR` or `ROLE_ADMIN`).
   - Rejection writes an RFC 7807 `ProblemDetail` with `type="https://codeconnect.dev/errors/forbidden"`, status `403 Forbidden`, and custom JSON detail.
   - For authorized requests, propagates downstream identity headers `X-User-Id`, `X-User-Role`, and `X-User-Email`.
   - Verified via `GatewayRbacIntegrationTest` covering unauthenticated, student, mentor, admin, and open public routes (5/5 passed).

2. **Domain Persistence & Administrative Verification (`user-service`)**:
   - Domain models: `User` (in `users` collection) and `MentorApprovalRequest` (in `mentor_approval_requests` collection).
   - `AdminMentorService` & `AdminMentorServiceImpl` facade orchestrating approval, rejection, and Redis session elevation.
   - `SessionElevationManager`: Mutates the active Redis session hash (`spring:session:sessions:<id>`) attributes `sessionAttr:USER_ROLE="ROLE_MENTOR"` and `sessionAttr:USER_STATUS="ACTIVE"` in $O(1)$ time without forcing user re-login.
   - Verified via `AdminMentorVerificationIntegrationTest` covering pending query, approval elevation in MongoDB and Redis, and rejection (16/16 tests green).

3. **Conversational Language Detector**:
   - `LanguageDetector`: Heuristic regex matcher evaluating phonetic Hinglish markers (`bhai`, `samajh nahi aaya`, `kaise kare`, `yaar`, `kya`, `batao`, `theek hai`).
   - `UserLanguageController` (`POST /api/v1/users/detect-language`) returning classification and tagging session attribute `languagePreference = HINGLISH`.

4. **Frontend Admin Verification Dashboard (`frontend`)**:
   - Added types `frontend/src/types/admin.ts` with `MentorApprovalResponse` and `LanguageDetection*` contracts.
   - Added API client `frontend/src/lib/api/admin.ts` with `getPendingMentors()`, `approveMentor()`, `rejectMentor()`, `detectLanguage()`.
   - Built modern, responsive dashboard `frontend/src/app/admin/mentors/page.tsx` featuring real-time application cards, LinkedIn profile deep links, applicant bio, one-click approve/reject actions with optimistic UI feedback, and an interactive Hinglish detection sandbox widget.
   - Updated `frontend/src/components/auth/AuthNav.tsx` with admin role detection and Admin Dashboard navigation link.
   - Verified clean Next.js 15 production build with 100% strict TypeScript.

## Spec Change Log

## Review Triage Log

## Design Notes

RFC 7807 403 Forbidden response payload:
```json
{
  "type": "https://codeconnect.dev/errors/forbidden",
  "title": "Access Denied",
  "status": 403,
  "detail": "Insufficient role permissions for requested resource",
  "timestamp": "2026-09-23T21:30:00Z"
}
```

Redis Session Elevation Strategy:
```java
// When admin approves mentor, retrieve session from RedisSessionRepository and elevate:
Session session = sessionRepository.findById(sessionId);
session.setAttribute("USER_ROLE", "ROLE_MENTOR");
session.setAttribute("USER_STATUS", "ACTIVE");
sessionRepository.save(session);
```

## Verification

**Commands:**
- `mvn -f services/gateway-service/pom.xml test` -- expected: `BUILD SUCCESS` with RBAC filter tests passing.
- `mvn -f services/user-service/pom.xml test` -- expected: `BUILD SUCCESS` with admin verification tests passing.
- `npm --prefix frontend run build` -- expected: Next.js compiles with strict TypeScript.
