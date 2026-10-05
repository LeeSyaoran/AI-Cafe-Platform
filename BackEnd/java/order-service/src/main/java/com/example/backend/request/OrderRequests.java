package com.example.backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Cafe ID is required")
    private UUID cafeId;

    @NotBlank(message = "Order type is required")
    private String orderType; // pickup, delivery, dine_in

    private UUID deliveryAddressId;

    @NotBlank(message = "At least one item is required")
    @NotNull(message = "Items cannot be null")
    private List<OrderItemRequest> items;

    private String customerNote;

    private String promoCode;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @Positive(message = "Quantity must be positive")
        private int quantity = 1;

        private String options; // JSON string for size, sugar, ice, etc.

        private String modifiers; // JSON string for add-ons

        private String notes;
    }
}

@Data
class UpdateCartItemRequest {
    @Positive(message = "Quantity must be positive")
    private int quantity;

    private String options;

    private String modifiers;

    private String notes;
}

@Data
class ApplyPromoRequest {
    @NotBlank(message = "Promo code is required")
    private String code;
}

@Data
class CheckoutRequest {
    private UUID deliveryAddressId;

    private String customerNote;

    private String promoCode;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // vnpay, momo, zalopay, wallet
}
