package com.scalecart.payment.webhook;

import com.scalecart.payment.dto.WebhookPayload;
import com.scalecart.payment.entity.Payment;
import com.scalecart.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WebhookProcessor {

    private static final Logger log =
            LoggerFactory.getLogger(WebhookProcessor.class);

    private final PaymentService paymentService;

    public WebhookProcessor(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Runs after the HTTP 200 is already sent
    @Async("webhookTaskExecutor")
    public void processPaymentWebhook(WebhookPayload payload) {
        try {
            log.info("Processing webhook async: event={}, paymentId={}",
                    payload.getEvent(), payload.getPaymentId());

            switch (payload.getEvent()) {

                case "payment.success" -> {
                    Payment completed = paymentService.completePayment(
                            payload.getPaymentId(),
                            payload.getGatewayTransactionId(),
                            payload.getRawPayload()
                    );
                    log.info("Payment completed successfully: id={}, orderId={}",
                            completed.getId(), completed.getOrderId());
                }

                case "payment.failed" -> {
                    Payment failed = paymentService.failPayment(
                            payload.getPaymentId(),
                            payload.getFailureReason(),
                            payload.getRawPayload()
                    );
                    log.warn("Payment failed: id={}, reason={}",
                            failed.getId(), failed.getFailureReason());
                }

                default -> log.warn("Unknown webhook event type: {}",
                        payload.getEvent());
            }

        } catch (Exception e) {
            // @Async exceptions do not propagate to the HTTP caller
            log.error("Error processing webhook for paymentId={}: {}",
                    payload.getPaymentId(), e.getMessage(), e);
        }
    }
}
