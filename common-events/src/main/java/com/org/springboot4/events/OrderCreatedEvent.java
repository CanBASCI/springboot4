package com.org.springboot4.events;

import java.util.UUID;

/**
 * Event published when an order is created.
 * This triggers the saga: user-service listens to this event to reserve credit.
 */
public record OrderCreatedEvent(
    UUID orderId,
    UUID userId,
    Long amount
) {
}

