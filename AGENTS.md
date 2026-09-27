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
   - **Type-Safe Configuration**: Zero hardcoded URLs, URIs, ports, topics, or email domain strings; all externalized via `application.yml` (`${ENV:default}`) and injected via pure `@ConfigurationProperties` data holders (zero logic).
   - **Enums in Dedicated `domain.enums` Package**: All discrete domain values, statuses, and roles must be enums placed in `domain.enums` (never mixed into `domain.model`).
   - **Global Exception Handling**: Centralized `@RestControllerAdvice` returning structured `ApiResponse<T>` / `ProblemDetail`.

2. **Next.js 14 & TypeScript**:
   - **100% Strict TypeScript**: No JavaScript; `strict: true`; no `any`.
   - **Type & DTO Parity**: TypeScript interfaces in `src/dto/` and `src/types/` matching Java DTO records 1:1.
   - **Strict Segregation of Structure / Logic vs. UI / Presentation**:
     - **Structural Layer** (`controller/`, `service/`, `domain/`, `validator/`, `client/`): Pure custom hooks, state, API facades, input validators, and domain rules. Zero JSX / UI styling!
     - **UI Presentation Layer** (`app/`, `presentation/`, `components/`): Thin route shells, feature UI assemblies, and atomic primitives. Zero business logic or raw `fetch` calls!
   - **SOLID & GoF Design Patterns**: Controller Hook pattern (`src/controller/`), Service Facades (`src/service/`), Mapper Adapters (`src/lib/mappers/`), Validation Chains (`src/validator/`), and Strategy runners.
3. **Anti-Shortcutting & Production Integrity Mandate**:
   - **Zero Dummy / Mock Logic**: Never write dummy fallback branches, fake mock data, stubbed return values, or commented-out checks to pass unit tests or bypass errors.
   - **Zero Hardcoded Domain Literals**: Never hardcode domain URLs (`codeconnect.dev`), error URIs, JWT secret strings, or topic names in Java/TypeScript code. Externalize all values to `application.yml` / `.env`.
   - **Pure Data Holders**: `@ConfigurationProperties` classes MUST remain pure data holders without logic or fallback code.
