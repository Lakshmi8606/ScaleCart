package com.scalecart.notification.consumer;

import com.scalecart.notification.event.OrderCreatedEvent;
import com.scalecart.notification.service.EmailNotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    private final EmailNotificationService emailNotificationService;

    public OrderEventConsumer(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @KafkaListener(
            topics = "order.created",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreated(
            ConsumerRecord<String, OrderCreatedEvent> record) {

        OrderCreatedEvent event = record.value();

        log.info("Consumed order.created event: orderId={}, partition={}, offset={}",
                event.getOrderId(),
                record.partition(),
                record.offset());

        // In a real system you'd fetch the user's email from Auth Service
        // For now we construct a placeholder email from userId
        // On Day 19 (API Gateway) we'll see how inter-service calls work
        String recipientEmail = "user" + event.getUserId() + "@example.com";

        // If this throws, DefaultErrorHandler catches it → retries → DLQ
        emailNotificationService.sendOrderConfirmationEmail(event, recipientEmail);

        log.info("Successfully processed order.created event for orderId={}",
                event.getOrderId());
    }
}