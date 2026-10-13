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
public class RewardRedemptionResponse {
    private UUID id;
    private UUID rewardId;
    private Long pointsSpent;
    private String status;
    private Instant redeemedAt;
    private Instant expiresAt;
}
