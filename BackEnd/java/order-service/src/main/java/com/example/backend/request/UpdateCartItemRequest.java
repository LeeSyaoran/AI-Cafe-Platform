package com.example.backend.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {

    @Positive(message = "Quantity must be positive")
    private int quantity;

    private String options;

    private String modifiers;

    private String notes;
}
