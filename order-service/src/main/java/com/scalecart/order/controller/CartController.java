package com.scalecart.order.controller;

import com.scalecart.order.dto.AddToCartRequest;
import com.scalecart.order.entity.Cart;
import com.scalecart.order.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        Cart cart = cartService.addItem(
                request.getUserId(),
                request.getProductId(),
                request.getProductName(),
                request.getPrice(),
                request.getQuantity()
        );
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}/item/{productId}")
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                cartService.removeItem(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}