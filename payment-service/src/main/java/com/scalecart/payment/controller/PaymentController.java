package com.scalecart.payment.controller;

import com.scalecart.payment.dto.PaymentInitiateRequest;
import com.scalecart.payment.dto.PaymentResponse;
import com.scalecart.payment.entity.Payment;
import com.scalecart.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody PaymentInitiateRequest request) {

        PaymentResponse response = paymentService.initiatePayment(request);

        // 200 for duplicates (no new resource); 201 for new payments
        HttpStatus status = response.isDuplicate()
                ? HttpStatus.OK
                : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
