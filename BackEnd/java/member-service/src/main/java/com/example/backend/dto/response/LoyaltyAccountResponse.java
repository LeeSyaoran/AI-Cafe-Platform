package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyAccountResponse {
    private String tier;
    private Long pointsBalance;
    private Long pointsLifetime;
    private Integer totalOrders;
    private Double totalSpent;
    private Instant tierExpiresAt;
}
