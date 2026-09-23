# CodeConnect Engineering Standards & Clean Code Contract
# Applicable to: Java 21, Spring Boot 3.3 Microservices, Next.js 14 (TypeScript)

> **"Think Twice, Code Once."**
> Writing code is not merely about making tests pass or satisfying acceptance criteria; it is about building self-explanatory, maintainable, resilient, and decoupled software. Every class, interface, method, and variable must reveal its intent, respect architectural boundaries, and follow foundational Object-Oriented Analysis & Design (OOAD) principles.

---

## 1. Core Engineering Philosophy & OOAD Foundations

### 1.1 The "Think Twice, Code Once" Clean Code Mandate
1. **Self-Explanatory & Intention-Revealing Naming**:
   - Class, method, and variable names must express **what** they represent in the domain, not technical mechanics.
   - Good: `FootholdSubmissionService.dispatchForEvaluation(submission)`
   - Bad: `DataProcessor.handle(obj)` or `tempList` or `flag`.
2. **Small, Focused Functions (Single Level of Abstraction)**:
   - Methods should ideally be under 20-30 lines and do exactly one thing.
   - Extract conditional logic into well-named private helper methods (e.g., `boolean isEligibleForPeerChat(Student student)`).
3. **Ubiquitous Domain Language**:
   - Adhere strictly to domain terminology defined in the PRD and Architecture (e.g., `Foothold`, `Track`, `Ascent`, `SocraticTutor`, `EscalationTicket`), not generic CRUD terms (`Item`, `Entity`, `Task`).

### 1.2 SOLID & OOAD Principles in Action
* **Single Responsibility Principle (SRP)**:
  - Every class has only one reason to change. Controllers strictly handle HTTP mapping; Services enforce business rules; Repositories handle data access; Mappers handle DTO-to-entity translations.
* **Open/Closed Principle (OCP)**:
  - System components are open for extension but closed for modification. When adding a new capability (e.g., a new programming language runner or authentication provider), introduce a new implementation of an interface, rather than adding sprawling `if/else` or `switch` branches to existing classes.
* **Liskov Substitution Principle (LSP)**:
  - Derived classes and interface implementations must be completely substitutable for their abstractions without throwing unexpected exceptions (e.g., `UnsupportedOperationException`).
* **Interface Segregation Principle (ISP)**:
  - Clients should not be forced to depend on methods they do not use. Prefer multiple small, focused interfaces over bloated "god" interfaces.
* **Dependency Inversion Principle (DIP)**:
  - High-level policy modules must never depend on low-level infrastructure details. Both depend on abstractions (interfaces).
* **Favor Composition Over Inheritance**:
  - Do not create deep, fragile inheritance hierarchies. Compose behavior through interfaces and injected collaborators.
* **Law of Demeter (Principle of Least Knowledge)**:
  - A method should only call methods on its own class, its parameters, objects it creates, or its direct dependencies. Never write "train-wrecks" (e.g., `submission.getStudent().getProfile().getAddress().getCity()`).

---

## 2. Mandatory GoF Design Patterns Catalog

When implementing features, AI agents and engineers MUST leverage the appropriate design patterns rather than writing naive procedural code:

### 2.1 The Strategy Pattern
* **When to use**: When an algorithm or business process has multiple variations that can be swapped at runtime.
* **In CodeConnect**:
  - Code execution engines in `sandbox-runner-service` (e.g., `ExecutionStrategy` implemented by `Java21ExecutionStrategy`, future `PythonExecutionStrategy`).
  - Socratic coaching prompts (e.g., `SocraticPromptStrategy` implemented by `SyntaxErrorStrategy`, `RuntimeErrorStrategy`, `ConceptualHintStrategy`).
* **Implementation Standard**:
  Define a clean interface. Register implementations as Spring beans. Inject a `Map<StrategyKey, StrategyInterface>` or use a resolver registry.

### 2.2 The Facade Pattern
* **When to use**: To provide a unified, high-level interface to a complex set of subsystem operations.
* **In CodeConnect**:
  - `SubmissionFacade`: Orchestrates checking user session state, verifying prerequisite completion, persisting the pending submission to MongoDB, and dispatching the event to Apache Kafka.
  - Keeps controllers thin and shields presentation layers from internal microservice coordination.

### 2.3 The Factory / Registry Pattern
* **When to use**: When object creation involves complex business rules, type inspection, or dynamic polymorphic resolution.
* **In CodeConnect**:
  - `SandboxContainerFactory`: Instantiates ephemeral Docker container specifications with strictly locked down limits.
  - `KafkaEventConsumerRegistry`: Dynamically resolves consumer handlers for varying Kafka message types.

### 2.4 The Chain of Responsibility (CoR) Pattern
* **When to use**: When a request must pass through a sequential series of checks, sanitizations, or transformations before execution.
* **In CodeConnect**:
  - **Code Safety & Sanitization Pipeline**: Before submitting student code to Docker:
    1. `SourceCodeSizeFilter` (rejects payloads > 64KB)
    2. `MaliciousImportFilter` (rejects prohibited packages)
    3. `SyntaxSanityFilter` (validates package/class structure)
  - Each handler handles its validation or delegates down the chain (`filterChain.filter(context)`).

### 2.5 The Command Pattern
* **When to use**: To encapsulate an action or transactional operation as a first-class object, enabling queuing, logging, retries, and undo operations.
* **In CodeConnect**:
  - `VerifyPrerequisiteCommand`: Encapsulates checking student submission history, calculating streak ascent, and locking/unlocking progressive footholds.

### 2.6 The Builder Pattern
* **When to use**: For instantiating complex domain entities, aggregate roots, or test fixtures with multiple required and optional fields.
* **In CodeConnect**:
  - Use Lombok `@Builder` or explicit builder classes on complex domain entities.

### 2.7 The Observer / Event-Driven Pattern
* **When to use**: For state transitions that trigger non-blocking side effects across services.
* **In CodeConnect**:
  - Publish Spring `ApplicationEvent`s for internal decoupling.
  - Publish typed Kafka records (`CodeSubmissionEvent`, `ExecutionResultEvent`) for distributed asynchronous microservice decoupling.

---

## 3. Java 21 & Spring Boot 3.3 Microservice Standards

### 3.1 Idiomatic Java 21 Language Features
* **Records for All Immutable Data**: All DTOs, query projections, Kafka event payloads, and value objects MUST be Java 21 `record`s.
* **Pattern Matching for `switch` and `instanceof`**: Eliminate fragile type-casting.
* **Sealed Interfaces / Classes**: Use `sealed` to represent finite, secure domain states (e.g., `FootholdState permits Locked, Unlocked, Completed`).
* **Text Blocks**: Use text blocks for multiline queries, JSON payloads, or LLM system prompt templates.
* **Safe `Optional` Usage**:
  - Use `Optional<T>` **only** as a method return type for potentially absent single results.
  - Never use `Optional` for method parameters, class fields, or collections.
  - Never call `optional.get()` without checking; use `.orElseThrow()`, `.map()`, or `.ifPresent()`.
* **Modern Streams & Collections**:
  - Use `.toList()` (immutable) instead of `.collect(Collectors.toList())`.
  - Use `List.of()`, `Set.of()`, `Map.of()` for immutable in-memory collections.

### 3.2 Package Organization & Boundaries
Each service in `services/<service-name>/src/main/java/com/codeconnect/<servicename>/`:

```
com.codeconnect.<servicename>/
├── config/                  # @Configuration, Security, WebMvc, Kafka beans
│   └── <Service>Properties.java  # @ConfigurationProperties
├── controller/              # @RestController (Strictly HTTP/REST & validation)
├── dto/
│   ├── request/             # Java 21 records: e.g., RegisterUserRequest.java
│   └── response/            # Java 21 records: e.g., UserProfileResponse.java
├── service/                 # Domain business interfaces
│   ├── <Service>Service.java
│   └── impl/
│       └── <Service>ServiceImpl.java
├── repository/              # Spring Data Mongo interfaces
├── model/                   # Domain Entities (@Document)
├── exception/               # Custom Domain Exceptions & @RestControllerAdvice
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ErrorResponse.java
├── events/                  # Kafka Event Backbone
│   ├── payload/             # Event DTO records
│   ├── publisher/           # KafkaTemplate publishers
│   └── consumer/            # @KafkaListener handlers
└── util/                    # Stateless helper utilities (pure functions)
```

### 3.3 Strict Layering Rules
* **Controllers are Ultra-Thin**:
  - Controllers only do 3 things: (1) Receive `@Valid` request record, (2) Delegate to a Service Interface, (3) Return `ResponseEntity<ApiResponse<T>>`.
  - Zero business logic, zero entity instantiation, zero DB queries in controllers.
* **Interface-First Service Layer**:
  - Every service must have an `interface` (e.g., `UserService`) and implementation (`impl/UserServiceImpl`).
* **Anti-Corruption Layer (No Entity Leaks)**:
  - MongoDB `@Document` models must NEVER escape the Service layer. Controllers only see DTO records.
* **Constructor Injection Only**:
  - Field injection (`@Autowired private ...`) is strictly forbidden. All dependencies must be `private final` injected via constructors (or `@RequiredArgsConstructor`).
* **Zero Hardcoded Values**:
  - All timeouts, URLs, Kafka topic names, and thresholds must be externalized into `@ConfigurationProperties` classes.
* **Centralized Global Exception Handling**:
  - Throw domain-specific exceptions. Centralized `@RestControllerAdvice` catches them and produces standardized `ApiResponse<T>` with HTTP error codes.

---

## 4. Next.js 14 (Strict TypeScript) Frontend Standards

### 4.1 Strict Type System (Tailored for Java Engineers)
* **Zero JavaScript / 100% Strict TypeScript**:
  - All files in `apps/web/src/` must be `.ts` or `.tsx`.
  - `strict: true`, `noImplicitAny: true`, `strictNullChecks: true`.
  - The `any` type is completely banned. Use generics, unions, or `unknown` with type guards.
* **1:1 Type Parity with Java Backend Records**:
  - Every Java Request/Response record in backend services must have an exact TypeScript `interface` counterpart in `src/types/`.

### 4.2 Component & Layering Separation
* **Thin Route Pages**: Next.js App Router files (`page.tsx`) act as layout orchestrators only.
* **UI Primitives vs. Feature Components**:
  - `components/ui/`: Atomic, reusable design system components (Button, Modal, Drawer, Card).
  - `components/<feature>/`: Feature-specific domain assemblies.
* **Custom Hooks for State & Side-Effects**:
  - Never clutter components with 50-line `useEffect` or WebSocket connection logic. Extract into custom hooks (`hooks/useSubmissionRunner.ts`, `hooks/useSocraticChat.ts`).
* **Typed API Client Layer**:
  - Network requests are encapsulated in `lib/api/`, returning typed promises `Promise<ApiResponse<T>>`.

---

## 5. Domain-Driven Design (DDD) Standards

> **Rule**: Every microservice boundary in CodeConnect is a Bounded Context. Domain logic is never scattered across layers. The domain layer is king; infrastructure is a detail.

### 5.1 Strategic Design Rules
* **Bounded Context as Service Boundary**:
  - One microservice = one Bounded Context. A service must be **no smaller than an Aggregate** and **no larger than a Bounded Context**.
  - Example: `user-service` owns the `User` Bounded Context. It does not reach into `submission-service`'s domain model.
* **Ubiquitous Language is Mandatory**:
  - Use the same terminology in code, tests, documentation, and team conversations. No translation between business terms and code.
  - Bad: `LearningItem`, `ContentEntity`, `ProcessingTask`.
  - Good: `Foothold`, `Track`, `Ascent`, `EscalationTicket`, `SocraticSession`.
* **Context Mapping**:
  - When two services communicate, define the relationship explicitly with an Anti-Corruption Layer (ACL). Use published events (Kafka) as the integration surface, never shared database schemas.

### 5.2 Tactical Design Building Blocks
* **Entities**:
  - Defined by identity, not attribute equality. Must have a stable, unique ID on a `@Document`.
  - Entities encapsulate business behavior — they are **not** anemic data bags.
  - GOOD: A `Foothold` entity that has an `unlock(Student student)` method enforcing the invariant.
  - BAD: A `Foothold` entity that is just getters and setters with all logic in a Service.

* **Value Objects**:
  - Defined entirely by their attributes; no identity. Always immutable. Use Java 21 `record`s.
  - Never use primitive `String` for domain concepts that carry meaning.
  - Example: `record Email(String value)` with validation in the compact constructor, not raw `String email`.

* **Aggregates & Aggregate Roots**:
  - An Aggregate is a cluster of Entities and Value Objects treated as a **single consistency unit**.
  - Only the **Aggregate Root** is accessible from outside; internal entities are never directly referenced by other aggregates.
  - **One Repository per Aggregate Root** only.
  - All business invariants are enforced inside the Aggregate Root, never in the Service layer.

* **Domain Events**:
  - When something significant happens inside a domain, publish a Domain Event.
  - Domain Events are named in the **past tense**: `SubmissionGradedEvent`, `UserRegisteredEvent`, `FootholdUnlockedEvent`.
  - Internal domain events use Spring `ApplicationEventPublisher`. Cross-service events use Kafka.
  - Event record: `record SubmissionGradedEvent(String submissionId, String userId, GradeResult result, Instant occurredOn) {}`

* **Repositories**:
  - Repositories are domain interfaces. The Spring Data repository is an infrastructure implementation detail.
  - Domain layer: `FootholdRepository` interface in `domain/repository/`.
  - Infrastructure layer: `MongoFootholdRepository implements FootholdRepository` in `infrastructure/persistence/`.

* **Domain Services**:
  - A Domain Service holds business logic that does not naturally belong to any single Entity or Value Object.
  - Keep Domain Services thin; fat services indicate missing domain logic that should live in Entities.

### 5.3 DDD Package Layout (per Microservice)

```
com.codeconnect.<servicename>/
├── domain/
│   ├── model/               # Entities, Aggregate Roots
│   ├── valueobject/         # Value Objects as Java records
│   ├── event/               # Domain Events as Java records (past-tense names)
│   ├── repository/          # Domain repository interfaces (NOT Spring Data)
│   └── service/             # Domain service interfaces
├── application/
│   ├── service/             # Application service implementations (orchestrate domain)
│   │   └── impl/
│   ├── command/             # Command objects
│   └── dto/
│       ├── request/         # Inbound DTO records
│       └── response/        # Outbound DTO records
├── infrastructure/
│   ├── persistence/         # Spring Data MongoDB implementations
│   ├── messaging/           # Kafka publishers & consumers
│   └── config/              # @Configuration, @ConfigurationProperties
└── presentation/
    └── controller/          # @RestController, thin HTTP adapters
```

---

## 6. Event-Driven Architecture (EDA) Standards

> **Rule**: Services communicate via events on Kafka. Direct synchronous calls between services are used only for real-time user-facing queries; all state-changing operations are event-driven.

### 6.1 Core EDA Principles
* **Events are First-Class Citizens**:
  - An event represents something that **already happened** in the domain. It is immutable, past-tense, and owns a timestamp.
* **Loose Coupling via Events**:
  - Publishers never know who consumes their events. Consumers never call back to producers.
  - Anti-pattern: Consumer calling the producer's REST API to fetch more data after receiving an event. Embed all necessary data in the event payload (Event-Carried State Transfer).
* **Idempotency is Non-Negotiable**:
  - All Kafka consumers MUST be idempotent. Duplicate event delivery is expected in distributed systems.
  - Use a unique `eventId` (UUID) to deduplicate: track processed event IDs in Redis or in the database.

### 6.2 Choreography vs. Orchestration Decision Rules

| Approach | Use When | Example in CodeConnect |
|---|---|---|
| **Choreography** | Services are loosely coupled and independently scalable | User registers -> `UserRegisteredEvent` -> Kafka -> Notification service reacts |
| **Orchestration** | Complex, stateful, multi-step workflow needs visibility and rollback | Code submission pipeline: submit -> evaluate -> grade -> unlock foothold |
| **Hybrid** | Mix per workflow complexity | Default pattern in CodeConnect |

### 6.3 Kafka Event Contract Standards
* **Topic Naming Convention**: `<domain>.<entity>.<event-type>` — e.g.:
  - `codeconnect.submission.code-submitted`
  - `codeconnect.execution.result-produced`
  - `codeconnect.foothold.unlocked`
* **Versioning**: Events are versioned (`v1`, `v2`). Never break a published event schema. Add new optional fields; never remove or rename existing ones.
* **Dead-Letter Queue (DLQ)**: Every consumer must configure a DLQ topic (`<topic>.dlq`) to capture failed message processing. Never silently drop a failed event. Use `@RetryableTopic` with `dltTopicSuffix = ".dlq"`.

### 6.4 Standard Event Envelope

```java
public record DomainEvent<T>(
    String eventId,       // UUID for idempotency
    String eventType,     // e.g., "SubmissionGraded"
    String aggregateId,   // ID of the root entity
    String aggregateType, // e.g., "Submission"
    Instant occurredOn,   // UTC timestamp of the fact
    T payload             // The actual event data record
) {}
```

* **CQRS when Warranted**: Apply CQRS only when read and write models have significantly different scaling requirements. Do not over-engineer CQRS onto simple CRUD services.

### 6.5 Observability for EDA
* **Distributed Tracing**: Propagate `traceId` and `spanId` in Kafka message headers via Micrometer + OpenTelemetry.
* **Correlation ID**: Every request (REST or Kafka) must carry a `correlationId` that flows through all downstream events and services.
* **Structured Logging**: All logs must be structured JSON. Never use string concatenation; use `{}` placeholders.
  - Example: `log.info("Submission graded. submissionId={} userId={} result={}", submissionId, userId, result);`

---

## 7. Microservice & 15-Factor App Standards

> **Rule**: Every CodeConnect microservice must be independently deployable, stateless, and observable. No shared databases, no shared memory, no hidden coupling.

### 7.1 The 15 Factors Applied to CodeConnect

| # | Factor | CodeConnect Implementation Rule |
|---|---|---|
| 1 | **Codebase** | One Git repo per microservice subdirectory under `services/`. Monorepo but independently deployable. |
| 2 | **Dependencies** | All dependencies declared in `pom.xml`. No system-level dependencies assumed. |
| 3 | **Config** | **Zero** hardcoded URLs, ports, secrets, or Kafka topics. All config in environment variables read via `@ConfigurationProperties`. |
| 4 | **Backing Services** | MongoDB, Redis, Kafka are attached resources. URIs configured via environment. Swap local Mongo for Atlas by changing one env var. |
| 5 | **Build / Release / Run** | Strictly separate build, release, run stages. No `if (env == "local")` branches in code. |
| 6 | **Processes** | Services are completely stateless. No in-memory session state. User sessions via JWT; execution state via Redis or Kafka. |
| 7 | **Port Binding** | Each service exposes its own HTTP port via `server.port`. Self-contained. |
| 8 | **Concurrency** | Scale horizontally by running multiple service instances. Never assume singleton in-memory state. Design for N instances. |
| 9 | **Disposability** | Fast startup (< 10s). Graceful shutdown: drain in-flight Kafka consumers, finish current requests, then exit. |
| 10 | **Dev/Prod Parity** | Docker Compose local infra must mirror production infra. Never test on H2 if production uses MongoDB. |
| 11 | **Logs** | Logs are event streams only. Write to `stdout`. Never write log files from the application. Collected by Docker/Kubernetes log drivers. |
| 12 | **Admin Processes** | DB migrations (Mongock), seed scripts run as one-off processes / Jobs, not in the service startup path. |
| 13 | **API First** | Design and document API contracts (OpenAPI 3.x) **before** writing implementation. The contract is the source of truth. |
| 14 | **Telemetry** | Every service MUST expose: (a) `/actuator/health`, (b) Micrometer metrics -> Prometheus, (c) OpenTelemetry distributed traces. |
| 15 | **Auth & Security** | Zero-trust between services: every inter-service call is authenticated (JWT / mTLS). No service trusts another without validation. |

### 7.2 Service Autonomy & Independence Rules
* **Database per Service**: Each microservice owns its MongoDB database/collection. No cross-service queries.
* **No Shared Domain Logic**: Shared libraries (`common-lib`) are allowed only for technical concerns (e.g., `ApiResponse<T>`, logging utilities). Domain models or business logic must **never** be shared.
* **Service-to-Service Communication**:
  - **Synchronous (REST/gRPC)**: Use only for real-time, user-facing queries where latency matters.
  - **Asynchronous (Kafka)**: Use for all state-changing cross-service workflows.
* **Resilience Patterns**:
  - **Circuit Breaker** (Resilience4j): Wrap all synchronous HTTP calls to other services.
  - **Timeout**: Every HTTP client call has a configured read/connection timeout. Never wait indefinitely.
  - **Retry with Backoff**: Transient failures retried with exponential backoff and jitter.

### 7.3 API Design Standards (API-First)
* **OpenAPI 3.x**: Every REST service defines its OpenAPI spec in `src/main/resources/openapi/`.
* **Versioning**: API versions via URL path: `/api/v1/submissions`. Never version via headers for external APIs.
* **Standard Response Envelope**: All responses wrapped in `ApiResponse<T>` including `success`, `message`, `data`, `traceId`, and `timestamp`.
* **HTTP Semantics**: Use correct HTTP verbs and status codes. Return `201 Created` with a `Location` header for resource creation.

### 7.4 Security Standards (Zero-Trust)
* **JWT Authentication**: All APIs secured by JWT Bearer tokens. `user-service` issues tokens; all services validate them independently.
* **No Secrets in Code or Git**: Secrets are in environment variables or a secrets manager. Never committed to version control.
* **Principle of Least Privilege**: Services only have permissions to the resources they explicitly need.
* **Input Validation at the Boundary**: All incoming data validated at the controller layer (`@Valid`, `@NotBlank`, `@Size`).
* **Rate Limiting at the Gateway**: API Gateway applies rate limiting per user/IP before requests reach internal services.

---

## 8. Enforcement Checklist: "Think Twice, Code Once"

Before any story implementation is submitted for review, verify all applicable gates:

### Clean Code & Architecture
- [ ] **OOAD & Clean Code**: Are class and method names intention-revealing? Are methods short and focused?
- [ ] **Design Patterns Applied**: Are complex algorithms wrapped in Strategy? Multi-step actions in Facades/Commands? Pipelines in Chains?
- [ ] **No Entity Leaks**: Are DTO records used exclusively at the Controller interface?
- [ ] **Validation Present**: Are all incoming DTO records annotated with `@Valid` and constraints?
- [ ] **Interface + Impl**: Does every service follow the interface separation pattern?
- [ ] **Constructor Injection**: Are all injected fields `private final` with constructor injection?
- [ ] **Zero Hardcoded Values**: Are URLs, topics, and constants mapped via `@ConfigurationProperties`?
- [ ] **Centralized Exceptions**: Are errors handled via `@RestControllerAdvice` returning structured responses?

### Domain-Driven Design
- [ ] **Bounded Context Respected**: Does this service only manage its own domain data?
- [ ] **Ubiquitous Language**: Do all class/method names match the domain glossary (Foothold, Ascent, Track, etc.)?
- [ ] **Aggregate Invariants**: Is all business logic enforced inside Aggregate Roots, not in Service layers?
- [ ] **Value Objects Immutable**: Are domain concepts like Email, Score, and Language modeled as immutable records?
- [ ] **Domain Events Past-Tense**: Are events named in the past tense and published for cross-service state transitions?

### Event-Driven Architecture
- [ ] **Consumer Idempotency**: Does every Kafka consumer guard against duplicate event processing?
- [ ] **DLQ Configured**: Is there a Dead-Letter Queue configured for every Kafka consumer?
- [ ] **Event Envelope Standard**: Does every event include `eventId`, `aggregateId`, `occurredOn`, and a typed payload?
- [ ] **No Synchronous Cross-Service State Changes**: Are all state mutations propagated asynchronously via events?
- [ ] **Correlation ID Propagated**: Is the `correlationId` threaded through all events and logs in the flow?

### 15-Factor Compliance
- [ ] **Config Externalized**: Are all environment-specific values in env variables or `@ConfigurationProperties`?
- [ ] **Stateless Process**: Does the service store zero in-memory state that would differ between instances?
- [ ] **Health & Metrics Exposed**: Are `/actuator/health` and metrics endpoints active and configured?
- [ ] **Graceful Shutdown**: Will the service drain in-flight work before terminating?
- [ ] **OpenAPI Spec Present**: Is the API contract documented in OpenAPI 3.x before or alongside the implementation?
- [ ] **Secrets Not in Code**: Are all credentials sourced from environment, not hardcoded or in `application.yml`?

### Frontend (Next.js + TypeScript)
- [ ] **Strict TypeScript**: Is the frontend 100% strict TypeScript with zero `any` types?
- [ ] **State Encapsulation**: Are complex UI side-effects encapsulated in custom React hooks?
- [ ] **Type Parity**: Does every backend DTO record have a matching TypeScript interface in `src/types/`?
- [ ] **Server Components Default**: Are Client Components (`'use client'`) used only at interactive leaves?
