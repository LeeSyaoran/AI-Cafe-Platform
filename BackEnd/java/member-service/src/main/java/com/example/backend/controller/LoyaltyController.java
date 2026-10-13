package com.example.backend.controller;

import com.example.backend.dto.request.RedeemRequest;
import com.example.backend.dto.response.LoyaltyAccountResponse;
import com.example.backend.dto.response.RedemptionResponse;
import com.example.backend.dto.response.RewardRedemptionResponse;
import com.example.backend.dto.response.RewardResponse;
import com.example.backend.dto.response.TransactionResponse;
import com.example.backend.entity.LoyaltyAccount;
import com.example.backend.entity.LoyaltyTransaction;
import com.example.backend.entity.Reward;
import com.example.backend.entity.RewardRedemption;
import com.example.backend.mapper.LoyaltyMapper;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final LoyaltyMapper loyaltyMapper;

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<LoyaltyAccountResponse>> getAccount(
            @RequestHeader("X-User-ID") UUID userId) {
        LoyaltyAccount account = loyaltyService.getAccount(userId);
        return ResponseEntity.ok(ApiResponse.ok(loyaltyMapper.toAccountResponse(account)));
    }

    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getRewards(
            @RequestHeader("X-User-ID") UUID userId) {
        List<Reward> rewards = loyaltyService.getAvailableRewards(userId);
        return ResponseEntity.ok(ApiResponse.ok(rewards.stream().map(loyaltyMapper::toRewardResponse).toList()));
    }

    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<RedemptionResponse>> redeem(
            @Valid @RequestBody RedeemRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        LoyaltyAccount account = loyaltyService.redeemPoints(userId, request.getRewardId(), request.getOrderId());
        long remaining = account.getPoints() != null ? account.getPoints().longValue() : 0L;
        return ResponseEntity.ok(ApiResponse.ok(new RedemptionResponse(remaining, "Reward redeemed successfully")));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getHistory(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "50") int limit) {
        List<LoyaltyTransaction> transactions = loyaltyService.getTransactionHistory(userId, limit);
        return ResponseEntity.ok(ApiResponse.ok(transactions.stream().map(loyaltyMapper::toTransactionResponse).toList()));
    }

    @GetMapping("/redemptions")
    public ResponseEntity<ApiResponse<List<RewardRedemptionResponse>>> getRedemptions(
            @RequestHeader("X-User-ID") UUID userId) {
        List<RewardRedemption> redemptions = loyaltyService.getRedemptions(userId);
        return ResponseEntity.ok(ApiResponse.ok(redemptions.stream().map(loyaltyMapper::toRedemptionResponse).toList()));
    }
}
