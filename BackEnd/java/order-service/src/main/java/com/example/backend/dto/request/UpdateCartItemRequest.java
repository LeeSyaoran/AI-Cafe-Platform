package com.example.backend.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {

    @Positive(message = "Quantity must be positive")
    private int quantity;

    private String options;
    private String modifiers;
    private String notes;
}
