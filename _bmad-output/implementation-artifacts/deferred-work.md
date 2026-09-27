# Deferred Work Items

## Deferred from: code review (Epic 1 Codebase Review - 2026-09-23)
- **SessionElevationManager Redis Scan Optimization**: Replace `redisTemplate.keys(SESSIONS_PATTERN)` with cursor-based `SCAN` or Spring Session `FindByIndexNameSessionRepository` index lookup for production scale (>100k active sessions) to prevent Redis event-loop blocking.
- **LanguageDetector Heuristic Disambiguation**: Refine Hinglish vocabulary heuristics (e.g. separating universal slang "bro" from characteristic Hinglish markers "bhai/yaar") or require a minimum score threshold (>1 match) during Epic 4 Socratic AI prompt integration.

## Deferred from: code review of spec-2-1 (2026-09-27)
- **Lombok boilerplate on domain models**: All `curriculum-service` domain models use hand-rolled builders/getters/setters as a workaround for Maven annotation processor issues; clean up once build pipeline confirmed stable.
- **Missing `@CompoundIndex` on `student_progress`**: `findByUserIdAndTrackId` will full-scan at scale; add `@CompoundIndex(def = "{'userId': 1, 'trackId': 1}", unique = true)` to `StudentProgressDocument`. Deferred to Epic 7 performance hardening pass.
- **`createModule` bare `orElseThrow()`**: `trackRepository.findById(...).orElseThrow()` throws `NoSuchElementException` with no message; replace with `orElseThrow(() -> new ResourceNotFoundException(...))`. Unreachable in normal flow as validator runs first; clean up in Story 2.3.
