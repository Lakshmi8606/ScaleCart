package com.scalecart.order.entity;

public enum OrderStatus {
    PENDING,      // order placed, payment not yet confirmed
    CONFIRMED,    // payment confirmed
    SHIPPED,      // handed to delivery partner
    DELIVERED,    // customer received it
    CANCELLED     // cancelled before delivery
}