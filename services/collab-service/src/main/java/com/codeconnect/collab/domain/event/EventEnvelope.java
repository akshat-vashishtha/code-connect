package com.codeconnect.collab.domain.event;

/**
 * Generic CloudEvents-compliant envelope wrapping an EventHeader and domain Payload.
 */
public record EventEnvelope<T>(
    EventHeader header,
    T payload
) {}
