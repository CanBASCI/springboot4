package com.org.springboot4.events;

import java.util.UUID;

/**
 * Event published when an order is canceled.
 * user-service listens to this to release reserved credit (compensation).
 */
public record OrderCanceledEvent(
    UUID orderId,
    UUID userId,
    Long amount
) {
}

