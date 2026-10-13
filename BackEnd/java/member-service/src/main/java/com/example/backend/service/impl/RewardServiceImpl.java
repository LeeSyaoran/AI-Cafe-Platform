package com.example.backend.service.impl;

import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;
import com.example.backend.repository.RewardRedemptionRepository;
import com.example.backend.repository.RewardRepository;
import com.example.backend.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository redemptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Reward> getActiveRewards() {
        return rewardRepository.findActiveRewards(Instant.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardRedemption> getUserRedemptions(UUID userId) {
        return redemptionRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardRedemption> getUserRedemptionsByStatus(UUID userId, String status) {
        return redemptionRepository.findByUserIdAndStatus(userId, status);
    }
}
