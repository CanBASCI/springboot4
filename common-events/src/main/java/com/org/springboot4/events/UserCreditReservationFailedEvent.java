package com.org.springboot4.events;

import java.util.UUID;

/**
 * Event published when user credit reservation fails (insufficient balance).
 * order-service listens to this to cancel the order.
 */
public record UserCreditReservationFailedEvent(
    UUID orderId,
    UUID userId,
    Long amount,
    String reason
) {
}

