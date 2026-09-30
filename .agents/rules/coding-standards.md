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
### 1.3 Anti-Shortcutting, Anti-Dummy Code & Hardcoding Mandate

> **Strict Rule**: AI Agents and Engineers MUST NEVER write quick-and-dirty procedural hacks, dummy fallback checks, stubbed mock responses, or hardcoded literal assumptions to pass unit tests or bypass compiler errors.

1. **Zero Dummy / Mock Logic in Production Code**:
   - Production classes (`src/main/java/` and `frontend/src/`) MUST NOT contain dummy `if (true)` checks, stubbed fallback return values, commented-out validation rules, or fake mock data.
   - All feature logic (e.g. User Ban verification, JWT signature parsing, Role Authorization, Session Elevation) MUST be fully implemented using production-grade domain entities, repositories, and collaborators.

2. **Zero Hardcoded URLs, URIs, Domains, Secrets & Magic Literals**:
   - **Absolute Ban on Hardcoded Literals**: Domain names (e.g. `codeconnect.dev`, `http://localhost:8080`), error URIs, gateway route patterns, JWT secret strings, Kafka topics, timeouts, and fallback credentials MUST NEVER be hardcoded as string constants or literals in Java or TypeScript source code.
   - **Pure `@ConfigurationProperties` Externalization**: Every configurable value MUST live exclusively in `application.yml` using standard Spring property placeholders: `${ENVIRONMENT_VARIABLE:defaultValue}` (e.g. `jwt-secret: ${JWT_SECRET:defaultSecret32BytesLongMinRequirement}`).
   - **Pure Data Holders Only**: `@ConfigurationProperties` Java classes/records MUST NOT contain compact constructor fallback logic, ternary expressions, or default assignments in Java code.

3. **Automated Enforcement via ArchUnit & Static Linters**:
   - All backend microservices execute automated **ArchUnit** tests in `src/test/java/.../arch/` that scan package boundaries and fail `mvn test` if:
     - Hardcoded HTTP/HTTPS domain string literals exist in production Java classes.
     - Controllers contain business logic or DB calls.
     - `@ConfigurationProperties` classes contain logic methods.

---

## 2. Mandatory GoF & OOAD Design Patterns Catalog

When authoring new microservices or implementing features, AI agents and engineers MUST leverage the appropriate design patterns from the start, rather than writing procedural scripts that require later refactoring.

### 2.1 The Command Pattern & Command Factory
* **When to use**: For transactional state mutations, business actions, adjudications, and multi-step state transitions.
* **Architecture Standard**:
  - Define a generic `DomainCommand<R>` interface:
    ```java
    public interface DomainCommand<R> {
        R execute();
    }
    ```
  - Implement concrete commands encapsulating all collaborators required to perform the action:
    - Example: `ApproveMentorApplicationCommand`, `RejectMentorApplicationCommand`, `UpdateLessonProgressCommand`.
  - Use a **Command Factory** (`*CommandFactory`) to inject Spring singleton dependencies (repositories, managers, publishers, mappers) and instantiate lightweight command instances with request-scoped parameters:
    ```java
    @Component
    @RequiredArgsConstructor
    public class MentorAdjudicationCommandFactory {
        private final MentorApprovalManager approvalManager;
        private final UserAccountElevator accountElevator;
        private final SessionElevationManager sessionElevationManager;
        private final MentorApprovalMapper mapper;
        private final ApplicationEventPublisher eventPublisher;

        public DomainCommand<MentorApprovalResponse> createApprovalCommand(String applicationId, String reviewerAdminEmail) {
            return new ApproveMentorApplicationCommand(
                applicationId, reviewerAdminEmail, approvalManager, accountElevator, sessionElevationManager, mapper, eventPublisher
            );
        }
    }
    ```
  - The Service facade simply invokes: `return commandFactory.createApprovalCommand(id, reviewer).execute();`.

### 2.2 The Strategy Pattern & Strategy Registry
* **When to use**: When an algorithm, evaluation logic, or business rule has multiple variations that must be selected dynamically at runtime without `if-else` or `switch` sprawl (Open-Closed Principle).
* **Architecture Standard**:
  - Define a clean strategy interface (e.g. `PrerequisiteEvaluationStrategy`, `ExecutionStrategy`, `SocraticPromptStrategy`):
    ```java
    public interface PrerequisiteEvaluationStrategy {
        PrerequisiteType getSupportedType();
        boolean isPrerequisiteSatisfied(String studentId, Lesson targetLesson, Track track, List<UserLessonProgress> studentProgress);
    }
    ```
  - Implement concrete strategy beans (e.g. `CompletedPrerequisiteStrategy`, `AlwaysUnlockedStrategy`, `LinearSequentialPrerequisiteStrategy`).
  - Use a **Strategy Registry** (or map injection) to dynamically resolve strategies in $O(1)$ time:
    ```java
    @Component
    public class PrerequisiteStrategyRegistry {
        private final Map<PrerequisiteType, PrerequisiteEvaluationStrategy> strategyMap;

        public PrerequisiteStrategyRegistry(List<PrerequisiteEvaluationStrategy> strategies) {
            this.strategyMap = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                    PrerequisiteEvaluationStrategy::getSupportedType,
                    Function.identity()
                ));
        }

        public PrerequisiteEvaluationStrategy resolve(PrerequisiteType type) {
            return Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported prerequisite type: " + type));
        }
    }
    ```

### 2.3 The Facade Pattern & Single Level of Abstraction (SLAP)
* **When to use**: For high-level Service interfaces (`*Service`, `*ServiceImpl`) orchestrating complex workflows.
* **Architecture Standard**:
  - Service classes must be clean, readable orchestrators (SLAP).
  - Never accumulate database queries, entity mappings, input validation, or Redis session manipulation inside the Service class.
  - Delegate to dedicated single-responsibility collaborators:
    - `collaborator/*Manager` or `collaborator/*Service`: Focused business domain logic (e.g. `LessonProgressManager`, `UserAccountElevator`).
    - `validator/*Validator`: Business & input invariants.
    - `mapper/*Mapper`: Bidirectional DTO $\leftrightarrow$ Entity conversion.
    - `command/*CommandFactory`: Transactional command execution.

### 2.4 The Chain of Responsibility (CoR) & Validation Pipelines
* **When to use**: When a request must pass through sequential validation, sanitization, or authentication stages.
* **Architecture Standard**:
  - Build sequential validation/sanitization steps where each step validates one invariant or passes to the next:
    - Code submission pipeline: `SourceCodeSizeFilter` $\rightarrow$ `MaliciousImportFilter` $\rightarrow$ `SyntaxSanityFilter`.
    - User registration pipeline: `EmailFormatFilter` $\rightarrow$ `UniqueEmailFilter` $\rightarrow$ `PasswordStrengthFilter`.

### 2.5 The Factory & Builder Patterns
* **When to use**: Complex object creation, aggregate assembly, and test fixture construction.
* **Architecture Standard**:
  - Use Lombok `@Builder` or explicit builder classes on complex domain entities and aggregates.
  - Use Factory classes when creation requires inspecting configuration or resolving runtime dependencies.

### 2.6 The Observer / Domain Event Pattern
* **When to use**: For decoupling core transactional business state changes from secondary side effects (notifications, audit logs, cache invalidation, cross-service propagation).
* **Architecture Standard**:
  - Fired by domain commands / services upon significant state transitions (e.g. `MentorApprovedEvent`, `LessonCompletedEvent`, `UserRegisteredEvent`).
  - Internal single-service events: Spring `ApplicationEventPublisher`.
  - Cross-service distributed events: Apache Kafka topics with immutable Java 21 `record` payloads.

### 2.7 The Filter / Interceptor Orchestrator Pattern (Security & Gateway)
* **When to use**: Authentication, authorization, HMAC request signing, and security boundary filters.
* **Architecture Standard**:
  - Filters (such as Spring Security `OncePerRequestFilter` or Spring Cloud Gateway `GlobalFilter`) must act strictly as thin orchestrators delegating to focused collaborators:
    1. `RouteAccessDecisionManager`: Pattern matching & RBAC access rules.
    2. `DownstreamHeaderEnricher`: Identity header injection and HMAC generation.
    3. `ProblemDetailsResponseWriter`: Serializing RFC 7807 ProblemDetail JSON responses.

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
├── application/
│   ├── dto/                 # Java 21 records: request/, response/
│   ├── mapper/              # Bidirectional entity <-> DTO mappers
│   ├── service/             # Domain business interfaces & impl/
│   └── validator/           # Input and business validation components
├── domain/
│   ├── enums/               # Domain Enums (UserRole, UserStatus, MentorApprovalStatus, etc. - NEVER in model/)
│   ├── exception/           # Custom Domain Exceptions
│   ├── model/               # Domain Entities / Documents (@Document)
│   └── repository/          # Spring Data repository interfaces
├── infrastructure/
│   ├── config/              # @Configuration, Security, Pure @ConfigurationProperties
│   └── session/             # Session managers, distributed caches (Redis)
└── presentation/
    ├── controller/          # @RestController (Strictly HTTP/REST & validation)
    └── exception/           # Centralized @RestControllerAdvice GlobalExceptionHandler
```

### 3.3 Strict Layering Rules
* **Controllers are Ultra-Thin**:
  - Controllers only do 3 things: (1) Receive `@Valid` request record, (2) Delegate to a Service Interface, (3) Return `ResponseEntity<ApiResponse<T>>`.
  - Zero business logic, zero entity instantiation, zero DB queries in controllers.
* **Interface-First Service Layer**:
  - Every service must have an `interface` (e.g., `UserService`) and implementation (`impl/UserServiceImpl`).
* **Service Classes Act as Orchestrators / Facades (No God Services)**:
  - Service implementation classes (`*ServiceImpl`) must act purely as high-level facades orchestrating business workflows at a single level of abstraction (SLAP).
  - **Never dump validation logic, entity-to-DTO mapping, session manipulation, or utility algorithms directly into the Service class** as monolithic code or an internal sprawl of private helper methods.
  - Delegate single-responsibility tasks to dedicated collaborators:
    - **Validators** (`application/validator/` or `domain/validator/`): Encapsulate domain and input validation rules (e.g., `MentorRegistrationValidator`).
    - **Mappers** (`application/mapper/`): Handle bidirectional conversions between Request DTOs, Entities, and Response DTOs (e.g., `UserMapper`).
    - **Infrastructure & Session Helpers** (`infrastructure/session/` or `infrastructure/helper/`): Encapsulate technical integrations such as Redis session attribute mutation and ID rotation (e.g., `SessionManager`).
  - The Service method simply chains and coordinates these focused collaborators in clean, intention-revealing lines:
    ```java
    @Override
    public Mono<UserResponse> signup(SignupRequest request, WebSession webSession) {
        String normalizedEmail = userMapper.normalizeEmail(request.email());

        return mentorValidator.validate(request)
            .then(ensureEmailIsAvailable(normalizedEmail))
            .then(saveNewUser(request, normalizedEmail))
            .flatMap(savedUser -> recordMentorAuditIfApplicable(request, savedUser)
                .then(sessionManager.establishSession(savedUser, webSession))
                .thenReturn(userMapper.toResponse(savedUser)));
    }
    ```
* **Anti-Corruption Layer (No Entity Leaks)**:
  - MongoDB `@Document` models must NEVER escape the Service layer. Controllers only see DTO records.
* **Zero N+1 Database Query Anti-Pattern (Batch Querying & In-Memory Stream Grouping)**:
  - Never execute database queries inside iterative loops (e.g. `for (Module m : modules) { lessonRepo.findByModuleId(m.getId()); }`).
  - Always fetch related documents in a single bulk query (e.g. `lessonRepository.findByTrackId(trackId)` or `lessonRepository.findByModuleIdIn(moduleIds)`).
  - Group and assemble relationships in-memory using Java 21 Streams and `Collectors.groupingBy(...)` in $O(N)$ total time, avoiding $N+1$ database roundtrips.
* **Constructor Injection Only**:
  - Field injection (`@Autowired private ...`) is strictly forbidden. All dependencies must be `private final` injected via constructors (or `@RequiredArgsConstructor`).
* **Zero Hardcoded Route Patterns, URLs/URIs, and Domain Assumptions (Pure `@ConfigurationProperties`)**:
  - All route path patterns (e.g., `/api/v1/admin/**`, `/api/v1/mentor/**`), URLs, URIs (such as error documentation base URIs, microservice endpoints, RFC 7807 error type URIs), domain strings, Kafka topic names, timeouts, thresholds, and administrative/fallback credentials MUST NEVER be hardcoded as constants or literals in Java code.
  - Path patterns, URLs, and URIs inevitably vary across environments (local, staging, production, edge proxies, ingress controllers, tenant prefixes). Hardcoding them in Java classes prevents environment-specific tuning, path rewrites, and zero-downtime reconfiguration.
  - Every configurable route pattern, URL, URI, endpoint, or system identifier must be externalized into `application.yml` using standard Spring property placeholders: `${ENVIRONMENT_VARIABLE:defaultValue}` (e.g., `admin-path-pattern: ${ADMIN_PATH_PATTERN:/api/v1/admin/**}`, `forbidden-error-uri: ${FORBIDDEN_ERROR_URI:${codeconnect.gateway.error-base-uri}/forbidden}`, `${DEFAULT_REVIEWER:SYSTEM}`, `${USER_SERVICE_URI:http://localhost:8081}`).
  - Security filters and routing components must evaluate route patterns dynamically through injected `@ConfigurationProperties` and Spring path matchers (e.g., `AntPathMatcher` or `PathPatternParser`).
  - **Filter Single Responsibility Principle (SRP) & Facade Pattern**:
    - Web filters (such as `RbacGatewayFilter`) must act strictly as thin orchestrators / facades at a Single Level of Abstraction (SLAP), delegating to focused collaborator components:
      1. **Route Access Decision Manager**: Route pattern matching and role authorization evaluation.
      2. **Downstream Header Enricher**: Request mutation and identity header decoration.
      3. **Problem Details Response Writer**: RFC 7807 ProblemDetail serialization, status setting, and reactive response stream writing.
    - Filters must NEVER mix route matching, session attribute inspection, header mutation, and low-level byte buffer response writing into a single monolithic class.
  - **CRITICAL MANDATE — Pure Data Holders Only**: NEVER EVER write any logic in classes or records annotated with `@ConfigurationProperties`.
    - No compact constructors with fallback logic.
    - No defaulting code, ternary expressions, or null checks in Java classes.
    - All defaults MUST live exclusively in `application.yml` using the `${ENVIRONMENT_VARIABLE:defaultValue}` syntax.
    - The `@ConfigurationProperties` class/record must remain a completely pure, dumb data container.
* **Centralized Global Exception Handling**:
  - Throw domain-specific exceptions. Centralized `@RestControllerAdvice` catches them and produces standardized RFC 7807 `ProblemDetail` or `ApiResponse<T>` with HTTP error codes, constructing error type URIs dynamically from injected `@ConfigurationProperties`.
* **Mandatory Enums in Dedicated Package (Zero Magic Strings in Backend & Frontend)**:
  - Whenever domain values, lifecycle statuses, account states, roles, or discrete categories are known (e.g., `PENDING`, `APPROVED`, `REJECTED`, `ACTIVE`, `BANNED`, `ROLE_STUDENT`, `ROLE_MENTOR`, `ROLE_ADMIN`, `HINGLISH`, `ENGLISH`), they **MUST** be modeled as type-safe Enums in both Backend (`com.codeconnect.<service>.domain.enums`) and Frontend (`frontend/src/domain/enums/`).
  - **Zero Hardcoded String Literals**: Never write magic string literals in entities, repositories, services, DTO records, or React UI components/event handlers (e.g., forbidding `setStatus("APPROVED")`, `setFormData({ role: "ROLE_STUDENT" })`, `findByStatus("PENDING")`, `status === "REJECTED"`).
  - Use `UserRole.STUDENT`, `UserRole.MENTOR`, `MentorApprovalStatus.PENDING` enums directly across both backend services and frontend React UI handlers.
  - Enums provide compile-time type safety, IDE refactoring support, exhaustive `switch` pattern matching, and self-documenting domain models.

---

## 4. Next.js 14 & Strict TypeScript Frontend Standards

### 4.1 Strict Type System & Java Backend Parity
* **Zero JavaScript / 100% Strict TypeScript**:
  - All files under `frontend/src/` must be `.ts` or `.tsx`.
  - `strict: true`, `noImplicitAny: true`, `strictNullChecks: true`.
  - The `any` type is completely banned. Use generics, discriminated unions, or `unknown` with narrowing type guards.
* **1:1 Type Parity with Java Backend Records**:
  - Every Java Request/Response record in backend microservices MUST have an exact TypeScript `interface` counterpart in `src/dto/` or `src/types/`.
  - Enums in backend `domain.enums` must be mirrored as TypeScript `enum`s or string literal union types in `src/types/`.

### 4.2 Clean Layered Frontend Architecture & Segregation
The frontend directory structure under `frontend/src/` strictly mirrors Clean Architecture principles, enforcing complete segregation between **Structure / Logic** and **UI / Presentation**:

```
frontend/src/
├── app/                  # Next.js App Router (Thin Page Shells & Layout Orchestrators)
├── client/               # HTTP / WebSocket API Clients (Fetch, Axios, STOMP infrastructure)
├── components/           # UI Atomic Primitives (Button, Modal, Card, Input) & Shared Layouts
├── controller/           # Custom React Hooks & View Models (UI Logic, State, Side-Effects)
├── domain/               # Frontend Domain Models, Entities & Value Objects (Pure Business Logic)
├── dto/                  # TypeScript Interfaces matching Backend DTO Records 1:1 (Request/Response)
├── lib/                  # Utilities, Formatters, Constants, Helper Mappers
├── presentation/         # Feature-Specific Presentational Assemblies (Pure UI Views, Zero API Calls)
├── service/              # Frontend Application Services / Facades (Orchestrate Client, DTOs & Storage)
├── types/                # Core TypeScript Contracts, Generic Enums & App Types
└── validator/            # Client-Side Form & Input Schema Validators (Yup, Zod, Custom Pipeline)
```

### 4.3 Strict Segregation: Structure / Logic vs. UI / Presentation

1. **Structural Layer (`controller/`, `service/`, `domain/`, `validator/`, `client/`)**:
   - **Zero JSX / Zero UI Code**: Structural files MUST NOT contain JSX elements (`<div>`, `<button>`), CSS class names, or presentation logic.
   - **Responsibilities**:
     - `controller/`: Custom React hooks handling state, `useEffect` hooks, event callbacks, and loading/error states.
     - `service/`: High-level frontend facades orchestrating API client calls, local storage/session tokens, and data mapping.
     - `domain/`: Pure domain entities and immutable business logic (e.g. calculating ascent streak, checking prerequisite eligibility).
     - `validator/`: Client-side input validation pipelines (e.g. email format, password strength, role validation).
     - `client/`: HTTP fetch adapters, header injection, and network error handling.

2. **UI / Presentation Layer (`app/`, `presentation/`, `components/`)**:
   - **Zero Business Logic & Zero Direct Fetch Calls**: Presentational components MUST NOT execute raw `fetch` calls, complex business algorithms, or direct API endpoint interactions.
   - **Responsibilities**:
     - `app/`: Next.js App Router pages act as ultra-thin shells that instantiate the appropriate `controller` hook and pass state/callbacks to the `presentation` view component.
     - `presentation/`: Feature-specific UI component assemblies receiving props from controllers.
     - `components/`: Atomic, reusable design system UI primitives (Buttons, Modals, Drawers, Cards).

### 4.4 SOLID & OOAD Principles in Frontend Development

* **Single Responsibility Principle (SRP)**:
  - `page.tsx`: Thin shell layout only.
  - `useSignupController.ts`: State management and event handlers only.
  - `SignupView.tsx`: JSX rendering and styling only.
  - `UserService.ts`: API call orchestration only.
  - `SignupValidator.ts`: Form validation rules only.
* **Open/Closed Principle (OCP)**:
  - Design UI primitives and presentation components to accept variant props, composition slots (`children`, `renderItem`), or theme tokens without editing component source code.
* **Liskov Substitution Principle (LSP)**:
  - Polymorphic component props must extend native HTML element attributes cleanly (e.g. `ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement>`). Subclasses or wrapper components must be 100% substitutable for their underlying base components.
* **Interface Segregation Principle (ISP)**:
  - Presentational components should take minimal, focused prop interfaces (e.g. `AvatarProps { imageUrl: string; name: string }`) instead of requiring the entire `UserResponse` DTO record.
* **Dependency Inversion Principle (DIP)**:
  - Controllers and Presentation components depend on abstract service interfaces or injected context providers (`AuthContext`, `ThemeContext`), never hardcoded fetch functions.

### 4.5 GoF & React Design Patterns in Frontend

* **Controller / Custom Hook Pattern**:
  - Encapsulates all React hooks (`useState`, `useReducer`, `useEffect`, `useCallback`) into dedicated controller files under `src/controller/`.
* **Facade Pattern (Frontend Service Layer)**:
  - `src/service/` classes act as unified facades for complex multi-step frontend workflows (e.g. `AuthService.login()` orchestrates calling `AuthClient.login()`, storing token in `SessionStore`, and populating `UserDomainModel`).
* **Adapter / Mapper Pattern**:
  - `src/lib/mappers/` transforms backend `ApiResponse<T>` DTO records (`src/dto/`) into frontend domain view models (`src/domain/`).
* **Strategy Pattern**:
  - Swappable UI strategies (e.g. `EditorExecutionStrategy` for Monaco Editor code execution by language, or `SocraticHintStrategy` for rendering different tutor prompt layouts).
* **Chain of Responsibility Pattern**:
  - Sequential client validation pipelines in `src/validator/` (e.g. `SanitizeInputStep` -> `ValidateEmailStep` -> `CheckPasswordStrengthStep`).
* **Observer / Event Pattern**:
  - Event listeners, WebSockets, STOMP subscriptions, and React Context providers notifying presentational subscribers of real-time state changes.

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

### 6.3 Kafka Event Contract & Enterprise Topic Taxonomy
* **Enterprise Topic Naming Taxonomy**: `<env>.<org/domain>.<service>.<entity>.<action-or-type>.<version>` — e.g.:
  - `${KAFKA_ENV:dev}.codeconnect.submission.code-execution.requested.v1`
  - `${KAFKA_ENV:dev}.codeconnect.sandbox.code-execution.completed.v1`
  - `${KAFKA_ENV:dev}.codeconnect.submission.code-execution.dlq.v1`
  - `${KAFKA_ENV:dev}.codeconnect.sandbox.code-execution.dlq.v1`
* **Versioning**: Events are strictly versioned (`v1`, `v2`). Never break a published event schema. Add new optional fields; never remove or rename existing ones.
* **Dead-Letter Queue (DLQ) & Poison Pill Recovery**: Every consumer must configure a Dead-Letter Topic (`*.dlq.v1`) with `DefaultErrorHandler` and `DeadLetterPublishingRecoverer` to capture failed or unparseable messages. Never silently drop a failed event or stall the partition pipeline.

### 6.4 Standard Event Envelope (CloudEvents-Style)

Every event published to Kafka MUST be wrapped in a generic `EventEnvelope<T>` carrying a standardized `EventHeader` for distributed tracing, auditability, and governance:

```java
public record EventHeader(
    String eventId,        // Unique UUID of this event instance (for consumer idempotency)
    String eventType,      // Concrete event name (e.g., "CodeExecutionRequestedEvent")
    String correlationId,  // Constant UUID shared across all hops of an end-to-end user request
    String sourceService,  // Originating microservice (e.g., "submission-service")
    String schemaVersion,  // e.g., "1.0"
    Instant timestamp,     // UTC instant when the event occurred
    String environment     // e.g., "production", "dev", "staging"
) {}

public record EventEnvelope<T>(
    EventHeader header,
    T payload              // Strongly-typed immutable Java 21 domain record
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
- [ ] **Design Patterns Applied Upfront**:
  - **Command & Factory Pattern**: Are transactional state mutations encapsulated in `DomainCommand<R>` instances created via `*CommandFactory`?
  - **Strategy & Registry Pattern**: Are swappable business rules encapsulated in Strategy interfaces resolved via a Strategy Registry (no `if/else` sprawl)?
  - **Facade & Collaborators (SLAP)**: Are service classes clean orchestrators delegating to dedicated collaborator components (`*Manager`, `*Validator`, `*Mapper`)?
  - **Chain of Responsibility**: Are sequential validation/sanitization steps organized as a pipeline?
  - **Filter SRP**: Are security and gateway filters thin orchestrators delegating to route decision managers, header enrichers, and problem writers?
- [ ] **Zero N+1 Queries**: Are hierarchical/related documents fetched in bulk and mapped in-memory using `Collectors.groupingBy(...)`?
- [ ] **No Entity Leaks**: Are DTO records used exclusively at the Controller interface?
- [ ] **Validation Present**: Are all incoming DTO records annotated with `@Valid` and constraints?
- [ ] **Interface + Impl**: Does every service follow the interface separation pattern?
- [ ] **Services as Facades**: Are services designed as clean facades delegating validation, mapping, and technical helpers to dedicated collaborator classes rather than accumulating private helper sprawl?
- [ ] **Constructor Injection**: Are all injected fields `private final` with constructor injection?
- [ ] **Zero Hardcoded Values**: Are URLs, topics, and constants mapped via `@ConfigurationProperties`?
- [ ] **Pure `@ConfigurationProperties`**: Are `@ConfigurationProperties` classes pure data holders with zero defaulting/fallback logic, delegating all defaults to `${ENV:default}` in `application.yml`?
- [ ] **Centralized Exceptions**: Are errors handled via `@RestControllerAdvice` returning structured responses?

### Domain-Driven Design
- [ ] **Bounded Context Respected**: Does this service only manage its own domain data?
- [ ] **Ubiquitous Language**: Do all class/method names match the domain glossary (Foothold, Ascent, Track, etc.)?
- [ ] **Aggregate Invariants**: Is all business logic enforced inside Aggregate Roots, not in Service layers?
- [ ] **Value Objects Immutable**: Are domain concepts like Email, Score, and Language modeled as immutable records?
- [ ] **Domain Events Past-Tense**: Are events named in the past tense and published for cross-service state transitions?

### Event-Driven Architecture
- [ ] **Consumer Idempotency**: Does every Kafka consumer guard against duplicate event processing?
- [ ] **DLQ Configured**: Is there a Dead-Letter Queue configured for every Kafka consumer routing to `*.dlq.v1`?
- [ ] **Event Envelope Standard**: Does every event published to Kafka implement `EventEnvelope<T>` with a standardized `EventHeader`?
- [ ] **No Synchronous Cross-Service State Changes**: Are all state mutations propagated asynchronously via events?
- [ ] **Correlation ID Propagated**: Is the `correlationId` threaded through all events and logs in the flow?

### 15-Factor Compliance
- [ ] **Config Externalized**: Are all environment-specific values in env variables or `@ConfigurationProperties`?
- [ ] **Stateless Process**: Does the service store zero in-memory state that would differ between instances?
- [ ] **Health & Metrics Exposed**: Are `/actuator/health` and metrics endpoints active and configured?
- [ ] **Graceful Shutdown**: Will the service drain in-flight work before terminating?
- [ ] **OpenAPI Spec Present**: Is the API contract documented in OpenAPI 3.x before or alongside the implementation?
- [ ] **Secrets Not in Code**: Are all credentials sourced from environment, not hardcoded or in `application.yml`?

### Distributed Infrastructure & Optimization (MongoDB, Redis & Kafka)
- [ ] **MongoDB Pool Tuning**: Are pool sizes (`min: 10`, `max: 100`), fail-fast timeouts (3s selection / wait), and wire compression (`snappy,zstd`) configured via `MongoPoolProperties` and `MongoPoolOptimizationConfig`?
- [ ] **Redis Connection Pooling & TCP Tuning**: Are Lettuce connection pools (`min-idle: 8`, `max-idle: 16`, `max-active: 32`, `max-wait: 1.5s`), low-latency `tcpNoDelay(true)`, and fail-fast timeouts configured?
- [ ] **Distributed Redis Caching**: Are hot read queries protected by `@Cacheable(sync = true)` with Jackson 2 JSON `record` serializers and namespaced keys (`codeconnect:cache:*`)?
- [ ] **Kafka Producer Durability & Batching**: Are `acks=all`, `enable.idempotence=true`, `max.in.flight.requests.per.connection=5`, `linger.ms=10`, `batch.size=16384`, and `snappy` compression configured?
- [ ] **Kafka Consumer Worker Safety & Non-Blocking DLQ**: Are `enable-auto-commit=false`, `ack-mode=RECORD`, tuned `max-poll-records`, and `DefaultErrorHandler` with exponential backoff and DLQ routing active?

### Frontend (Next.js 14 + Strict TypeScript)
- [ ] **100% Strict TypeScript**: Is the frontend 100% strict TypeScript with zero `any` types?
- [ ] **Strict Layering & Segregation**: Is structural logic (`controller/`, `service/`, `domain/`, `validator/`, `client/`) completely separated from presentation JSX (`app/`, `presentation/`, `components/`)?
- [ ] **Zero Business Logic in UI**: Are presentational components pure functions free of raw `fetch` calls or direct business algorithms?
- [ ] **Controller Hooks**: Are React hooks and View Models encapsulated inside `src/controller/` custom hooks?
- [ ] **DTO & Type Parity**: Does every backend Java DTO record have a 1:1 matching TypeScript interface in `src/dto/` or `src/types/`?
- [ ] **SOLID Principles**: Are SRP, OCP, LSP, ISP, and DIP strictly respected in frontend components and services?
- [ ] **Server Components Default**: Are Next.js Client Components (`'use client'`) used exclusively at interactive leaf components?

---

## 9. Distributed Infrastructure, Caching & Performance Optimization Standards

### 9.1 MongoDB Production Optimization & Connection Pooling

All MongoDB microservices (`user-service`, `curriculum-service`, `submission-service`) MUST implement production-grade connection pooling, fail-fast latency boundaries, and wire compression.

1. **Configuration via Pure `@ConfigurationProperties`**:
   - Every service configures a dedicated `MongoPoolProperties` record with zero logic or Java defaults.
   - All settings externalized to `application.yml` via `${ENV:default}`.
2. **Standard Sizing & Timeout Baseline**:
   ```yaml
   codeconnect:
     mongodb:
       min-pool-size: ${MONGO_MIN_POOL_SIZE:10}
       max-pool-size: ${MONGO_MAX_POOL_SIZE:100}
       max-wait-time-ms: ${MONGO_MAX_WAIT_TIME_MS:3000}
       max-idle-time-ms: ${MONGO_MAX_IDLE_TIME_MS:60000}
       max-life-time-ms: ${MONGO_MAX_LIFE_TIME_MS:1800000}
       maintenance-frequency-ms: ${MONGO_MAINTENANCE_MS:10000}
       connect-timeout-ms: ${MONGO_CONNECT_TIMEOUT_MS:3000}
       read-timeout-ms: ${MONGO_READ_TIMEOUT_MS:5000}
       server-selection-timeout-ms: ${MONGO_SERVER_SELECTION_TIMEOUT_MS:3000}
       compressors: ${MONGO_COMPRESSORS:snappy,zstd}
       write-concern: ${MONGO_WRITE_CONCERN:majority}
       read-preference: ${MONGO_READ_PREFERENCE:primaryPreferred}
   ```
3. **Defensive Wire Compression**:
   - `MongoPoolOptimizationConfig` registers a `MongoClientSettingsBuilderCustomizer` bean.
   - Classpath reflection checks (`Class.forName("org.xerial.snappy.Snappy")`) MUST be used defensively to only activate compressors when driver native libraries are loaded.

---

### 9.2 Redis Distributed Caching & Lettuce Connection Pooling

1. **Lettuce Connection Pooling (Apache Commons Pool 2)**:
   - Configured via `RedisPoolProperties` and `RedisOptimizationConfig` with `LettuceClientConfigurationBuilderCustomizer`.
   - Tuned pool boundaries: `min-idle: 8`, `max-idle: 16`, `max-active: 32`, `max-wait-ms: 1500`.
   - Low-latency TCP optimizations: `tcpNoDelay(true)` (disables Nagle algorithm), `keepAlive(true)`.
2. **Distributed Redis Caching Layer**:
   - **Annotation**: `@EnableCaching` on configuration class.
   - **Cache Manager**: `RedisCacheManager` configured with `RedisCacheConfiguration`.
   - **Serialization**: `GenericJackson2JsonRedisSerializer` configured with an `ObjectMapper` registering `JavaTimeModule` and `activateDefaultTyping(NON_FINAL)` to deserialize Java 21 `record` DTO types without `ClassCastException`.
   - **Cache Stampede Prevention**: Always use `@Cacheable(value = "...", sync = true)` on read queries to prevent thundering herd against MongoDB.
   - **Per-Domain TTLs**: Managed via `CacheTtlProperties` (e.g., tracks: 15m, lessons: 10m, users: 15m, mentors: 5m).
   - **Explicit Cache Eviction**: All state-mutating commands (e.g. updating a lesson, publishing a track, approving a mentor) MUST declare `@CacheEvict(value = "...", allEntries = true)`.

---

### 9.3 Enterprise Apache Kafka Production Architecture

1. **Standardized Topic Naming Hierarchy**:
   $$\mathbf{\langle env \rangle.\langle domain \rangle.\langle service \rangle.\langle entity \rangle.\langle action\text{-}or\text{-}type \rangle.\langle version \rangle}$$
   - Execution Request: `${KAFKA_ENV:dev}.codeconnect.submission.code-execution.requested.v1`
   - Execution Completed: `${KAFKA_ENV:dev}.codeconnect.sandbox.code-execution.completed.v1`
   - Dead-Letter Topics: `${KAFKA_ENV:dev}.codeconnect.<service>.<entity>.dlq.v1`
2. **Standardized CloudEvents `EventEnvelope<T>` Contract**:
   - All events MUST implement `EventEnvelope<T>(EventHeader header, T payload)`.
   - `EventHeader` contains: `eventId`, `eventType`, `correlationId`, `sourceService`, `schemaVersion`, `timestamp`, and `environment`.
3. **Producer Production Profile**:
   - **Durability & Zero Loss**: `acks: all`, `retries: Integer.MAX_VALUE`, `enable.idempotence: true`, `max.in.flight.requests.per.connection: 5`.
   - **Delivery Bounds**: `delivery.timeout.ms: 180000`, `request.timeout.ms: 120000`, `max.block.ms: 60000`.
   - **High-Throughput Batching**: `batch.size: 16384` (16 KB), `linger.ms: 10` (10ms batch coalescing), `buffer.memory: 33554432` (32 MB), `compression.type: snappy`.
   - **Socket & Network**: `send.buffer.bytes: 131072` (128 KB), `receive.buffer.bytes: 131072` (128 KB), `connections.max.idle.ms: 540000`.
   - **Telemetry**: `enable.jmx: true`, `metrics.recording.level: INFO`.
4. **Consumer & Heavy Worker Production Profile**:
   - **Offset Safety**: `enable-auto-commit: false`, `ack-mode: RECORD`, `auto-offset-reset: earliest`, `concurrency: 3`.
   - **Heavy-Workload Polling (Sandbox Runner)**: `max-poll-records: 10` and `max.poll.interval.ms: 600000` (10 minutes) to eliminate rebalance storms during long Docker execution lifecycles.
   - **Error Handling & Dead-Letter Queue (DLQ)**: `DefaultErrorHandler` with exponential backoff (1s $\rightarrow$ 2s $\rightarrow$ 4s; max 3 attempts) coupled with `DeadLetterPublishingRecoverer` routing poison pills to `.dlq.v1`.


