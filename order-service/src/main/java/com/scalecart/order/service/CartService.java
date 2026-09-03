package com.scalecart.order.service;

import com.scalecart.order.entity.Cart;
import com.scalecart.order.entity.CartItem;
import com.scalecart.order.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));
    }

    @Transactional
    public Cart addItem(Long userId, Long productId, String productName,
                        BigDecimal price, Integer quantity) {

        Cart cart = getOrCreateCart(userId);

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(
                                existing.getQuantity() + quantity),
                        () -> cart.getItems().add(
                                new CartItem(cart, productId,
                                        productName, price, quantity))
                );

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));

        // orphanRemoval deletes the row when removed from the collection
        cart.getItems().removeIf(
                item -> item.getProductId().equals(productId));

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }
}