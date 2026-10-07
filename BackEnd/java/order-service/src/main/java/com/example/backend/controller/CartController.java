package com.example.backend.controller;

import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import com.example.backend.request.AddCartItemRequest;
import com.example.backend.request.UpdateCartItemRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET /v1/cart
    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID cafeId) {

        Cart cart = cartService.getCart(userId, cafeId);
        if (cart == null) {
            return ResponseEntity.ok(ApiResponse.ok(CartResponse.builder()
                    .items(List.of())
                    .subtotal(0.0)
                    .itemCount(0)
                    .build()));
        }

        List<CartItem> items = cartService.getCartItems(userId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(cart, items)));
    }

    // POST /v1/cart/items
    @PostMapping("/cart/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        CartItem item = cartService.addItem(
                userId,
                request.getCafeId(),
                request.getProductId(),
                request.getVariantId(),
                request.getQuantity(),
                request.getOptions(),
                request.getModifiers(),
                request.getNotes()
        );

        return ResponseEntity.status(201).body(ApiResponse.ok(toItemResponse(item)));
    }

    // PUT /v1/cart/items/:id
    @PutMapping("/cart/items/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCartItemRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        CartItem item = cartService.updateItem(userId, id, request.getQuantity(), request.getNotes());
        if (item == null) {
            return ResponseEntity.ok(ApiResponse.<CartItemResponse>ok((CartItemResponse) null));
        }
        return ResponseEntity.ok(ApiResponse.ok(toItemResponse(item)));
    }

    // DELETE /v1/cart/items/:id
    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<ApiResponse<String>> removeItem(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        cartService.removeItem(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Item removed"));
    }

    // DELETE /v1/cart
    @DeleteMapping("/cart")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID cafeId) {

        cartService.clearCart(userId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared"));
    }

    private CartResponse toResponse(Cart cart, List<CartItem> items) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .cafeId(cart.getCafeId())
                .subtotal(cart.getSubtotal())
                .itemCount(cart.getItemCount())
                .items(items.stream().map(this::toItemResponse).toList())
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .notes(item.getNotes())
                .build();
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class CartResponse {
        private UUID id;
        private UUID userId;
        private UUID cafeId;
        private Double subtotal;
        private Integer itemCount;
        private List<CartItemResponse> items;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class CartItemResponse {
        private UUID id;
        private UUID productId;
        private UUID variantId;
        private String productName;
        private Integer quantity;
        private Double unitPrice;
        private Double lineTotal;
        private String notes;
    }
}
