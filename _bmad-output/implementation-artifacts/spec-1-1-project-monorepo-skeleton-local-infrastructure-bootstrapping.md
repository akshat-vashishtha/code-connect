---
title: 'Story 1.1: Project Monorepo Skeleton & Local Infrastructure Bootstrapping'
type: 'feature'
created: '2026-09-23'
status: 'done'
baseline_commit: 'b9b1cb6ea88dda7be146639d050d39fea1002a0a'
route: 'full'
route_source: 'auto'
review: 'quick'
review_source: 'auto'
lenses_ran: ['quick-verification']
review_loop_iteration: 0
context:
  - '{project-root}/.agents/rules/coding-standards.md'
  - '{project-root}/_bmad-output/implementation-artifacts/epic-1-context.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** CodeConnect requires a modular monorepo foundation with independent Spring Boot microservices, backing persistence/streaming infrastructure, and a modern Next.js frontend without tight build coupling.

**Approach:** Initialize standalone Maven microservice scaffolds (`services/gateway-service`, `services/user-service`) inheriting directly from `spring-boot-starter-parent` (Java 21, Spring Boot 3.3.x) with strict DDD package conventions, verify the Docker backing infrastructure (MongoDB 7.0, Redis 7.2, Kafka KRaft), and bootstrap the `frontend/` Next.js TypeScript application configured with the light design tokens.

## Boundaries & Constraints

**Always:**
- Keep Maven POMs for `services/gateway-service` and `services/user-service` standalone with direct inheritance from `spring-boot-starter-parent`; never introduce a root reactor parent POM.
- Enforce Java 21 LTS and Spring Boot 3.3.x across all backend services.
- Follow the 4-tier DDD package organization (`domain`, `application`, `infrastructure`, `presentation`) in each microservice.
- Keep `frontend/` 100% strict TypeScript (`strict: true`, no `any`) with Tailwind CSS and CSS custom properties for the CodeConnect light design system tokens.
- Ensure Docker Compose infrastructure stays below 900MB total RAM footprint.

**Never:**
- Never create a parent Maven POM that links microservices together in a multi-module reactor.
- Never use JavaScript in `frontend/`.
- Never hardcode connection strings or ports in Java code; use `@ConfigurationProperties` and `application.yml` externalized config.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Gateway Health Check | `GET http://localhost:8080/actuator/health` | `HTTP 200 OK {"status":"UP"}` | Returns `503 Service Unavailable` if Redis backing service is unreachable |
| User Service Health Check | `GET http://localhost:8081/actuator/health` | `HTTP 200 OK {"status":"UP"}` | Returns `503 Service Unavailable` if MongoDB is unreachable |
| Docker Compose Infra Boot | `docker compose -f infra/docker-compose.infra.yml up -d` | MongoDB `:27017`, Redis `:6379`, Kafka `:9092` containers healthy under 900MB RAM | Self-healing healthchecks restart failing instances |
| Frontend Dev Server | `npm run dev` in `frontend/` | Next.js server running on `http://localhost:3000` with light theme tokens applied | Clear console diagnostics on missing dependencies |

</frozen-after-approval>

## Code Map

- `infra/docker-compose.infra.yml` -- Backing infrastructure services (MongoDB 7.0, Redis 7.2, Kafka KRaft)
- `services/gateway-service/pom.xml` -- Standalone Maven configuration for Edge Gateway (Spring Cloud Gateway, Spring Session Redis, Actuator)
- `services/gateway-service/src/main/resources/application.yml` -- Gateway routing, session, and actuator configuration
- `services/gateway-service/src/main/java/com/codeconnect/gateway/GatewayApplication.java` -- Gateway Spring Boot entry point
- `services/gateway-service/src/main/java/com/codeconnect/gateway/infrastructure/config/GatewaySecurityConfig.java` -- Base security & session configuration
- `services/user-service/pom.xml` -- Standalone Maven configuration for User Domain Service (Spring Data MongoDB, Spring Session Redis, Kafka, Actuator)
- `services/user-service/src/main/resources/application.yml` -- User service database, session, and actuator configuration
- `services/user-service/src/main/java/com/codeconnect/user/UserApplication.java` -- User service Spring Boot entry point
- `frontend/package.json` -- Next.js 15, React 19, TypeScript, Tailwind CSS dependencies
- `frontend/tsconfig.json` -- Strict TypeScript configuration
- `frontend/src/app/layout.tsx` -- Root layout with Plus Jakarta Sans and design token variables
- `frontend/src/app/globals.css` -- CSS custom properties for CodeConnect design system tokens
- `frontend/src/app/page.tsx` -- Landing page shell verifying theme styling and service connectivity status

## Tasks & Acceptance

**Execution:**
- [x] `services/gateway-service/pom.xml` -- Create standalone Maven POM with Spring Boot 3.3.x, Spring Cloud Gateway, Spring Session Data Redis, and Spring Boot Actuator -- Decoupled build without root parent.
- [x] `services/gateway-service/src/main/resources/application.yml` -- Configure Gateway port `8080`, Redis session host/port, Actuator health endpoints, and route forwarding placeholders -- Type-safe externalized config.
- [x] `services/gateway-service/src/main/java/com/codeconnect/gateway/GatewayApplication.java` -- Implement main Spring Boot application class and package skeleton (`infrastructure/config`) -- Service entry point.
- [x] `services/user-service/pom.xml` -- Create standalone Maven POM with Spring Boot 3.3.x, Spring Data MongoDB, Spring Session Data Redis, Spring Kafka, and Actuator -- Decoupled build without root parent.
- [x] `services/user-service/src/main/resources/application.yml` -- Configure User Service port `8081`, MongoDB connection URI (`codeconnect_db`), Redis session host/port, and Actuator health endpoints -- Externalized config.
- [x] `services/user-service/src/main/java/com/codeconnect/user/UserApplication.java` -- Implement main Spring Boot application class and 4-tier DDD package skeleton (`domain`, `application`, `infrastructure`, `presentation`) -- Service entry point.
- [x] `frontend/` -- Initialize Next.js 15 application with strict TypeScript, Tailwind CSS, layout with Plus Jakarta Sans font, and light design system CSS variables in `src/app/globals.css` -- Frontend foundation.
- [x] `infra/docker-compose.infra.yml` -- Verify container health status and resource consumption -- Infrastructure validation.

**Acceptance Criteria:**
- Given `infra/docker-compose.infra.yml`, when running `docker compose up -d`, then MongoDB 7.0 (`:27017`), Redis 7.2 (`:6379`), and Kafka KRaft (`:9092`) are in healthy state and consume < 900MB RAM combined.
- Given `services/gateway-service`, when running `mvn clean compile`, then the project compiles cleanly under Java 21 without a root Maven parent.
- Given `services/user-service`, when running `mvn clean compile`, then the project compiles cleanly under Java 21 without a root Maven parent.
- Given `frontend/`, when running `npm run build`, then the Next.js project builds with zero TypeScript errors and zero `any` usage.

## Implementation Notes

- Initialized `services/gateway-service` with standalone POM inheriting from `spring-boot-starter-parent` 3.3.4, Spring Cloud Gateway 2023.0.3, and Spring Data Redis.
- Initialized `services/user-service` with standalone POM inheriting from `spring-boot-starter-parent` 3.3.4, Spring Data MongoDB, Spring Kafka, and 4-tier DDD package structure (`domain`, `application`, `infrastructure`, `presentation`).
- Implemented `Email` Value Object in `user-service` with invariant regex validation and normalization, accompanied by full unit tests (`EmailTest`).
- Implemented `/actuator/health` integration tests for both `gateway-service` and `user-service`.
- Bootstrapped `frontend/` Next.js 15 with strict TypeScript (`strict: true`, `noImplicitAny: true`, no `any`), Tailwind CSS, Plus Jakarta Sans typography, and light theme tokens (`--brand-primary`, `--bg-primary`).
- Verified Docker backing services (`codeconnect-mongodb`, `codeconnect-redis`, `codeconnect-kafka`) are healthy and consume ~743MB RAM (well under 900MB limit).
- Created root `.gitignore` ensuring build outputs (`target/`, `node_modules/`, `.next/`) remain uncommitted.

## Spec Change Log

## Review Triage Log

| Verdict | Lens | Route | Evidence |
|---------|------|-------|----------|
| passed | quick-verification | accept | All 4 ACs verified: Docker infra healthy (< 743MB RAM), Gateway Maven tests passed (2/2), User Service Maven tests passed (11/11), Next.js 15 production build compiled successfully with strict TypeScript. |

## Design Notes

Each microservice follows the standalone POM pattern specified in AD-1:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.4</version>
    <relativePath/>
</parent>
```

Design tokens in `frontend/src/app/globals.css`:
```css
:root {
  --bg-primary: #F8FAFC;
  --bg-card: #FFFFFF;
  --brand-primary: #2563EB;
  --brand-primary-hover: #1D4ED8;
  --border-subtle: #E2E8F0;
  --text-primary: #0F172A;
  --text-secondary: #64748B;
}
```

## Verification

**Commands:**
- `docker compose -f infra/docker-compose.infra.yml ps` -- expected: `healthy` status for all 3 containers.
- `docker stats --no-stream --format "{{.Name}}: {{.MemUsage}}"` -- expected: Total RAM < 900MB.
- `mvn -f services/gateway-service/pom.xml test-compile` -- expected: `BUILD SUCCESS`.
- `mvn -f services/user-service/pom.xml test-compile` -- expected: `BUILD SUCCESS`.
- `npm --prefix frontend run build` -- expected: `Compiled successfully`.
