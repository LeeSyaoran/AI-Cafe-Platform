package com.example.backend.service.impl;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService  {

    private final PromotionRepository promotionRepository;
    private final RewardRepository rewardRepository;

    // ============ Promotion CRUD ============

    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion updatePromotion(UUID promotionId, Promotion updates) {
        Promotion promotion = findPromotionOrThrow(promotionId);

        if (updates.getName() != null) promotion.setName(updates.getName());
        if (updates.getDiscountType() != null) promotion.setDiscountType(updates.getDiscountType());
        if (updates.getDiscountValue() != null) promotion.setDiscountValue(updates.getDiscountValue());
        if (updates.getMinOrderAmount() != null) promotion.setMinOrderAmount(updates.getMinOrderAmount());
        if (updates.getMaxDiscountAmount() != null) promotion.setMaxDiscountAmount(updates.getMaxDiscountAmount());
        if (updates.getUsageLimit() != null) promotion.setUsageLimit(updates.getUsageLimit());
        if (updates.getStatus() != null) promotion.setStatus(updates.getStatus());

        return promotionRepository.save(promotion);
    }

    @Transactional
    public void deletePromotion(UUID promotionId) {
        Promotion promotion = findPromotionOrThrow(promotionId);
        promotion.setStatus("inactive");
        promotionRepository.save(promotion);
    }

    @Transactional(readOnly = true)
    public List<Promotion> getPromotions() {
        return promotionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Promotion getPromotion(UUID promotionId) {
        return findPromotionOrThrow(promotionId);
    }

    private Promotion findPromotionOrThrow(UUID promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", promotionId));
    }

    // ============ Reward CRUD ============

    @Transactional
    public Reward createReward(Reward reward) {
        if (reward.getQuantityRemaining() == null && reward.getQuantity() != null) {
            reward.setQuantityRemaining(reward.getQuantity());
        }
        return rewardRepository.save(reward);
    }

    @Transactional
    public Reward updateReward(UUID rewardId, Reward updates) {
        Reward reward = findRewardOrThrow(rewardId);

        if (updates.getName() != null) reward.setName(updates.getName());
        if (updates.getDiscountType() != null) reward.setDiscountType(updates.getDiscountType());
        if (updates.getDiscountValue() != null) reward.setDiscountValue(updates.getDiscountValue());
        if (updates.getPointsCost() != null) reward.setPointsCost(updates.getPointsCost());
        if (updates.getQuantity() != null) reward.setQuantity(updates.getQuantity());
        if (updates.getStatus() != null) reward.setStatus(updates.getStatus());

        return rewardRepository.save(reward);
    }

    @Transactional(readOnly = true)
    public List<Reward> getRewards() {
        return rewardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reward getReward(UUID rewardId) {
        return findRewardOrThrow(rewardId);
    }

    private Reward findRewardOrThrow(UUID rewardId) {
        return rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", rewardId));
    }
}
