package com.org.springboot4.orderservice.service;

import com.org.springboot4.events.OrderCreatedEvent;
import com.org.springboot4.events.OrderStatus;
import com.org.springboot4.orderservice.domain.Order;
import com.org.springboot4.orderservice.dto.CreateOrderRequest;
import com.org.springboot4.orderservice.dto.OrderDto;
import com.org.springboot4.orderservice.mapper.OrderMapper;
import com.org.springboot4.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Order service: creates orders and publishes events, handles saga completion
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // Creates order with PENDING status and publishes OrderCreatedEvent
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(OrderStatus.PENDING)
                .build();
        
        Order saved = orderRepository.save(order);
        log.info("Created order: id={}, userId={}, amount={}", saved.getId(), saved.getUserId(), saved.getAmount());
        
        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getAmount()
        );
        
        kafkaTemplate.send("order.created", event);
        log.info("Published OrderCreatedEvent for orderId={}", saved.getId());
        
        return orderMapper.toDto(saved);
    }
    
    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return orderMapper.toDto(order);
    }
    
    // Updates order status to CONFIRMED
    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        log.info("Order confirmed: orderId={}", orderId);
    }
    
    // Updates order status to CANCELED
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        log.info("Order canceled: orderId={}", orderId);
    }
}

