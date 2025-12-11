package com.org.springboot4.orderservice.event;

import com.org.springboot4.events.UserCreditReservationFailedEvent;
import com.org.springboot4.events.UserCreditReservedEvent;
import com.org.springboot4.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listens to credit events from user-service.
 * Updates order status: UserCreditReservedEvent -> CONFIRMED, UserCreditReservationFailedEvent -> CANCELED
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreditEventListeners {
    
    private final OrderService orderService;
    
    @KafkaListener(topics = "user.credit-reserved", groupId = "order-service-group")
    @Transactional
    public void handleCreditReserved(UserCreditReservedEvent event) {
        log.info("Received UserCreditReservedEvent: orderId={}, userId={}, amount={}", 
                event.orderId(), event.userId(), event.amount());
        
        try {
            orderService.confirmOrder(event.orderId());
        } catch (Exception e) {
            log.error("Error confirming order orderId={}", event.orderId(), e);
        }
    }
    
    @KafkaListener(topics = "user.credit-reservation-failed", groupId = "order-service-group")
    @Transactional
    public void handleCreditReservationFailed(UserCreditReservationFailedEvent event) {
        log.info("Received UserCreditReservationFailedEvent: orderId={}, userId={}, amount={}, reason={}", 
                event.orderId(), event.userId(), event.amount(), event.reason());
        
        try {
            orderService.cancelOrder(event.orderId());
        } catch (Exception e) {
            log.error("Error canceling order orderId={}", event.orderId(), e);
        }
    }
}

