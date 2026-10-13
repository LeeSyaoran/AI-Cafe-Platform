package com.example.backend.service;

import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;

import java.util.List;
import java.util.UUID;

public interface RewardService {

    List<Reward> getActiveRewards();

    List<RewardRedemption> getUserRedemptions(UUID userId);

    List<RewardRedemption> getUserRedemptionsByStatus(UUID userId, String status);
}
