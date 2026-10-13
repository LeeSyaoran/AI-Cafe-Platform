package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    @NotNull(message = "Plan ID is required")
    private UUID planId;

    @NotBlank(message = "Billing cycle is required")
    private String billingCycle;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
