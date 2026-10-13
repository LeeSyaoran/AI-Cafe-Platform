package com.example.backend.service.impl;

import com.example.backend.entity.LoyaltyAccount;
import com.example.backend.entity.LoyaltyTransaction;
import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.LoyaltyAccountRepository;
import com.example.backend.repository.LoyaltyTransactionRepository;
import com.example.backend.repository.RewardRedemptionRepository;
import com.example.backend.repository.RewardRepository;
import com.example.backend.service.LoyaltyService;
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
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository transactionRepository;
    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository redemptionRepository;

    private static final int POINTS_PER_VND = 1000;
    private static final int BRONZE_THRESHOLD = 0;
    private static final int SILVER_THRESHOLD = 100000;
    private static final int GOLD_THRESHOLD = 500000;
    private static final int PLATINUM_THRESHOLD = 1000000;

    @Override
    @Transactional
    public LoyaltyAccount getAccount(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    LoyaltyAccount newAccount = LoyaltyAccount.builder()
                            .userId(userId)
                            .tier("bronze")
                            .points(0)
                            .lifetimePoints(0)
                            .totalOrders(0)
                            .totalSpent(0.0)
                            .build();
                    return accountRepository.save(newAccount);
                });
    }

    @Override
    @Transactional
    public LoyaltyAccount earnPoints(UUID userId, UUID orderId, Double orderAmount) {
        LoyaltyAccount account = getAccount(userId);

        int points = (int) (orderAmount / POINTS_PER_VND);
        double tierMultiplier = getTierMultiplier(account.getTier());
        points = (int) (points * tierMultiplier);

        int currentPoints = account.getPoints() != null ? account.getPoints() : 0;
        int currentLifetime = account.getLifetimePoints() != null ? account.getLifetimePoints() : 0;
        int currentOrders = account.getTotalOrders() != null ? account.getTotalOrders() : 0;
        double currentSpent = account.getTotalSpent() != null ? account.getTotalSpent() : 0.0;

        account.setPoints(currentPoints + points);
        account.setLifetimePoints(currentLifetime + points);
        account.setTotalOrders(currentOrders + 1);
        account.setTotalSpent(currentSpent + orderAmount);

        updateTier(account);
        accountRepository.save(account);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .userId(userId)
                .companyId(account.getCompanyId())
                .type("earn")
                .points(points)
                .orderId(orderId)
                .balanceAfter(account.getPoints())
                .description("Earned from order")
                .build();
        transactionRepository.save(transaction);

        log.info("User {} earned {} points (order {})", userId, points, orderId);
        return account;
    }

    @Override
    @Transactional
    public LoyaltyAccount redeemPoints(UUID userId, UUID rewardId, UUID orderId) {
        LoyaltyAccount account = getAccount(userId);
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", rewardId));

        if (reward.getMinTier() != null && getTierLevel(reward.getMinTier()) > getTierLevel(account.getTier())) {
            throw new ForbiddenException("TIER_REQUIRED", "Your tier does not allow this reward");
        }

        int pointsRequired = reward.getPointsRequired() != null ? reward.getPointsRequired() : 0;
        int currentPoints = account.getPoints() != null ? account.getPoints() : 0;

        if (currentPoints < pointsRequired) {
            throw new BadRequestException("INSUFFICIENT_POINTS", "Not enough points to redeem this reward");
        }

        if (reward.getRemainingQuantity() != null && reward.getRemainingQuantity() <= 0) {
            throw new BadRequestException("REWARD_EXHAUSTED", "Reward is no longer available");
        }

        account.setPoints(currentPoints - pointsRequired);
        accountRepository.save(account);

        RewardRedemption redemption = RewardRedemption.builder()
                .userId(userId)
                .companyId(account.getCompanyId())
                .rewardId(rewardId)
                .pointsSpent(pointsRequired)
                .orderId(orderId)
                .status("active")
                .validFrom(Instant.now())
                .build();
        redemptionRepository.save(redemption);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .userId(userId)
                .companyId(account.getCompanyId())
                .type("redeem")
                .points(-pointsRequired)
                .rewardId(rewardId)
                .orderId(orderId)
                .balanceAfter(account.getPoints())
                .description("Redeemed: " + reward.getName())
                .build();
        transactionRepository.save(transaction);

        log.info("User {} redeemed {} points for reward {}", userId, pointsRequired, rewardId);
        return account;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reward> getAvailableRewards(UUID userId) {
        LoyaltyAccount account = getAccount(userId);
        Instant now = Instant.now();

        List<Reward> rewards = rewardRepository.findActiveRewards(now);
        final String accountTier = account.getTier();
        final int accountTierLevel = getTierLevel(accountTier);

        return rewards.stream()
                .filter(r -> r.getMinTier() == null || getTierLevel(r.getMinTier()) <= accountTierLevel)
                .filter(r -> r.getRemainingQuantity() == null || r.getRemainingQuantity() > 0)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyTransaction> getTransactionHistory(UUID userId, int limit) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().limit(limit).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardRedemption> getRedemptions(UUID userId) {
        return redemptionRepository.findByUserId(userId);
    }

    private double getTierMultiplier(String tier) {
        if (tier == null) return 1.0;
        return switch (tier.toLowerCase()) {
            case "silver" -> 1.25;
            case "gold" -> 1.5;
            case "platinum" -> 2.0;
            default -> 1.0;
        };
    }

    private int getTierLevel(String tier) {
        if (tier == null) return 1;
        return switch (tier.toLowerCase()) {
            case "platinum" -> 4;
            case "gold" -> 3;
            case "silver" -> 2;
            default -> 1;
        };
    }

    private void updateTier(LoyaltyAccount account) {
        String newTier;
        int lifetime = account.getLifetimePoints() != null ? account.getLifetimePoints() : 0;

        if (lifetime >= PLATINUM_THRESHOLD) {
            newTier = "platinum";
        } else if (lifetime >= GOLD_THRESHOLD) {
            newTier = "gold";
        } else if (lifetime >= SILVER_THRESHOLD) {
            newTier = "silver";
        } else {
            newTier = "bronze";
        }

        if (!newTier.equals(account.getTier())) {
            account.setTier(newTier);
            account.setTierExpiresAt(Instant.now().plusSeconds(365 * 24 * 60 * 60L));
            log.info("User {} upgraded to tier {}", account.getUserId(), newTier);
        }
    }
}
