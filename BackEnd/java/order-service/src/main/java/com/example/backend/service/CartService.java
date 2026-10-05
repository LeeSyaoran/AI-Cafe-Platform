package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Cart getOrCreateCart(UUID userId, UUID companyId, UUID cafeId) {
        return cartRepository.findByUserIdAndCafeId(userId, cafeId)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .userId(userId)
                            .companyId(companyId)
                            .cafeId(cafeId)
                            .subtotal(0.0)
                            .itemCount(0)
                            .expiresAt(Instant.now().plusSeconds(3600)) // 1 hour
                            .build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public CartItem addItem(UUID userId, UUID cafeId, UUID productId, UUID variantId,
                           Integer quantity, String optionsJson, String modifiersJson, String notes) {
        // Get or create cart
        Cart cart = getOrCreateCart(userId, null, cafeId);

        // Get product info
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setLineTotal(calculateLineTotal(existingItem));
            existingItem.setNotes(notes);
            cartItemRepository.save(existingItem);
            updateCartTotals(cart.getId());
            return existingItem;
        }

        // Create new item
        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(productId)
                .variantId(variantId)
                .quantity(quantity)
                .optionsJson(optionsJson)
                .modifiersJson(modifiersJson)
                .unitPrice(product.getPrice())
                .lineTotal(product.getPrice() * quantity)
                .notes(notes)
                .build();

        item = cartItemRepository.save(item);
        updateCartTotals(cart.getId());

        log.info("Added item {} to cart {} for user {}", productId, cart.getId(), userId);
        return item;
    }

    @Transactional
    public CartItem updateItem(UUID userId, UUID itemId, Integer quantity, String notes) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND", "Cart item not found"));

        // Verify ownership
        if (!item.getCart().getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_OWNER", "You do not own this item");
        }

        if (quantity <= 0) {
            // Remove item
            cartItemRepository.delete(item);
            updateCartTotals(item.getCart().getId());
            return null;
        }

        item.setQuantity(quantity);
        item.setLineTotal(calculateLineTotal(item));
        if (notes != null) {
            item.setNotes(notes);
        }

        item = cartItemRepository.save(item);
        updateCartTotals(item.getCart().getId());

        return item;
    }

    @Transactional
    public void removeItem(UUID userId, UUID itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND", "Cart item not found"));

        // Verify ownership
        if (!item.getCart().getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_OWNER", "You do not own this item");
        }

        UUID cartId = item.getCart().getId();
        cartItemRepository.delete(item);
        updateCartTotals(cartId);

        log.info("Removed item {} from cart for user {}", itemId, userId);
    }

    @Transactional
    public void clearCart(UUID userId, UUID cafeId) {
        Cart cart = cartRepository.findByUserIdAndCafeId(userId, cafeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", "Cart not found"));

        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.setSubtotal(0.0);
        cart.setItemCount(0);
        cartRepository.save(cart);

        log.info("Cleared cart for user {} at cafe {}", userId, cafeId);
    }

    @Transactional(readOnly = true)
    public Cart getCart(UUID userId, UUID cafeId) {
        return cartRepository.findByUserIdAndCafeId(userId, cafeId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(UUID userId, UUID cafeId) {
        Cart cart = getCart(userId, cafeId);
        if (cart == null) {
            return List.of();
        }
        return cartItemRepository.findByCartId(cart.getId());
    }

    private void updateCartTotals(UUID cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        Cart cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null) return;

        double subtotal = items.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();

        cart.setSubtotal(subtotal);
        cart.setItemCount(items.stream().mapToInt(CartItem::getQuantity).sum());
        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);
    }

    private double calculateLineTotal(CartItem item) {
        // TODO: Add modifier prices from modifiersJson
        return item.getUnitPrice() * item.getQuantity();
    }
}
