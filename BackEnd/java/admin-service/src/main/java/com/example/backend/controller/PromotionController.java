package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Promotion>>> getPromotions() {
        List<Promotion> promotions = promotionService.getPromotions();
        return ResponseEntity.ok(ApiResponse.ok(promotions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Promotion>> getPromotion(@PathVariable UUID id) {
        Promotion promotion = promotionService.getPromotion(id);
        return ResponseEntity.ok(ApiResponse.ok(promotion));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(
            @Valid @RequestBody CreatePromotionRequest request,
            @RequestHeader("X-Company-ID") UUID companyId) {

        Promotion promotion = Promotion.builder()
                .companyId(companyId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .status("active")
                .build();

        promotion = promotionService.createPromotion(promotion);
        return ResponseEntity.ok(ApiResponse.ok(promotion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Promotion>> updatePromotion(
            @PathVariable UUID id,
            @RequestBody Promotion updates) {

        Promotion promotion = promotionService.updatePromotion(id, updates);
        return ResponseEntity.ok(ApiResponse.ok(promotion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePromotion(@PathVariable UUID id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.ok("Promotion deleted"));
    }

    // ============ Rewards ============

    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<List<Reward>>> getRewards() {
        List<Reward> rewards = promotionService.getRewards();
        return ResponseEntity.ok(ApiResponse.ok(rewards));
    }

    @GetMapping("/rewards/{id}")
    public ResponseEntity<ApiResponse<Reward>> getReward(@PathVariable UUID id) {
        Reward reward = promotionService.getReward(id);
        return ResponseEntity.ok(ApiResponse.ok(reward));
    }

    @PostMapping("/rewards")
    public ResponseEntity<ApiResponse<Reward>> createReward(
            @Valid @RequestBody CreateRewardRequest request,
            @RequestHeader("X-Company-ID") UUID companyId) {

        Reward reward = Reward.builder()
                .companyId(companyId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .rewardType(request.getRewardType())
                .category(request.getRewardType())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .pointsRequired(request.getPointsCost())
                .status("active")
                .build();

        reward = promotionService.createReward(reward);
        return ResponseEntity.ok(ApiResponse.ok(reward));
    }

    @PutMapping("/rewards/{id}")
    public ResponseEntity<ApiResponse<Reward>> updateReward(
            @PathVariable UUID id,
            @RequestBody Reward updates) {

        Reward reward = promotionService.updateReward(id, updates);
        return ResponseEntity.ok(ApiResponse.ok(reward));
    }
}
