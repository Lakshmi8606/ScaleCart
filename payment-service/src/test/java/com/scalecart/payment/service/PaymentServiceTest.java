package com.scalecart.payment.service;

import com.scalecart.payment.dto.PaymentInitiateRequest;
import com.scalecart.payment.dto.PaymentResponse;
import com.scalecart.payment.entity.Payment;
import com.scalecart.payment.entity.PaymentStatus;
import com.scalecart.payment.event.OrderPaidEvent;
import com.scalecart.payment.rabbitmq.PaymentConfirmedEvent;
import com.scalecart.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, OrderPaidEvent> kafkaTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentInitiateRequest validRequest;
    private Payment existingPayment;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                paymentService,
                "orderPaidTopic",
                "order.paid"
        );

        ReflectionTestUtils.setField(
                paymentService,
                "paymentExchange",
                "payment.exchange"
        );

        ReflectionTestUtils.setField(
                paymentService,
                "orderPaidRoutingKey",
                "order.paid"
        );

        validRequest = new PaymentInitiateRequest();
        validRequest.setOrderId(1L);
        validRequest.setUserId(1L);
        validRequest.setAmount(new BigDecimal("80498.00"));
        validRequest.setIdempotencyKey("unique-key-abc-123");
        validRequest.setGateway("razorpay");

        existingPayment = new Payment();
        existingPayment.setOrderId(1L);
        existingPayment.setUserId(1L);
        existingPayment.setAmount(new BigDecimal("80498.00"));
        existingPayment.setIdempotencyKey("unique-key-abc-123");
        existingPayment.setStatus(PaymentStatus.PENDING);
        existingPayment.setGateway("razorpay");
    }

    @Test
    @DisplayName("Should create new payment when idempotency key is fresh")
    void initiatePayment_NewKey_CreatesPayment() {

        when(paymentRepository.findByIdempotencyKey(
                "unique-key-abc-123"))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        PaymentResponse result =
                paymentService.initiatePayment(validRequest);

        assertThat(result).isNotNull();

        assertThat(result.getStatus())
                .isEqualTo(PaymentStatus.PENDING);

        assertThat(result.isDuplicate())
                .isFalse();

        verify(paymentRepository)
                .save(any(Payment.class));
    }

    @Test
    @DisplayName("Should return existing payment when idempotency key already used")
    void initiatePayment_DuplicateKey_ReturnsExisting() {

        when(paymentRepository.findByIdempotencyKey(
                "unique-key-abc-123"))
                .thenReturn(Optional.of(existingPayment));

        PaymentResponse result =
                paymentService.initiatePayment(validRequest);

        assertThat(result).isNotNull();

        assertThat(result.isDuplicate())
                .isTrue();

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    @DisplayName("Should return same payment ID for duplicate")
    void initiatePayment_DuplicateKey_ReturnsSameId() {

        when(paymentRepository.findByIdempotencyKey(anyString()))
                .thenReturn(Optional.of(existingPayment));

        PaymentResponse result =
                paymentService.initiatePayment(validRequest);

        assertThat(result).isNotNull();

        assertThat(result.isDuplicate())
                .isTrue();
    }

    @Test
    @DisplayName("Should complete PENDING payment and publish Kafka and RabbitMQ events")
    void completePayment_PendingPayment_Succeeds() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(existingPayment));

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(existingPayment);

        when(kafkaTemplate.send(
                anyString(),
                anyString(),
                any(OrderPaidEvent.class)
        )).thenReturn(
                CompletableFuture.completedFuture(null)
        );

        Payment result = paymentService.completePayment(
                1L,
                "rzp_txn_abc123",
                "{\"status\":\"captured\"}"
        );

        assertThat(result.getStatus())
                .isEqualTo(PaymentStatus.COMPLETED);

        assertThat(result.getGatewayTransactionId())
                .isEqualTo("rzp_txn_abc123");

        assertThat(result.getGatewayResponse())
                .contains("captured");

        verify(kafkaTemplate)
                .send(
                        eq("order.paid"),
                        anyString(),
                        any(OrderPaidEvent.class)
                );

        verify(rabbitTemplate)
                .convertAndSend(
                        eq("payment.exchange"),
                        eq("order.paid"),
                        any(PaymentConfirmedEvent.class)
                );
    }

    @Test
    @DisplayName("Should throw exception when completing already COMPLETED payment")
    void completePayment_AlreadyCompleted_ThrowsException() {

        existingPayment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(existingPayment));

        assertThatThrownBy(() ->
                paymentService.completePayment(
                        1L,
                        "rzp_txn_123",
                        "{}"
                ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Cannot complete payment in status"
                );

        verify(paymentRepository, never())
                .save(any(Payment.class));

        verify(kafkaTemplate, never())
                .send(
                        anyString(),
                        anyString(),
                        any(OrderPaidEvent.class)
                );

        verify(rabbitTemplate, never())
                .convertAndSend(
                        anyString(),
                        anyString(),
                        any(PaymentConfirmedEvent.class)
                );
    }

    @Test
    @DisplayName("Should fail PENDING payment with reason")
    void failPayment_PendingPayment_Succeeds() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(existingPayment));

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(existingPayment);

        Payment result = paymentService.failPayment(
                1L,
                "Insufficient funds",
                "{\"error\":\"card_declined\"}"
        );

        assertThat(result.getStatus())
                .isEqualTo(PaymentStatus.FAILED);

        assertThat(result.getFailureReason())
                .isEqualTo("Insufficient funds");
    }

    @Test
    @DisplayName("Should throw exception when failing already FAILED payment")
    void failPayment_AlreadyFailed_ThrowsException() {

        existingPayment.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(existingPayment));

        assertThatThrownBy(() ->
                paymentService.failPayment(
                        1L,
                        "reason",
                        "{}"
                ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Cannot fail payment in status"
                );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void completePayment_NotFound_ThrowsException() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                paymentService.completePayment(
                        999L,
                        "txn",
                        "{}"
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Payment not found: 999"
                );
    }
}
