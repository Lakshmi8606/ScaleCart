package com.scalecart.order.service;

import com.scalecart.order.entity.*;
import com.scalecart.order.event.OrderCreatedEvent;
import com.scalecart.order.kafka.OrderEventProducer;
import com.scalecart.order.repository.CartRepository;
import com.scalecart.order.repository.OrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderEventProducer = orderEventProducer;
    }

    @Transactional
    public Order checkout(Long userId, String shippingAddress) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "No cart found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot checkout with an empty cart");
        }

        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> new OrderItem(
                        order,
                        cartItem.getProductId(),
                        cartItem.getProductName(),
                        cartItem.getPrice(),
                        cartItem.getQuantity()
                ))
                .toList();

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getTotalAmount(),
                savedOrder.getShippingAddress(),
                savedOrder.getItems().stream()
                        .map(item -> new OrderCreatedEvent.OrderItemDetail(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity()
                        ))
                        .toList(),
                savedOrder.getCreatedAt()
        );

        orderEventProducer.publishOrderCreated(event);

        return savedOrder;
    }

    public Order getOrderById(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
    }

    public org.springframework.data.domain.Page<Order> getUserOrders(
            Long userId,
            org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING orders can be cancelled. Current status: "
                            + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
