package com.codeconnect.submission.domain.event;

/**
 * Generic CloudEvents-compliant envelope wrapping an EventHeader and domain Payload.
 *
 * @param <T> The concrete domain payload type.
 */
public record EventEnvelope<T>(
    EventHeader header,
    T payload
) {}
