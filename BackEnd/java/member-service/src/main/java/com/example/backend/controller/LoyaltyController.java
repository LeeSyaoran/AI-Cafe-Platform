package com.example.backend.controller;

import com.example.backend.entity.LoyaltyAccount;
import com.example.backend.entity.LoyaltyTransaction;
import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    // GET /v1/loyalty/account
    @GetMapping("/account")
    public ResponseEntity<ApiResponse<LoyaltyAccountResponse>> getAccount(
            @RequestHeader("X-User-ID") UUID userId) {
        LoyaltyAccount account = loyaltyService.getAccount(userId);
        return ResponseEntity.ok(ApiResponse.ok(toAccountResponse(account)));
    }

    // GET /v1/loyalty/rewards
    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getRewards(
            @RequestHeader("X-User-ID") UUID userId) {
        List<Reward> rewards = loyaltyService.getAvailableRewards(userId);
        return ResponseEntity.ok(ApiResponse.ok(rewards.stream().map(this::toRewardResponse).toList()));
    }

    // POST /v1/loyalty/redeem
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<RedemptionResponse>> redeem(
            @RequestBody RedeemRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        LoyaltyAccount account = loyaltyService.redeemPoints(
                userId,
                request.getRewardId(),
                request.getOrderId()
        );

        return ResponseEntity.ok(ApiResponse.ok(new RedemptionResponse(
                account.getPoints() != null ? account.getPoints().longValue() : 0L,
                "Reward redeemed successfully"
        )));
    }

    // GET /v1/loyalty/history
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getHistory(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "50") int limit) {
        List<LoyaltyTransaction> transactions = loyaltyService.getTransactionHistory(userId, limit);
        return ResponseEntity.ok(ApiResponse.ok(transactions.stream().map(this::toTransactionResponse).toList()));
    }

    // GET /v1/loyalty/redemptions
    @GetMapping("/redemptions")
    public ResponseEntity<ApiResponse<List<RewardRedemptionResponse>>> getRedemptions(
            @RequestHeader("X-User-ID") UUID userId) {
        List<RewardRedemption> redemptions = loyaltyService.getRedemptions(userId);
        return ResponseEntity.ok(ApiResponse.ok(redemptions.stream().map(this::toRedemptionResponse).toList()));
    }

    private LoyaltyAccountResponse toAccountResponse(LoyaltyAccount account) {
        return LoyaltyAccountResponse.builder()
                .tier(account.getTier())
                .pointsBalance(account.getPoints() != null ? account.getPoints().longValue() : 0L)
                .pointsLifetime(account.getLifetimePoints() != null ? account.getLifetimePoints().longValue() : 0L)
                .totalOrders(account.getTotalOrders())
                .totalSpent(account.getTotalSpent())
                .tierExpiresAt(account.getTierExpiresAt())
                .build();
    }

    private RewardResponse toRewardResponse(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .code(reward.getName()) // Use name as code if needed
                .name(reward.getName())
                .description(reward.getDescription())
                .rewardType(reward.getCategory())
                .discountValue(null) // Not in entity
                .pointsCost(reward.getPointsRequired())
                .validityDays(null) // Not in entity
                .build();
    }

    private TransactionResponse toTransactionResponse(LoyaltyTransaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .points(tx.getPoints() != null ? tx.getPoints().longValue() : 0L)
                .description(tx.getDescription())
                .status(tx.getType()) // Use type as status
                .createdAt(tx.getCreatedAt())
                .build();
    }

    private RewardRedemptionResponse toRedemptionResponse(RewardRedemption r) {
        return RewardRedemptionResponse.builder()
                .id(r.getId())
                .rewardId(r.getRewardId())
                .pointsSpent(r.getPointsSpent() != null ? r.getPointsSpent().longValue() : 0L)
                .status(r.getStatus())
                .redeemedAt(r.getUsedAt())
                .expiresAt(r.getExpiredAt())
                .build();
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class LoyaltyAccountResponse {
        private String tier;
        private Long pointsBalance;
        private Long pointsLifetime;
        private Integer totalOrders;
        private Double totalSpent;
        private java.time.Instant tierExpiresAt;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class RewardResponse {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private String rewardType;
        private Double discountValue;
        private Integer pointsCost;
        private Integer validityDays;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class TransactionResponse {
        private UUID id;
        private String type;
        private Long points;
        private String description;
        private String status;
        private java.time.Instant createdAt;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class RewardRedemptionResponse {
        private UUID id;
        private UUID rewardId;
        private Long pointsSpent;
        private String status;
        private java.time.Instant redeemedAt;
        private java.time.Instant expiresAt;
    }

    @lombok.Data
    public static class RedeemRequest {
        private UUID rewardId;
        private UUID orderId;
    }

    @lombok.Data @lombok.AllArgsConstructor
    public static class RedemptionResponse {
        private Long remainingPoints;
        private String message;
    }
}
