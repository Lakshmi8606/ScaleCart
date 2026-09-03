package com.scalecart.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalecart.payment.dto.WebhookPayload;
import com.scalecart.payment.security.WebhookSignatureValidator;
import com.scalecart.payment.webhook.WebhookProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class WebhookController {

    private static final Logger log =
            LoggerFactory.getLogger(WebhookController.class);

    private final WebhookSignatureValidator signatureValidator;
    private final WebhookProcessor webhookProcessor;
    private final ObjectMapper objectMapper;

    public WebhookController(WebhookSignatureValidator signatureValidator,
                             WebhookProcessor webhookProcessor,
                             ObjectMapper objectMapper) {
        this.signatureValidator = signatureValidator;
        this.webhookProcessor = webhookProcessor;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Razorpay-Signature",
                    required = false) String signature) {

        log.info("Webhook received, validating signature...");

        if (signature == null || signature.isBlank()) {
            log.warn("Webhook rejected: missing signature header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing signature");
        }

        if (!signatureValidator.isValidSignature(rawBody, signature)) {
            log.warn("Webhook rejected: invalid signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid signature");
        }

        WebhookPayload payload;
        try {
            payload = parseWebhookBody(rawBody);
        } catch (Exception e) {
            log.error("Failed to parse webhook body: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid webhook payload");
        }

        webhookProcessor.processPaymentWebhook(payload);

        log.info("Webhook acknowledged for event={}, paymentId={}",
                payload.getEvent(), payload.getPaymentId());

        return ResponseEntity.ok("Webhook received");
    }

    private WebhookPayload parseWebhookBody(String rawBody) throws Exception {

        JsonNode root = objectMapper.readTree(rawBody);

        String event = root.path("event").asText();
        Long paymentId = root.path("paymentId").asLong();
        String gatewayTransactionId = root.path("gatewayTransactionId").asText();
        String failureReason = root.path("failureReason").asText(null);

        return new WebhookPayload(
                event,
                paymentId,
                gatewayTransactionId,
                failureReason,
                rawBody
        );
    }
}
