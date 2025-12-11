package com.org.springboot4.userservice.event;

import com.org.springboot4.events.OrderCreatedEvent;
import com.org.springboot4.events.UserCreditReservationFailedEvent;
import com.org.springboot4.events.UserCreditReservedEvent;
import com.org.springboot4.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listens to OrderCreatedEvent and reserves credit.
 * Publishes UserCreditReservedEvent on success, UserCreditReservationFailedEvent on failure.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {
    
    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @KafkaListener(topics = "order.created", groupId = "user-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: orderId={}, userId={}, amount={}", 
                event.orderId(), event.userId(), event.amount());
        
        try {
            boolean reserved = userService.reserveCredit(event.userId(), event.amount());
            
            if (reserved) {
                log.info("Credit reserved successfully for orderId={}, userId={}, amount={}", 
                        event.orderId(), event.userId(), event.amount());
                
                UserCreditReservedEvent creditReservedEvent = new UserCreditReservedEvent(
                        event.orderId(),
                        event.userId(),
                        event.amount()
                );
                
                kafkaTemplate.send("user.credit-reserved", creditReservedEvent);
            } else {
                log.warn("Insufficient balance for orderId={}, userId={}, amount={}", 
                        event.orderId(), event.userId(), event.amount());
                
                UserCreditReservationFailedEvent failedEvent = new UserCreditReservationFailedEvent(
                        event.orderId(),
                        event.userId(),
                        event.amount(),
                        "Insufficient balance"
                );
                
                kafkaTemplate.send("user.credit-reservation-failed", failedEvent);
            }
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for orderId={}", event.orderId(), e);
            
            UserCreditReservationFailedEvent failedEvent = new UserCreditReservationFailedEvent(
                    event.orderId(),
                    event.userId(),
                    event.amount(),
                    "Error: " + e.getMessage()
            );
            
            kafkaTemplate.send("user.credit-reservation-failed", failedEvent);
        }
    }
}

