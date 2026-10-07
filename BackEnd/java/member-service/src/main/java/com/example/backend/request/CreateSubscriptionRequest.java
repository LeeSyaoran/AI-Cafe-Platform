package com.example.backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSubscriptionRequest {

    @NotNull(message = "Plan ID is required")
    private UUID planId;

    @NotBlank(message = "Billing cycle is required")
    private String billingCycle; // monthly, yearly

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
