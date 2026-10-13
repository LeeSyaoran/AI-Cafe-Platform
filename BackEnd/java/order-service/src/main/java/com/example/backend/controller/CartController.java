package com.example.backend.controller;

import com.example.backend.dto.request.AddCartItemRequest;
import com.example.backend.dto.request.UpdateCartItemRequest;
import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.dto.response.CartResponse;
import com.example.backend.entity.CartItem;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID cafeId) {
        CartResponse response = cartService.getCartResponse(userId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        CartItem item = cartService.addItem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(cartService.toItemResponse(item)));
    }

    @PutMapping("/cart/items/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCartItemRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        CartItem item = cartService.updateItem(userId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(item != null ? cartService.toItemResponse(item) : null));
    }

    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {
        cartService.removeItem(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID cafeId) {
        cartService.clearCart(userId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
