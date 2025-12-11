package com.org.springboot4.events;

/**
 * Order status enum for saga orchestration
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

