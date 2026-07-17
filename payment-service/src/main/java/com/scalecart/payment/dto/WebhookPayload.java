package com.scalecart.payment.dto;

public class WebhookPayload {

    // Event type from gateway: "payment.success", "payment.failed"
    private String event;

    // Your internal payment ID (you'd embed this when initiating payment)
    private Long paymentId;

    // Gateway's own transaction reference
    private String gatewayTransactionId;

    // Failure reason (populated only for payment.failed events)
    private String failureReason;

    // The raw JSON body - stored for audit trail
    private String rawPayload;

    public WebhookPayload() {}

    public WebhookPayload(String event, Long paymentId,
                          String gatewayTransactionId,
                          String failureReason, String rawPayload) {
        this.event = event;
        this.paymentId = paymentId;
        this.gatewayTransactionId = gatewayTransactionId;
        this.failureReason = failureReason;
        this.rawPayload = rawPayload;
    }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String id) { this.gatewayTransactionId = id; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String reason) { this.failureReason = reason; }
    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }
}