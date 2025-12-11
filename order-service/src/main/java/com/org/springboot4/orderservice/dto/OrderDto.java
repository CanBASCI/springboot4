package com.org.springboot4.orderservice.dto;

import com.org.springboot4.events.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private UUID userId;
    private Long amount;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

