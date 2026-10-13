package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequest {

    @NotNull(message = "Cafe ID is required")
    private UUID cafeId;

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
