# Deferred Work Items

## Deferred from: code review (Epic 1 Codebase Review - 2026-09-23)
- **SessionElevationManager Redis Scan Optimization**: Replace `redisTemplate.keys(SESSIONS_PATTERN)` with cursor-based `SCAN` or Spring Session `FindByIndexNameSessionRepository` index lookup for production scale (>100k active sessions) to prevent Redis event-loop blocking.
- **LanguageDetector Heuristic Disambiguation**: Refine Hinglish vocabulary heuristics (e.g. separating universal slang "bro" from characteristic Hinglish markers "bhai/yaar") or require a minimum score threshold (>1 match) during Epic 4 Socratic AI prompt integration.
