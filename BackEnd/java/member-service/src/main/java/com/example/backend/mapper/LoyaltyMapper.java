package com.example.backend.mapper;

import com.example.backend.dto.response.LoyaltyAccountResponse;
import com.example.backend.dto.response.RewardRedemptionResponse;
import com.example.backend.dto.response.RewardResponse;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.entity.LoyaltyAccount;
import com.example.backend.entity.LoyaltyTransaction;
import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyMapper {

    public LoyaltyAccountResponse toAccountResponse(LoyaltyAccount account) {
        if (account == null) {
            return null;
        }

        return LoyaltyAccountResponse.builder()
                .tier(account.getTier())
                .pointsBalance(account.getPoints() != null ? account.getPoints().longValue() : 0L)
                .pointsLifetime(account.getLifetimePoints() != null ? account.getLifetimePoints().longValue() : 0L)
                .totalOrders(account.getTotalOrders())
                .totalSpent(account.getTotalSpent())
                .tierExpiresAt(account.getTierExpiresAt())
                .build();
    }

    public RewardResponse toRewardResponse(Reward reward) {
        if (reward == null) {
            return null;
        }

        return RewardResponse.builder()
                .id(reward.getId())
                .code(reward.getName())
                .name(reward.getName())
                .description(reward.getDescription())
                .rewardType(reward.getCategory())
                .discountValue(null)
                .pointsCost(reward.getPointsRequired())
                .validityDays(null)
                .build();
    }

    public TransactionResponse toTransactionResponse(LoyaltyTransaction tx) {
        if (tx == null) {
            return null;
        }

        return TransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .points(tx.getPoints() != null ? tx.getPoints().longValue() : 0L)
                .description(tx.getDescription())
                .status(tx.getType())
                .createdAt(tx.getCreatedAt())
                .build();
    }

    public RewardRedemptionResponse toRedemptionResponse(RewardRedemption r) {
        if (r == null) {
            return null;
        }

        return RewardRedemptionResponse.builder()
                .id(r.getId())
                .rewardId(r.getRewardId())
                .pointsSpent(r.getPointsSpent() != null ? r.getPointsSpent().longValue() : 0L)
                .status(r.getStatus())
                .redeemedAt(r.getUsedAt())
                .expiresAt(r.getExpiredAt())
                .build();
    }
}
