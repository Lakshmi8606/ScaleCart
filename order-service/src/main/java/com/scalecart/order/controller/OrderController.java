package com.scalecart.order.controller;

import com.scalecart.order.dto.CheckoutRequest;
import com.scalecart.order.entity.Order;
import com.scalecart.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders/checkout - THE main endpoint
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            @Valid @RequestBody CheckoutRequest request) {

        Order order = orderService.checkout(
                request.getUserId(),
                request.getShippingAddress()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Order>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Order> orders = orderService.getUserOrders(
                userId,
                PageRequest.of(page, size,
                        Sort.by("createdAt").descending())
        );
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/user/{userId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long orderId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                orderService.getOrderById(orderId, userId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, userId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}