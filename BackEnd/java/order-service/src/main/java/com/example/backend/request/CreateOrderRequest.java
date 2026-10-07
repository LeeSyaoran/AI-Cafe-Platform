package com.example.backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotNull(message = "Cafe ID is required")
    private UUID cafeId;

    @NotBlank(message = "Order type is required")
    private String orderType;

    private UUID deliveryAddressId;

    @NotBlank(message = "At least one item is required")
    @NotNull(message = "Items cannot be null")
    private java.util.List<OrderItemRequest> items;

    private String customerNote;

    private String promoCode;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        private UUID variantId;

        @Positive(message = "Quantity must be positive")
        private int quantity = 1;

        private String options;

        private String modifiers;

        private String notes;
    }
}
