package com.example.backend.service;

import com.example.backend.dto.request.AddCartItemRequest;
import com.example.backend.dto.request.UpdateCartItemRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.dto.response.CartResponse;
import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartService {

    Cart getOrCreateCart(UUID userId, UUID companyId, UUID cafeId);

    CartItem addItem(UUID userId, AddCartItemRequest request);

    CartItem updateItem(UUID userId, UUID itemId, UpdateCartItemRequest request);

    void removeItem(UUID userId, UUID itemId);

    void clearCart(UUID userId, UUID cafeId);

    Cart getCart(UUID userId, UUID cafeId);

    List<CartItem> getCartItems(UUID userId, UUID cafeId);

    CartResponse getCartResponse(UUID userId, UUID cafeId);

    CartItemResponse toItemResponse(CartItem item);
}
