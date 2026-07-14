package com.scalecart.payment.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderPaidEvent {

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String gatewayTransactionId;
    private LocalDateTime paidAt;

    public OrderPaidEvent() {}

    public OrderPaidEvent(Long paymentId, Long orderId, Long userId,
                          BigDecimal amount, String gatewayTransactionId,
                          LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.gatewayTransactionId = gatewayTransactionId;
        this.paidAt = paidAt;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String id) { this.gatewayTransactionId = id; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}