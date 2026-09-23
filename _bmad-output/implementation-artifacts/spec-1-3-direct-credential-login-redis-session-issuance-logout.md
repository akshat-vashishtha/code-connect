---
title: 'Story 1.3: Direct Credential Login, Redis Session Issuance & Logout'
type: 'feature'
created: '2026-09-23'
status: 'in-progress'
baseline_commit: '44d5e140c0b5098ce8951893943381f2fe1990a0'
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

**Problem:** Registered students and mentors need to authenticate using their email and password, receive a distributed Redis session via an opaque cookie, verify their current identity, and terminate sessions securely without exposing tokens to browser JavaScript.

**Approach:** Implement `POST /api/v1/auth/login`, `GET /api/v1/auth/me`, and `POST /api/v1/auth/logout` in `gateway-service` (:8080) with BCrypt verification against MongoDB `users`, reactive Redis session lifecycle management (`APP_SESSION` cookie), RFC 7807 problem details for invalid credentials (`401`) and banned accounts (`403`), and a Next.js 15 login interface with role-directed post-login routing.

## Boundaries & Constraints

**Always:**
- Verify credentials against MongoDB `users` using `BCryptPasswordEncoder.matches()`.
- Return `HTTP 401 Unauthorized` with RFC 7807 Problem Details on invalid credentials (email not found or wrong password) without leaking whether the email exists.
- Return `HTTP 403 Forbidden` with RFC 7807 Problem Details if the user's `status` is `BANNED`.
- Maintain active Redis sessions storing `USER_ID`, `USER_EMAIL`, `USER_ROLE`, `USER_STATUS`.
- Rotate session ID (`webSession.changeSessionId()`) upon successful login to prevent session fixation.
- Invalidate session in Redis immediately ($O(1)$) on `POST /api/v1/auth/logout` (`webSession.invalidate()`).
- Return `HTTP 200 OK` with sanitized `UserResponse` on `GET /api/v1/auth/me` when session is valid; return `HTTP 401 Unauthorized` when session is missing or invalid.
- Model all request/response DTOs as Java 21 `record`s.
- Enforce strict TypeScript in frontend without any `any`.

**Never:**
- Never expose password hashes or sensitive internal properties in API responses.
- Never differentiate error messages between "user not found" and "incorrect password" to guard against user enumeration attacks.
- Never write fallback or defaulting logic inside `@ConfigurationProperties` classes.
- Never store or expose JWTs in browser cookies or client memory.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Valid Student Login | `POST /api/v1/auth/login` with registered email & correct password | `HTTP 200 OK`, `ApiResponse<UserResponse>`, `Set-Cookie: APP_SESSION=...`, redirect to home/dashboard | Standard 400 on malformed input |
| Valid Mentor Login (Pending) | `POST /api/v1/auth/login` for mentor in `PENDING_APPROVAL` status | `HTTP 200 OK`, `ApiResponse<UserResponse>` with status `PENDING_APPROVAL`, cookie issued, routed to `/pending-approval` | None |
| Invalid Credentials (Wrong Password) | `POST /api/v1/auth/login` with correct email & wrong password | `HTTP 401 Unauthorized`, RFC 7807 Problem Details (`invalid-credentials`) | No session created, cookie not issued |
| Non-Existent Email | `POST /api/v1/auth/login` with unregistered email | `HTTP 401 Unauthorized`, RFC 7807 Problem Details (`invalid-credentials`) | Same error detail as wrong password |
| Banned Account Login | `POST /api/v1/auth/login` with valid credentials for user with `status: BANNED` | `HTTP 403 Forbidden`, RFC 7807 Problem Details (`account-banned`) | No session created |
| Valid Session Verification | `GET /api/v1/auth/me` with valid `APP_SESSION` cookie | `HTTP 200 OK`, `ApiResponse<UserResponse>` with profile details | None |
| Unauthenticated Session Check | `GET /api/v1/auth/me` without cookie or with expired session | `HTTP 401 Unauthorized`, RFC 7807 Problem Details (`unauthorized`) | Client clears stale state |
| User Logout | `POST /api/v1/auth/logout` with active session | `HTTP 200 OK`, `webSession.invalidate()`, Redis session deleted, cookie expired (`Max-Age=0`) | Subsequent `/me` returns 401 |

</frozen-after-approval>

## Code Map

- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/dto/request/LoginRequest.java` -- Inbound login command record with `@NotBlank` and `@Email`
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/exception/InvalidCredentialsException.java` -- Domain exception for authentication failure
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/exception/AccountBannedException.java` -- Domain exception for banned accounts
- `services/gateway-service/src/main/java/com/codeconnect/gateway/domain/exception/UnauthorizedException.java` -- Domain exception for unauthenticated requests
- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/service/AuthService.java` -- Extended with `login()`, `getCurrentUser()`, and `logout()`
- `services/gateway-service/src/main/java/com/codeconnect/gateway/application/service/impl/AuthServiceImpl.java` -- Implement login verification, session hydration, and logout invalidation
- `services/gateway-service/src/main/java/com/codeconnect/gateway/presentation/controller/AuthController.java` -- Added `POST /login`, `GET /me`, `POST /logout`
- `services/gateway-service/src/main/java/com/codeconnect/gateway/presentation/exception/GlobalExceptionHandler.java` -- Handlers for 401 and 403 RFC 7807 Problem Details
- `services/gateway-service/src/test/java/com/codeconnect/gateway/AuthLoginLogoutIntegrationTest.java` -- Integration test suite covering login, /me, and logout
- `frontend/src/types/auth.ts` -- TypeScript interface for `LoginRequest`
- `frontend/src/lib/api/auth.ts` -- API client functions `login()`, `getMe()`, `logout()`
- `frontend/src/app/(auth)/login/page.tsx` -- Next.js 15 login page with validation, error states, and redirect routing
- `frontend/src/app/page.tsx` -- Update header with user session indicator & logout action

## Tasks & Acceptance

**Execution:**
- [x] `services/gateway-service/` DTO & domain exceptions -- Create `LoginRequest`, `InvalidCredentialsException`, `AccountBannedException`, `UnauthorizedException` -- Request structures & domain errors.
- [x] `services/gateway-service/` application service -- Extend `AuthService` and `AuthServiceImpl` with `login()`, `getCurrentUser()`, and `logout()` -- Business logic & session mutation.
- [x] `services/gateway-service/` presentation layer -- Add `POST /login`, `GET /me`, `POST /logout` in `AuthController` and update `GlobalExceptionHandler` with 401/403 problem details -- REST endpoints & error handling.
- [x] `services/gateway-service/src/test/java/com/codeconnect/gateway/` -- Implement `AuthLoginLogoutIntegrationTest` verifying all I/O matrix scenarios -- Automated backend verification.
- [x] `frontend/src/lib/api/auth.ts` & `src/types/auth.ts` -- Add `login()`, `getMe()`, `logout()` API functions and `LoginRequest` interface -- Frontend API client.
- [x] `frontend/src/app/(auth)/login/page.tsx` & `frontend/src/app/page.tsx` -- Create login page with form validation, role routing, and header auth state -- UI implementation.

**Acceptance Criteria:**
- Given valid registered credentials, when submitting `POST /api/v1/auth/login`, then service verifies BCrypt hash against MongoDB `users`, creates Redis session, and sets `APP_SESSION` cookie.
- Given an active `APP_SESSION` cookie, calling `GET /api/v1/auth/me` returns `HTTP 200 OK` with user profile details.
- Given invalid credentials or a banned account, the service returns `HTTP 401 Unauthorized` or `HTTP 403 Forbidden` in RFC 7807 problem details format.
- Given an authenticated user calling `POST /api/v1/auth/logout`, the Redis session is invalidated and browser cookie is cleared.

## Implementation Notes

- **Strict Service Facade & Collaborators**:
  - `AuthServiceImpl` strictly orchestrates at a Single Level of Abstraction (SLAP) with zero private helper methods.
  - Verification is cleanly delegated to `CredentialValidator`, which encapsulates user lookup, BCrypt verification, account status evaluation, and anti-enumeration invariants.
  - Session hydration, ID rotation (`webSession.changeSessionId()`), and persistence are handled in `SessionManager`.
- **Anti-Enumeration Guard**:
  - `CredentialValidator` raises `InvalidCredentialsException` with identical title and detail for both non-existent emails and wrong passwords.
- **RFC 7807 Standard Compliance**:
  - `GlobalExceptionHandler` renders standard problem details with URIs:
    - 401 Unauthorized: `https://codeconnect.dev/errors/invalid-credentials`
    - 401 Unauthorized: `https://codeconnect.dev/errors/unauthorized`
    - 403 Forbidden: `https://codeconnect.dev/errors/account-banned`
- **Reactive Integration Tests**:
  - `AuthLoginLogoutIntegrationTest` verifies 8 integration scenarios covering active student login, pending mentor login, bad password, missing email, banned account, `/me` profile retrieval, unauthenticated access, and logout session invalidation with `Max-Age=0` cookie expiration.
  - All 15 tests in `gateway-service` pass cleanly (`mvn test`).
- **Next.js 15 & Strict TypeScript**:
  - Client component `/login` built with form validation, password show/hide, responsive error banners, and role-based redirect.
  - Interactive leaf `AuthNav` component added to `/` to reflect live authentication status and handle session logout.
  - Frontend production build passed cleanly (`next build`).

## Spec Change Log
- Refactored `AuthServiceImpl` to adhere to "Service as Facade" architectural standard using 4 collaborators (`RegistrationValidator`, `CredentialValidator`, `SessionManager`, `UserMapper`, and `MentorApprovalService`).

## Review Triage Log
- All 6 implementation tasks completed and verified with automated test suites.
- Type parity maintained 1:1 between Java DTO records and TypeScript interfaces.

## Design Notes

RFC 7807 Problem Detail response on invalid credentials:
```json
{
  "type": "https://codeconnect.dev/errors/invalid-credentials",
  "title": "Invalid Credentials",
  "status": 401,
  "detail": "Invalid email or password",
  "timestamp": "2026-09-23T18:00:00Z"
}
```

RFC 7807 Problem Detail response on banned account:
```json
{
  "type": "https://codeconnect.dev/errors/account-banned",
  "title": "Account Banned",
  "status": 403,
  "detail": "This account has been suspended by an administrator",
  "timestamp": "2026-09-23T18:00:00Z"
}
```

## Verification

**Commands:**
- `mvn -f services/gateway-service/pom.xml test` -- expected: `BUILD SUCCESS` with all auth tests passing.
- `npm --prefix frontend run build` -- expected: Next.js compiles successfully with strict TypeScript.
