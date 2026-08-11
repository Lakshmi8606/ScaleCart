package com.scalecart.payment.rabbitmq;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentConfirmedEvent {

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String gatewayTransactionId;
    private LocalDateTime confirmedAt;

    public PaymentConfirmedEvent() {}

    public PaymentConfirmedEvent(Long paymentId, Long orderId,
                                 Long userId, BigDecimal amount,
                                 String gatewayTransactionId,
                                 LocalDateTime confirmedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.gatewayTransactionId = gatewayTransactionId;
        this.confirmedAt = confirmedAt;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long p) { this.paymentId = p; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long o) { this.orderId = o; }
    public Long getUserId() { return userId; }
    public void setUserId(Long u) { this.userId = u; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal a) { this.amount = a; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String g) { this.gatewayTransactionId = g; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime c) { this.confirmedAt = c; }
}