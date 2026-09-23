# Epic 1 Context: Foundation, User Authentication & Role Governance

<!-- Compiled from planning artifacts. Edit freely. Regenerate with compile-epic-context if planning docs change. -->

## Goal

Establish the core platform foundation, infrastructure, and security framework for CodeConnect. Enable students, mentors, and administrators to register, authenticate securely via distributed server-side Redis sessions with opaque `HttpOnly` cookies, manage profile preferences with dynamic Hinglish/English language detection, and govern platform access via dual-layer role-based access control (RBAC).

## Stories

- Story 1.1: Project Monorepo Skeleton & Local Infrastructure Bootstrapping
- Story 1.2: Student & Mentor Registration with Role Routing
- Story 1.3: Direct Credential Login, Redis Session Issuance & Logout
- Story 1.4: Dual-Layer RBAC Gatekeeping & Admin Mentor Verification

## Requirements & Constraints

- **Registration & Authentication**: Email/password registration with BCrypt hashing. Students activate immediately (`status: ACTIVE`). Mentors register with proof (LinkedIn URL, bio) in `status: PENDING_APPROVAL` awaiting admin review.
- **Session Security**: Browser never receives or stores JWTs. Authentication uses an opaque `APP_SESSION` cookie (`HttpOnly; Secure; SameSite=Strict`).
- **Dynamic Language Preference**: Chat and prompt interactions dynamically adapt to English or Hinglish based on conversational markers (e.g., "bhai", "samajh nahi aaya").
- **Admin Verification**: Administrators can inspect pending mentor applications and approve or reject them, elevating roles in real time.
- **Resource Constraints**: Local infrastructure (MongoDB 7.0, Redis 7.2, Kafka KRaft) must operate under 900MB total RAM.

## Technical Decisions

- **Monorepo Topology**: Root repository with standalone Maven Spring Boot services in `services/<service-name>/pom.xml` inheriting from `spring-boot-starter-parent` (Java 21 LTS, Spring Boot 3.3.x). No root reactor parent.
- **Dedicated Auth Gateway**: `gateway-service` (:8080) handles all auth endpoints (`/api/v1/auth/**`), issues `APP_SESSION` cookie, and routes to internal services.
- **Shared Session Cache**: All microservices share `spring-session-data-redis` (`spring:session` namespace). Instant global revocation and role promotion in $O(1)$ time via `sessionRepository.findByPrincipalName`.
- **Dual-Layer RBAC**: Gateway validates path prefixes (`/api/v1/admin/**`, `/api/v1/mentor/**`); domain services enforce `@PreAuthorize("hasRole(...)")` using authorities resolved from shared Redis session.
- **Persistence**: MongoDB 7.0 (`codeconnect_db`) stores `users` and `mentor_approval_requests`. Redis 7.2 is reserved for sessions and presence.
- **Clean Architecture & DDD**: All services follow Java 21 `record` DTOs, interface-first service contracts, constructor injection, and strict anti-corruption layers without entity leaks.
- **Frontend Stack**: Next.js 14/15 with 100% strict TypeScript, Tailwind CSS, Plus Jakarta Sans typography, and design tokens matching the light theme palette.

## UX & Interaction Patterns

- **Auth Pages**: Clean, focused signup and login views with student/mentor role selector tab.
- **Pending Review Screen**: Distinct waiting view for mentors pending admin verification, blocking mentor-specific navigation.
- **Admin Approval Desk**: Data table of pending mentor applications with one-click Approve/Reject actions.
- **Design Tokens**: Professional light theme palette (`--bg-primary: #F8FAFC`, `--brand-primary: #2563EB`, high-contrast borders).

## Cross-Story Dependencies

- Story 1.1 must establish the monorepo structure, Docker Compose stack, and base service configurations before Story 1.2 implements registration.
- Story 1.2 persists users in MongoDB and initiates sessions, which Story 1.3 relies on for credential verification and session lifecycle management.
- Story 1.4 requires the user data model and Redis session infrastructure from Stories 1.2 and 1.3 to enforce dual-layer RBAC and instant session mutation.
- All subsequent epics (Curriculum, Execution Sandbox, Socratic AI, Peer Chat) depend on the session cookie and user identity established in Epic 1.
