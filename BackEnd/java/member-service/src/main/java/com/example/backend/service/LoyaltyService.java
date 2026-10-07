package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository transactionRepository;
    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository redemptionRepository;

    // Points rate: 1 point per 1000 VND spent
    private static final int POINTS_PER_VND = 1000;

    // Tier thresholds (lifetime points)
    private static final int BRONZE_THRESHOLD = 0;
    private static final int SILVER_THRESHOLD = 100000;
    private static final int GOLD_THRESHOLD = 500000;
    private static final int PLATINUM_THRESHOLD = 1000000;

    @Transactional(readOnly = true)
    public LoyaltyAccount getAccount(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    LoyaltyAccount newAccount = LoyaltyAccount.builder()
                            .userId(userId)
                            .tier("bronze")
                            .build();
                    return accountRepository.save(newAccount);
                });
    }

    @Transactional
    public LoyaltyAccount earnPoints(UUID userId, UUID orderId, Double orderAmount) {
        LoyaltyAccount account = getAccount(userId);

        // Calculate points
        int points = (int) (orderAmount / POINTS_PER_VND);

        // Apply tier bonus
        double tierMultiplier = getTierMultiplier(account.getTier());
        points = (int) (points * tierMultiplier);

        // Update account
        account.setPoints(account.getPoints() + points);
        account.setLifetimePoints(account.getLifetimePoints() + points);
        account.setTotalOrders(account.getTotalOrders() + 1);
        account.setTotalSpent(account.getTotalSpent() + orderAmount);

        // Check for tier upgrade
        updateTier(account);

        accountRepository.save(account);

        // Log transaction
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

    @Transactional
    public LoyaltyAccount redeemPoints(UUID userId, UUID rewardId, UUID orderId) {
        LoyaltyAccount account = getAccount(userId);
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), "REWARD_NOT_FOUND", "Reward not found"));

        // Check tier access - simplified check
        if (reward.getMinTier() != null && getTierLevel(reward.getMinTier()) > getTierLevel(account.getTier())) {
            throw new ApiException(HttpStatus.FORBIDDEN.value(), "TIER_REQUIRED", "Your tier does not allow this reward");
        }

        // Check points
        if (account.getPoints() < reward.getPointsRequired()) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT_POINTS", "Not enough points");
        }

        // Check availability
        if (reward.getRemainingQuantity() != null && reward.getRemainingQuantity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "REWARD_EXHAUSTED", "Reward is no longer available");
        }

        // Deduct points
        account.setPoints(account.getPoints() - reward.getPointsRequired());
        accountRepository.save(account);

        // Create redemption
        RewardRedemption redemption = RewardRedemption.builder()
                .userId(userId)
                .companyId(account.getCompanyId())
                .rewardId(rewardId)
                .pointsSpent(reward.getPointsRequired() != null ? reward.getPointsRequired() : 0)
                .orderId(orderId)
                .status("active")
                .validFrom(Instant.now())
                .build();
        redemptionRepository.save(redemption);

        // Log transaction
        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .userId(userId)
                .companyId(account.getCompanyId())
                .type("redeem")
                .points(-reward.getPointsRequired())
                .rewardId(rewardId)
                .orderId(orderId)
                .balanceAfter(account.getPoints())
                .description("Redeemed: " + reward.getName())
                .build();
        transactionRepository.save(transaction);

        log.info("User {} redeemed {} points for reward {}", userId, reward.getPointsRequired(), rewardId);
        return account;
    }

    @Transactional(readOnly = true)
    public List<Reward> getAvailableRewards(UUID userId) {
        LoyaltyAccount account = getAccount(userId);
        Instant now = Instant.now();

        List<Reward> rewards = rewardRepository.findActiveRewards(now);

        // Filter by tier and availability
        final String accountTier = account.getTier();
        final int accountTierLevel = getTierLevel(accountTier);

        return rewards.stream()
                .filter(r -> {
                    if (r.getMinTier() == null) return true;
                    return getTierLevel(r.getMinTier()) <= accountTierLevel;
                })
                .filter(r -> r.getRemainingQuantity() == null || r.getRemainingQuantity() > 0)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoyaltyTransaction> getTransactionHistory(UUID userId, int limit) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().limit(limit).toList();
    }

    @Transactional(readOnly = true)
    public List<RewardRedemption> getRedemptions(UUID userId) {
        return redemptionRepository.findByUserId(userId);
    }

    private double getTierMultiplier(String tier) {
        return switch (tier) {
            case "silver" -> 1.25;
            case "gold" -> 1.5;
            case "platinum" -> 2.0;
            default -> 1.0;
        };
    }

    private int getTierLevel(String tier) {
        return switch (tier) {
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
            // Tier benefits valid for 1 year
            account.setTierExpiresAt(Instant.now().plusSeconds(365 * 24 * 60 * 60L));
            log.info("User {} upgraded to tier {}", account.getUserId(), newTier);
        }
    }
}
