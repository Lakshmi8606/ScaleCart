package com.scalecart.payment.entity;

public enum PaymentStatus {
    PENDING,    // payment initiated, awaiting gateway confirmation
    COMPLETED,  // gateway confirmed payment successful
    FAILED,     // gateway reported failure
    REFUNDED    // payment was refunded after completion
}