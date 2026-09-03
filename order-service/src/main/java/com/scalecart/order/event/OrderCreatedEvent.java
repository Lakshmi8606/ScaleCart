package com.scalecart.order.event;

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

    public OrderCreatedEvent(Long orderId, Long userId, BigDecimal totalAmount,
                             String shippingAddress, List<OrderItemDetail> items,
                             LocalDateTime createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.createdAt = createdAt;
    }

    public static class OrderItemDetail {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;

        public OrderItemDetail() {}

        public OrderItemDetail(Long productId, String productName,
                               BigDecimal price, Integer quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
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
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<OrderItemDetail> getItems() { return items; }
    public void setItems(List<OrderItemDetail> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}