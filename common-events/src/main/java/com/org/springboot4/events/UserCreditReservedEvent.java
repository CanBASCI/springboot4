package com.org.springboot4.events;

import java.util.UUID;

/**
 * Event published when user credit is successfully reserved.
 * order-service listens to this to confirm the order.
 */
public record UserCreditReservedEvent(
    UUID orderId,
    UUID userId,
    Long amount
) {
}

