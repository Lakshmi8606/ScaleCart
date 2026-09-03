package com.scalecart.report.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class InvoiceRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order date is required")
    private String orderDate;

    @NotNull(message = "Shipping address is required")
    private String shippingAddress;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @NotNull(message = "Items are required")
    private List<InvoiceItem> items;

    public static class InvoiceItem {
        private String productName;
        private Integer quantity;
        private BigDecimal price;

        public InvoiceItem() {}

        // Computed for report display; not stored
        public BigDecimal getLineTotal() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }

        public String getProductName() { return productName; }
        public void setProductName(String n) { this.productName = n; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer q) { this.quantity = q; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal p) { this.price = p; }
    }

    public InvoiceRequest() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String s) { this.shippingAddress = s; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal t) { this.totalAmount = t; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
}
