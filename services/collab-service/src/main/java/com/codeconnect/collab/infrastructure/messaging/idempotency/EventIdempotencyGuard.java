package com.codeconnect.collab.infrastructure.messaging.idempotency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-process idempotency guard for Kafka consumer deduplication in collab-service.
 *
 * <p>Prevents double-broadcast to WebSocket clients when Kafka delivers the same
 * execution-result message more than once (at-least-once delivery guarantee).
 *
 * <p><strong>Production Note:</strong> For multi-pod deployments replace with a Redis-backed
 * check using {@code SET eventId NX EX <ttl>} to make deduplication cluster-wide.
 */
@Slf4j
@Component
public class EventIdempotencyGuard {

    private static final long TTL_SECONDS = 3600L;

    private final Map<String, Instant> processedEventIds = new ConcurrentHashMap<>();

    /**
     * Returns {@code true} if the given {@code eventId} has already been processed within the TTL window.
     */
    public boolean isDuplicate(String eventId) {
        evictExpiredEntries();

        if (processedEventIds.containsKey(eventId)) {
            log.warn("Idempotency guard: duplicate eventId={} detected in collab-service — skipping broadcast", eventId);
            return true;
        }
        return false;
    }

    /**
     * Marks the given {@code eventId} as successfully processed.
     */
    public void markProcessed(String eventId) {
        processedEventIds.put(eventId, Instant.now().plusSeconds(TTL_SECONDS));
    }

    private void evictExpiredEntries() {
        Instant now = Instant.now();
        processedEventIds.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
