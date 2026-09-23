# CodeConnect Repository Agent Guidelines

This repository follows strict Clean Architecture, enterprise Java 21 / Spring Boot 3.3 microservices best practices, and Next.js 14 with strict TypeScript.

> **Mandate: "Think Twice, Code Once."**
> Never write quick-and-dirty procedural code. Every class, interface, and method must be self-explanatory, intention-revealing, modular, and designed around established OOAD principles and Gang-of-Four (GoF) design patterns.

## Core Architectural Rules
All development in this repository must strictly adhere to the standards codified in:
👉 **[coding-standards.md](file:///.agents/rules/coding-standards.md)**

### Mandatory Summary:
1. **Java 21 & Spring Boot 3.3**:
   - **Records for DTOs**: Every Request, Response, query projection, and event payload must be an immutable Java 21 `record`.
   - **No Entity Leaks**: Never expose `@Document` MongoDB models over HTTP.
   - **Thin Controllers**: Controllers only validate (`@Valid`) and delegate to a Service Interface.
   - **Interface + Impl**: Always use interfaces for business services (`UserService` / `UserServiceImpl`).
   - **Services as Facades/Orchestrators**: Service classes must act strictly as high-level facades orchestrating workflows at a single level of abstraction (SLAP). Never accumulate validation logic, entity-to-DTO mapping, session management, or utility algorithms as sprawling private helpers inside the service class; delegate to dedicated collaborator components (`validator/`, `mapper/`, `session/`).
   - **Constructor Injection**: Field injection (`@Autowired`) is forbidden; use `private final` fields.
   - **Design Patterns in Practice**:
     - *Strategy*: For swappable execution or AI prompt strategies.
     - *Facade*: For orchestrating complex multi-service workflows.
     - *Factory / Registry*: For dynamic handler resolution and container creation.
     - *Chain of Responsibility*: For sequential validation/sanitization pipelines.
     - *Command*: For encapsulating transactional state transitions and job actions.
     - *Builder*: For assembling complex entities and aggregate roots.
     - *Observer*: For event-driven Kafka messaging and Spring events.
   - **Modern Java 21**: Pattern matching (`switch`/`instanceof`), sealed interfaces, text blocks, safe `Optional` usage.
   - **Type-Safe Configuration**: Zero hardcoded URLs, ports, or topics; use `@ConfigurationProperties` as pure data holders (NEVER write defaulting/fallback logic in Java config classes; define all defaults in `application.yml` via `${ENV:default}`).
   - **Global Exception Handling**: Centralized `@RestControllerAdvice` returning structured `ApiResponse<T>`.

2. **Next.js 14 & TypeScript**:
   - **100% Strict TypeScript**: No JavaScript; `strict: true`; no `any`.
   - **Type Parity**: TypeScript interfaces in `src/types/` matching Java DTO records 1:1.
   - **Clean Layering**: Route pages are thin shells; business state and side effects live in custom hooks (`src/hooks/`); API calls live in `src/lib/api/`.
   - **Server Components by Default**: Client components (`'use client'`) only at interactive leaves.
