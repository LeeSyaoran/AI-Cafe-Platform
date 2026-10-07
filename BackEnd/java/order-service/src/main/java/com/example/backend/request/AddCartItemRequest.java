package com.example.backend.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequest {

    @NotNull(message = "Cafe ID is required")
    private java.util.UUID cafeId;

    @NotNull(message = "Product ID is required")
    private java.util.UUID productId;

    private java.util.UUID variantId;

    @Positive(message = "Quantity must be positive")
    private int quantity = 1;

    private String options;

    private String modifiers;

    private String notes;
}
