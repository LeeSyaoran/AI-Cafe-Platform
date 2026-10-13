package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private UUID userId;
    private UUID companyId;
    private UUID cafeId;
    private String orderType;
    private String status;
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double deliveryFee;
    private Double totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String customerNote;
    private Instant createdAt;
    private List<OrderItemResponse> items;
}
