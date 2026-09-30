package com.codeconnect.sandbox.infrastructure.messaging.idempotency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-process idempotency guard for Kafka consumer deduplication.
 *
 * <p>Prevents duplicate processing when Kafka delivers the same message more than once
 * (at-least-once delivery guarantee). Uses a thread-safe {@link ConcurrentHashMap} keyed on
 * {@code eventId} with an expiry TTL.
 *
 * <p><strong>Production Note:</strong> For multi-pod deployments, replace this with a Redis-backed
 * check using {@code SET eventId NX EX <ttl>} to make deduplication cluster-wide.
 */
@Slf4j
@Component
public class EventIdempotencyGuard {

    private static final long TTL_SECONDS = 3600L; // 1 hour dedup window

    private final Map<String, Instant> processedEventIds = new ConcurrentHashMap<>();

    /**
     * Returns {@code true} if the given {@code eventId} has already been processed within the TTL window,
     * meaning the current message is a duplicate and should be skipped.
     *
     * @param eventId the unique event identifier from {@code EventHeader}
     * @return {@code true} if already processed (duplicate), {@code false} if new
     */
    public boolean isDuplicate(String eventId) {
        evictExpiredEntries();

        if (processedEventIds.containsKey(eventId)) {
            log.warn("Idempotency guard: duplicate eventId={} detected — skipping reprocessing", eventId);
            return true;
        }
        return false;
    }

    /**
     * Marks the given {@code eventId} as successfully processed.
     *
     * @param eventId the unique event identifier to register
     */
    public void markProcessed(String eventId) {
        processedEventIds.put(eventId, Instant.now().plusSeconds(TTL_SECONDS));
    }

    private void evictExpiredEntries() {
        Instant now = Instant.now();
        processedEventIds.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
