package com.scalecart.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalecart.payment.dto.PaymentInitiateRequest;
import com.scalecart.payment.dto.PaymentResponse;
import com.scalecart.payment.entity.PaymentStatus;
import com.scalecart.payment.security.JwtService;
import com.scalecart.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PaymentController Web Layer Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/payments/initiate — 201 Created for new payment")
    void initiatePayment_NewKey_Returns201() throws Exception {
        PaymentInitiateRequest request = new PaymentInitiateRequest();
        request.setOrderId(1L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("80498.00"));
        request.setIdempotencyKey("unique-key-xyz");
        request.setGateway("razorpay");

        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setId(1L);
        mockResponse.setStatus(PaymentStatus.PENDING);
        mockResponse.setDuplicate(false);

        when(paymentService.initiatePayment(
                any(PaymentInitiateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())  // 201 for new payment
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.duplicate").value(false));
    }

    @Test
    @DisplayName("POST /api/payments/initiate — 200 OK for duplicate key")
    void initiatePayment_DuplicateKey_Returns200() throws Exception {
        PaymentInitiateRequest request = new PaymentInitiateRequest();
        request.setOrderId(1L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("80498.00"));
        request.setIdempotencyKey("already-used-key");
        request.setGateway("razorpay");

        PaymentResponse duplicateResponse = new PaymentResponse();
        duplicateResponse.setId(1L);
        duplicateResponse.setStatus(PaymentStatus.PENDING);
        duplicateResponse.setDuplicate(true);  // it's a duplicate!

        when(paymentService.initiatePayment(
                any(PaymentInitiateRequest.class)))
                .thenReturn(duplicateResponse);

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // 200 for duplicate — no new resource created
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duplicate").value(true));
    }

    @Test
    @DisplayName("POST /api/payments/initiate — 400 on missing idempotency key")
    void initiatePayment_MissingIdempotencyKey_Returns400() throws Exception {
        PaymentInitiateRequest request = new PaymentInitiateRequest();
        request.setOrderId(1L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("80498.00"));
        // idempotencyKey not set — @NotBlank fails
        request.setGateway("razorpay");

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation Failed"));
    }

    @Test
    @DisplayName("POST /api/payments/initiate — 400 on negative amount")
    void initiatePayment_NegativeAmount_Returns400() throws Exception {
        PaymentInitiateRequest request = new PaymentInitiateRequest();
        request.setOrderId(1L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("-100.00")); // negative — fails @DecimalMin
        request.setIdempotencyKey("key-abc");
        request.setGateway("razorpay");

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}