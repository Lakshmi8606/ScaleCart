package com.scalecart.notification.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderCreatedEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private List<OrderItemDetail> items;
    private LocalDateTime createdAt;

    public OrderCreatedEvent() {}

    public static class OrderItemDetail {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;

        public OrderItemDetail() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String n) { this.productName = n; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal t) { this.totalAmount = t; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String s) { this.shippingAddress = s; }
    public List<OrderItemDetail> getItems() { return items; }
    public void setItems(List<OrderItemDetail> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
}