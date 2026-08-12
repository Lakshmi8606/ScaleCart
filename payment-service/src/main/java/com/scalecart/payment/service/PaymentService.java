package com.scalecart.payment.service;

import com.scalecart.payment.dto.PaymentInitiateRequest;
import com.scalecart.payment.dto.PaymentResponse;
import com.scalecart.payment.entity.Payment;
import com.scalecart.payment.entity.PaymentStatus;
import com.scalecart.payment.event.OrderPaidEvent;
import com.scalecart.payment.rabbitmq.PaymentConfirmedEvent;
import com.scalecart.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, OrderPaidEvent> kafkaTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${kafka.topic.order-paid}")
    private String orderPaidTopic;

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.routing-key.order-paid}")
    private String orderPaidRoutingKey;

    public PaymentService(PaymentRepository paymentRepository,
                          KafkaTemplate<String, OrderPaidEvent> kafkaTemplate,
                          RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public PaymentResponse initiatePayment(PaymentInitiateRequest request) {

        // ── IDEMPOTENCY CHECK ──────────────────────────────────────
        // Before doing ANYTHING, check if we've seen this key before
        var existing = paymentRepository
                .findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            log.info("Duplicate payment request detected for " +
                            "idempotency_key={}, returning existing payment id={}",
                    request.getIdempotencyKey(), existing.get().getId());

            // Return EXACT same response as the original request
            // Client gets correct response regardless of how many times they retry
            PaymentResponse response = toResponse(existing.get());
            response.setDuplicate(true);
            return response;
        }
        // ── END IDEMPOTENCY CHECK ──────────────────────────────────

        // New payment - create and persist
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setGateway(request.getGateway());
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);

        log.info("Payment initiated: id={}, orderId={}, amount={}, gateway={}",
                saved.getId(), saved.getOrderId(),
                saved.getAmount(), saved.getGateway());

        PaymentResponse response = toResponse(saved);
        response.setDuplicate(false);
        return response;
    }

    // Called when webhook confirms payment success (Day 12)
    @Transactional
    public Payment completePayment(Long paymentId,
                                   String gatewayTransactionId,
                                   String gatewayResponse) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found: " + paymentId));

        // State machine validation - can only complete a PENDING payment
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot complete payment in status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setGatewayTransactionId(gatewayTransactionId);
        payment.setGatewayResponse(gatewayResponse);

        Payment completed = paymentRepository.save(payment);

        // Publish order.paid event to Kafka
        publishOrderPaidEvent(completed);

        // Also publish to RabbitMQ for Order Service status update
        publishToRabbitMQ(completed);

        return completed;
    }

    @Transactional
    public Payment failPayment(Long paymentId, String reason,
                               String gatewayResponse) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot fail payment in status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setGatewayResponse(gatewayResponse);

        return paymentRepository.save(payment);
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found for order: " + orderId));
        return toResponse(payment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private void publishOrderPaidEvent(Payment payment) {
        OrderPaidEvent event = new OrderPaidEvent(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getGatewayTransactionId(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                orderPaidTopic,
                payment.getOrderId().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish order.paid event " +
                        "for orderId={}", payment.getOrderId());
            } else {
                log.info("Published order.paid event for orderId={}",
                        payment.getOrderId());
            }
        });
    }

    // Publish payment confirmation to RabbitMQ
    private void publishToRabbitMQ(Payment payment) {

        PaymentConfirmedEvent event = new PaymentConfirmedEvent(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getGatewayTransactionId(),
                LocalDateTime.now()
        );

        // Converts event to JSON and sends it to the exchange
        // with the payment.paid routing key.
        // The exchange then routes it to the order-status-update queue.
        rabbitTemplate.convertAndSend(
                paymentExchange,
                orderPaidRoutingKey,
                event
        );

        log.info("Published PaymentConfirmedEvent to RabbitMQ " +
                "for orderId={}", payment.getOrderId());
    }

    // Entity → DTO mapper
    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setIdempotencyKey(payment.getIdempotencyKey());
        response.setStatus(payment.getStatus());
        response.setGateway(payment.getGateway());
        response.setGatewayTransactionId(payment.getGatewayTransactionId());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }
}

