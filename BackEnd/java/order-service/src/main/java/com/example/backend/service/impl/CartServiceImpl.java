package com.example.backend.service.impl;

import com.example.backend.dto.request.AddCartItemRequest;
import com.example.backend.dto.request.UpdateCartItemRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.dto.response.CartResponse;
import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import com.example.backend.entity.Product;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.CartMapper;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
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
                            .expiresAt(Instant.now().plusSeconds(3600))
                            .build();
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional
    public CartItem addItem(UUID userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId, null, request.getCafeId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setLineTotal(calculateLineTotal(existingItem));
            existingItem.setNotes(request.getNotes());
            cartItemRepository.save(existingItem);
            updateCartTotals(cart.getId());
            return existingItem;
        }

        CartItem item = CartItem.builder()
                .cartId(cart.getId())
                .productId(request.getProductId())
                .productName(product.getName())
                .variantId(request.getVariantId())
                .quantity(request.getQuantity())
                .optionsJson(request.getOptions())
                .modifiersJson(request.getModifiers())
                .unitPrice(product.getPrice())
                .lineTotal(product.getPrice() * request.getQuantity())
                .notes(request.getNotes())
                .build();

        cart.addItem(item);
        item = cartItemRepository.save(item);
        updateCartTotals(cart.getId());

        log.info("Added item {} to cart {} for user {}", request.getProductId(), cart.getId(), userId);
        return item;
    }

    @Override
    @Transactional
    public CartItem updateItem(UUID userId, UUID itemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getUserId().equals(userId)) {
            throw new ForbiddenException("NOT_OWNER", "You do not own this cart item");
        }

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(item);
            updateCartTotals(item.getCart().getId());
            return null;
        }

        item.setQuantity(request.getQuantity());
        item.setLineTotal(calculateLineTotal(item));
        if (request.getNotes() != null) {
            item.setNotes(request.getNotes());
        }

        item = cartItemRepository.save(item);
        updateCartTotals(item.getCart().getId());

        return item;
    }

    @Override
    @Transactional
    public void removeItem(UUID userId, UUID itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getUserId().equals(userId)) {
            throw new ForbiddenException("NOT_OWNER", "You do not own this cart item");
        }

        UUID cartId = item.getCart().getId();
        cartItemRepository.delete(item);
        updateCartTotals(cartId);

        log.info("Removed item {} from cart for user {}", itemId, userId);
    }

    @Override
    @Transactional
    public void clearCart(UUID userId, UUID cafeId) {
        Cart cart = cartRepository.findByUserIdAndCafeId(userId, cafeId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cafeId", cafeId));

        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.setSubtotal(0.0);
        cart.setItemCount(0);
        cartRepository.save(cart);

        log.info("Cleared cart for user {} at cafe {}", userId, cafeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getCart(UUID userId, UUID cafeId) {
        return cartRepository.findByUserIdAndCafeId(userId, cafeId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(UUID userId, UUID cafeId) {
        Cart cart = getCart(userId, cafeId);
        if (cart == null) {
            return List.of();
        }
        return cartItemRepository.findByCartId(cart.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartResponse(UUID userId, UUID cafeId) {
        Cart cart = getCart(userId, cafeId);
        if (cart == null) {
            return CartResponse.builder()
                    .userId(userId)
                    .cafeId(cafeId)
                    .subtotal(0.0)
                    .itemCount(0)
                    .items(List.of())
                    .build();
        }
        List<CartItem> items = getCartItems(userId, cafeId);
        return cartMapper.toResponse(cart, items);
    }

    @Override
    public CartItemResponse toItemResponse(CartItem item) {
        return cartMapper.toItemResponse(item);
    }

    private void updateCartTotals(UUID cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        Cart cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null) return;

        double subtotal = items.stream().mapToDouble(CartItem::getLineTotal).sum();

        cart.setSubtotal(subtotal);
        cart.setItemCount(items.stream().mapToInt(CartItem::getQuantity).sum());
        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);
    }

    private double calculateLineTotal(CartItem item) {
        return item.getUnitPrice() * item.getQuantity();
    }
}
