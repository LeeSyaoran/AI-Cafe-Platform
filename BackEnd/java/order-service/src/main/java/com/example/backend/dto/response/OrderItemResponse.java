package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
    private String itemStatus;
    private Instant createdAt;
}
