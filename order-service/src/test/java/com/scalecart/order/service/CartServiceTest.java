package com.scalecart.order.service;

import com.scalecart.order.entity.Cart;
import com.scalecart.order.entity.CartItem;
import com.scalecart.order.repository.CartRepository;
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
@DisplayName("CartService Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart existingCart;

    @BeforeEach
    void setUp() {
        existingCart = new Cart(1L);
    }

    @Test
    @DisplayName("Should return existing cart when found for user")
    void getOrCreateCart_ExistingCart_ReturnsIt() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(existingCart));

        Cart result = cartService.getOrCreateCart(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should create new cart when none exists for user")
    void getOrCreateCart_NoCart_CreatesNew() {
        when(cartRepository.findByUserId(99L))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(inv -> {
                    Cart c = inv.getArgument(0);
                    return c;
                });

        Cart result = cartService.getOrCreateCart(99L);

        assertThat(result).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should add item to empty cart")
    void addItem_EmptyCart_AddsItem() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(existingCart);

        Cart result = cartService.addItem(
                1L, 1L, "iPhone 15",
                new BigDecimal("79999.00"), 1);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName())
                .isEqualTo("iPhone 15");
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should increment quantity when same product added again")
    void addItem_SameProduct_IncrementsQuantity() {
        CartItem existing = new CartItem(
                existingCart, 1L, "iPhone 15",
                new BigDecimal("79999.00"), 1);
        existingCart.getItems().add(existing);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(existingCart);

        cartService.addItem(1L, 1L, "iPhone 15",
                new BigDecimal("79999.00"), 2);

        assertThat(existingCart.getItems()).hasSize(1);
        assertThat(existingCart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should remove item from cart")
    void removeItem_ExistingItem_RemovesIt() {
        CartItem item = new CartItem(
                existingCart, 1L, "iPhone 15",
                new BigDecimal("79999.00"), 1);
        existingCart.getItems().add(item);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(existingCart);

        cartService.removeItem(1L, 1L);

        assertThat(existingCart.getItems()).isEmpty();
        verify(cartRepository).save(existingCart);
    }

    @Test
    @DisplayName("Should throw exception when removing from non-existent cart")
    void removeItem_NoCart_ThrowsException() {
        when(cartRepository.findByUserId(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.removeItem(99L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cart not found");
    }

    @Test
    @DisplayName("Should clear all items from cart")
    void clearCart_RemovesAllItems() {
        CartItem item1 = new CartItem(existingCart, 1L, "iPhone",
                new BigDecimal("79999"), 1);
        CartItem item2 = new CartItem(existingCart, 2L, "Book",
                new BigDecimal("499"), 2);
        existingCart.getItems().addAll(java.util.List.of(item1, item2));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(existingCart);

        cartService.clearCart(1L);

        assertThat(existingCart.getItems()).isEmpty();
    }
}
