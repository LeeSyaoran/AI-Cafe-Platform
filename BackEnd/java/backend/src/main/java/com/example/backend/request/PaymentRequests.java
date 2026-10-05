package com.example.backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePaymentRequest {
    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String returnUrl;
}

package com.example.backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull private UUID companyId;
    @NotNull private UUID cafeId;
    @NotBlank private String orderType;
    private UUID deliveryAddressId;
    private String customerNote;
    private String promoCode;
}

@Data
class UpdateCartItemRequest {
    @NotNull private Integer quantity;
    private String notes;
}

@Data
class AddCartItemRequest {
    @NotNull private UUID productId;
    private UUID variantId;
    @NotNull private Integer quantity = 1;
    private String optionsJson;
    private String modifiersJson;
    private String notes;
}

@Data
class RegisterRequest {
    @NotBlank private String email;
    @NotBlank private String password;
    private String phone;
    private String fullName;
}

@Data
class LoginRequest {
    @NotBlank private String email;
    @NotBlank private String password;
}

@Data
class RefreshTokenRequest {
    @NotBlank private String refreshToken;
}

@Data
class UpdateProfileRequest {
    private String fullName;
    private String displayName;
    private String avatarUrl;
    private String dateOfBirth;
    private String gender;
    private String language;
    private String timezone;
    private String bio;
}

@Data
class ChangePasswordRequest {
    @NotBlank private String currentPassword;
    @NotBlank private String newPassword;
}