package com.org.springboot4.userservice.event;

import com.org.springboot4.events.OrderCanceledEvent;
import com.org.springboot4.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listens to OrderCanceledEvent and releases reserved credit (compensation).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCanceledEventListener {
    
    private final UserService userService;
    
    @KafkaListener(topics = "order.canceled", groupId = "user-service-group")
    @Transactional
    public void handleOrderCanceled(OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent: orderId={}, userId={}, amount={}", 
                event.orderId(), event.userId(), event.amount());
        
        try {
            userService.releaseCredit(event.userId(), event.amount());
            log.info("Credit released successfully for orderId={}, userId={}, amount={}", 
                    event.orderId(), event.userId(), event.amount());
        } catch (Exception e) {
            log.error("Error releasing credit for orderId={}, userId={}, amount={}", 
                    event.orderId(), event.userId(), event.amount(), e);
        }
    }
}

