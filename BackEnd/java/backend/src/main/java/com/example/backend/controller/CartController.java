package com.example.backend.controller;

import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import com.example.backend.request.CartRequests.AddCartItemRequest;
import com.example.backend.request.CartRequests.UpdateCartItemRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID cafeId) {

        Cart cart = cartService.getCart(userId, cafeId);
        if (cart == null) {
            return ResponseEntity.ok(ApiResponse.ok(CartResponse.builder()
                    .items(List.of()).subtotal(0.0).itemCount(0).build()));
        }
        return ResponseEntity.ok(ApiResponse.ok(toResponse(cart)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addItem(
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody AddCartItemRequest request) {

        CartItem item = cartService.addItem(
                userId, request.getCompanyId(), request.getCafeId(),
                request.getProductId(), request.getVariantId(),
                request.getQuantity(), request.getOptionsJson(),
                request.getModifiersJson(), request.getNotes());

        return ResponseEntity.ok(ApiResponse.ok(toItemResponse(item)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateItem(
            @RequestHeader("X-User-ID") UUID userId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        CartItem item = cartService.updateItem(userId, itemId, request.getQuantity(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.ok(toItemResponse(item)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @RequestHeader("X-User-ID") UUID userId,
            @PathVariable UUID itemId) {

        cartService.removeItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID cafeId) {

        cartService.clearCart(userId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItem> items = cartService.getCartItems(cart.getUserId(), cart.getCafeId());
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
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .optionsJson(item.getOptionsJson())
                .notes(item.getNotes())
                .build();
    }
}

@lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
class CartResponse {
    private UUID id;
    private UUID userId;
    private UUID cafeId;
    private Double subtotal;
    private Integer itemCount;
    private List<CartItemResponse> items;
}

@lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
class CartItemResponse {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
    private String optionsJson;
    private String notes;
}

package com.example.backend.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

public class CartRequests {

    @Data
    public static class AddCartItemRequest {
        @NotNull private UUID companyId;
        @NotNull private UUID cafeId;
        @NotNull private UUID productId;
        private UUID variantId;
        @NotNull private Integer quantity = 1;
        private String optionsJson;
        private String modifiersJson;
        private String notes;
    }

    @Data
    public static class UpdateCartItemRequest {
        @NotNull private Integer quantity;
        private String notes;
    }
}