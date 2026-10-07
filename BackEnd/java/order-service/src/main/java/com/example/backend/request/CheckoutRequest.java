package com.example.backend.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    private UUID deliveryAddressId;

    private String customerNote;

    private String promoCode;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
