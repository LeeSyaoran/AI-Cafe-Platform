package com.example.backend.service;

import com.example.backend.entity.LoyaltyAccount;
import com.example.backend.entity.LoyaltyTransaction;
import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;

import java.util.List;
import java.util.UUID;

public interface LoyaltyService {

    LoyaltyAccount getAccount(UUID userId);

    LoyaltyAccount earnPoints(UUID userId, UUID orderId, Double orderAmount);

    LoyaltyAccount redeemPoints(UUID userId, UUID rewardId, UUID orderId);

    List<Reward> getAvailableRewards(UUID userId);

    List<LoyaltyTransaction> getTransactionHistory(UUID userId, int limit);

    List<RewardRedemption> getRedemptions(UUID userId);
}
