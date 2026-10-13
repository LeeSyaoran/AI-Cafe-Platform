package com.example.backend.mapper;

import com.example.backend.dto.response.CartItemResponse;
import com.example.backend.dto.response.CartResponse;
import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart, List<CartItem> items) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponse> itemResponses = items != null
                ? items.stream().map(this::toItemResponse).toList()
                : List.of();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .cafeId(cart.getCafeId())
                .subtotal(cart.getSubtotal())
                .itemCount(cart.getItemCount())
                .items(itemResponses)
                .build();
    }

    public CartItemResponse toItemResponse(CartItem item) {
        if (item == null) {
            return null;
        }

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
}
