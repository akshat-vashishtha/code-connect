# Walkthrough Narrative: Story 1.1 — Monorepo Skeleton & Local Infrastructure

Target: Commits `17a9442` and `664c358` (`feature/idea`)

## Review Blocks

- [x] Block 1: Intent (completed)
- [ ] Block 2: Broad strokes (current)
- [ ] Block 3: Slice 1 — Standalone Maven Backend Services & Clean DDD Foundation
- [ ] Block 4: Slice 2 — Next.js 15 Frontend with Strict TypeScript & Design Tokens
- [ ] Block 5: Slice 3 — Docker Infrastructure & Backing Services
- [ ] Block 6: Periphery & Governance

---

### Block 1: Intent

**Problem:** CodeConnect requires a modular monorepo foundation with independent Spring Boot microservices, backing persistence/streaming infrastructure, and a modern Next.js frontend without tight build coupling.

**Approach:** Initialize standalone Maven microservice scaffolds (`services/gateway-service`, `services/user-service`) inheriting directly from `spring-boot-starter-parent` (Java 21, Spring Boot 3.3.x) with strict DDD package conventions, verify the Docker backing infrastructure (MongoDB 7.0, Redis 7.2, Kafka KRaft), and bootstrap the `frontend/` Next.js TypeScript application configured with the light design tokens.

---

### Block 2: Broad Strokes

1. [`services/gateway-service/pom.xml`](../../services/gateway-service/pom.xml) — Standalone Maven POM for edge gateway (Spring Cloud Gateway, Redis Session, Actuator).
2. [`services/gateway-service/src/main/resources/application.yml`](../../services/gateway-service/src/main/resources/application.yml) — Gateway routing table and externalized config via `${ENV:default}`.
3. [`services/user-service/pom.xml`](../../services/user-service/pom.xml) — Standalone Maven POM for User Service (MongoDB, Redis, Kafka, Actuator).
4. [`services/user-service/src/main/resources/application.yml`](../../services/user-service/src/main/resources/application.yml) — User service persistence and messaging connection properties.
5. [`frontend/src/app/page.tsx`](../../frontend/src/app/page.tsx) — Next.js 15 entry cockpit dashboard displaying service grid and theme tokens.

---

### Block 3: Slice 1 — Standalone Maven Backend Services & Clean DDD Foundation

Two independent microservices with no shared parent reactor POM. Java 21 `record`s are used for DTOs and Value Objects. All `@ConfigurationProperties` are pure data containers with zero logic.

- [`services/gateway-service/src/main/java/com/codeconnect/gateway/GatewayApplication.java`](../../services/gateway-service/src/main/java/com/codeconnect/gateway/GatewayApplication.java) — Gateway entry point.
- [`services/gateway-service/src/main/java/com/codeconnect/gateway/infrastructure/config/GatewayProperties.java`](../../services/gateway-service/src/main/java/com/codeconnect/gateway/infrastructure/config/GatewayProperties.java) — Pure configuration record.
- [`services/user-service/src/main/java/com/codeconnect/user/UserApplication.java`](../../services/user-service/src/main/java/com/codeconnect/user/UserApplication.java) — User service entry point.
- [`services/user-service/src/main/java/com/codeconnect/user/domain/valueobject/Email.java`](../../services/user-service/src/main/java/com/codeconnect/user/domain/valueobject/Email.java) — Immutable Value Object with regex validation.
- [`services/user-service/src/main/java/com/codeconnect/user/application/dto/response/ApiResponse.java`](../../services/user-service/src/main/java/com/codeconnect/user/application/dto/response/ApiResponse.java) — Unified response envelope.
- [`services/user-service/src/test/java/com/codeconnect/user/domain/valueobject/EmailTest.java`](../../services/user-service/src/test/java/com/codeconnect/user/domain/valueobject/EmailTest.java) — 9 parameterized unit tests covering email invariants.
- [`services/gateway-service/src/test/java/com/codeconnect/gateway/GatewayHealthCheckTest.java`](../../services/gateway-service/src/test/java/com/codeconnect/gateway/GatewayHealthCheckTest.java) — Gateway Actuator health check test.
- [`services/user-service/src/test/java/com/codeconnect/user/UserHealthCheckTest.java`](../../services/user-service/src/test/java/com/codeconnect/user/UserHealthCheckTest.java) — User Service Actuator health check test.

---

### Block 4: Slice 2 — Next.js 15 Frontend with Strict TypeScript & Design Tokens

Next.js 15 App Router setup with 100% strict TypeScript (no `any`), Plus Jakarta Sans typography, Tailwind CSS custom variables, and type parity interfaces.

- [`frontend/package.json`](../../frontend/package.json) — Dependencies (Next.js 15, React 19, Tailwind CSS, Lucide icons).
- [`frontend/tsconfig.json`](../../frontend/tsconfig.json) — Strict TypeScript configuration (`noImplicitAny`, `strictNullChecks`).
- [`frontend/src/app/globals.css`](../../frontend/src/app/globals.css) — CSS variables for `--brand-primary`, `--bg-primary`, `--border-subtle`.
- [`frontend/src/types/api.ts`](../../frontend/src/types/api.ts) — TypeScript interfaces matching Java DTO records 1:1.
- [`frontend/src/app/layout.tsx`](../../frontend/src/app/layout.tsx) — Root layout with Google font configuration.
- [`frontend/src/app/page.tsx`](../../frontend/src/app/page.tsx) — Status cockpit dashboard.

---

### Block 5: Slice 3 — Docker Infrastructure & Backing Services

Local containerized backing infrastructure running under 900MB total RAM limit.

- [`infra/docker-compose.infra.yml`](../../infra/docker-compose.infra.yml) — MongoDB 7.0 (`:27017`), Redis 7.2 (`:6379`), Kafka KRaft (`:9092`).
- Total RAM measured: ~743MB.

---

### Block 6: Periphery & Governance

- [`.gitignore`](../../.gitignore) — Root monorepo ignore rules for Maven `target/`, Node `node_modules/`, `.next/`, IDEs.
- [`.agents/rules/coding-standards.md`](../../.agents/rules/coding-standards.md) — Comprehensive rules for Clean Code, GoF patterns, DDD, EDA, 15-Factor App, and pure `@ConfigurationProperties`.
- [`AGENTS.md`](../../AGENTS.md) — Agent system prompt anchor enforcing repository rules.
