package com.scalecart.order.service;

import com.scalecart.order.entity.*;
import com.scalecart.order.kafka.OrderEventProducer;
import com.scalecart.order.repository.CartRepository;
import com.scalecart.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService orderService;

    private Cart cartWithItems;

    @BeforeEach
    void setUp() {
        cartWithItems = new Cart(1L);

        CartItem item1 = new CartItem(
                cartWithItems,
                1L,
                "iPhone 15",
                new BigDecimal("79999.00"),
                1
        );

        CartItem item2 = new CartItem(
                cartWithItems,
                3L,
                "Java Book",
                new BigDecimal("499.00"),
                2
        );

        cartWithItems.getItems().add(item1);
        cartWithItems.getItems().add(item2);
    }

    @Test
    @DisplayName("Should checkout successfully and create order from cart")
    void checkout_Success_CreatesOrder() {

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cartWithItems));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cartWithItems);

        doNothing().when(orderEventProducer)
                .publishOrderCreated(any());

        Order result = orderService.checkout(
                1L,
                "123 MG Road, Bangalore"
        );

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);

        assertThat(result.getShippingAddress())
                .isEqualTo("123 MG Road, Bangalore");

        assertThat(result.getTotalAmount())
                .isEqualByComparingTo(new BigDecimal("80997.00"));

        assertThat(result.getItems())
                .hasSize(2);

        assertThat(cartWithItems.getItems())
                .isEmpty();

        verify(orderEventProducer)
                .publishOrderCreated(any());
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void checkout_CartNotFound_ThrowsException() {

        when(cartRepository.findByUserId(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.checkout(99L, "some address")
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No cart found for user: 99");

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(orderEventProducer, never())
                .publishOrderCreated(any());
    }

    @Test
    @DisplayName("Should throw exception when cart is empty")
    void checkout_EmptyCart_ThrowsException() {

        Cart emptyCart = new Cart(1L);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(emptyCart));

        assertThatThrownBy(() ->
                orderService.checkout(1L, "some address")
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot checkout with an empty cart");

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(orderEventProducer, never())
                .publishOrderCreated(any());
    }

    @Test
    @DisplayName("Should cancel PENDING order successfully")
    void cancelOrder_PendingOrder_Cancels() {

        Order pendingOrder = new Order();

        pendingOrder.setUserId(1L);
        pendingOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(pendingOrder));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(pendingOrder);

        Order result = orderService.cancelOrder(1L, 1L);

        assertThat(result.getStatus())
                .isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository)
                .save(pendingOrder);
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-PENDING order")
    void cancelOrder_ConfirmedOrder_ThrowsException() {

        Order confirmedOrder = new Order();

        confirmedOrder.setUserId(1L);
        confirmedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(confirmedOrder));

        assertThatThrownBy(() ->
                orderService.cancelOrder(1L, 1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Only PENDING orders can be cancelled"
                );

        assertThat(confirmedOrder.getStatus())
                .isEqualTo(OrderStatus.CONFIRMED);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    @DisplayName("Should calculate total correctly with multiple items")
    void checkout_TotalCalculation_IsCorrect() {

        Cart cart = new Cart(2L);

        cart.getItems().add(
                new CartItem(
                        cart,
                        1L,
                        "Item A",
                        new BigDecimal("100.00"),
                        3
                )
        );

        cart.getItems().add(
                new CartItem(
                        cart,
                        2L,
                        "Item B",
                        new BigDecimal("250.00"),
                        2
                )
        );

        when(cartRepository.findByUserId(2L))
                .thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cart);

        doNothing().when(orderEventProducer)
                .publishOrderCreated(any());

        Order result = orderService.checkout(
                2L,
                "Test Address"
        );

        assertThat(result.getTotalAmount())
                .isEqualByComparingTo(new BigDecimal("800.00"));
    }
}