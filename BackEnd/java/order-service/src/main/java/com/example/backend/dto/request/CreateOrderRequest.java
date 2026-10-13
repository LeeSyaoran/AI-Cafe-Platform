package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
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

    private List<OrderItemRequest> items;

    private String customerNote;

    private String promoCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {

        @NotNull(message = "Product ID is required")
        private UUID productId;

        private UUID variantId;

        @Positive(message = "Quantity must be positive")
        @Builder.Default
        private int quantity = 1;

        private String options;
        private String modifiers;
        private String notes;
    }
}
