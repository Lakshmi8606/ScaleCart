package com.scalecart.order.rabbitmq;

import com.scalecart.order.entity.Order;
import com.scalecart.order.entity.OrderStatus;
import com.scalecart.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final OrderRepository orderRepository;

    public PaymentEventConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // @RabbitListener: listens to this queue
    // Spring calls this method for every message that arrives
    @RabbitListener(queues = "${rabbitmq.queue.order-status-update}")
    @Transactional
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {

        log.info("RabbitMQ message received: PaymentConfirmedEvent " +
                        "for orderId={}, paymentId={}",
                event.getOrderId(), event.getPaymentId());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + event.getOrderId()));

        // Guard: only update if currently PENDING
        // Idempotency protection — if this message is delivered twice
        // (at-least-once delivery), second processing is a no-op
        if (order.getStatus() != OrderStatus.PENDING) {
            log.warn("Order {} is already in status {} — skipping update",
                    event.getOrderId(), order.getStatus());
            return;
        }

        // PENDING → CONFIRMED
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        log.info("Order {} status updated: PENDING → CONFIRMED " +
                        "(triggered by payment {})",
                event.getOrderId(), event.getPaymentId());
    }
}