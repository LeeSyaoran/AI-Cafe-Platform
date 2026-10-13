package com.example.backend.mapper;

import com.example.backend.dto.request.CreatePromotionRequest;
import com.example.backend.dto.request.CreateRewardRequest;
import com.example.backend.entity.Promotion;
import com.example.backend.entity.Reward;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PromotionMapper {

    public Promotion toEntity(CreatePromotionRequest request, UUID companyId) {
        if (request == null) return null;

        return Promotion.builder()
                .companyId(companyId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .status("active")
                .isActive(true)
                .build();
    }

    public Reward toRewardEntity(CreateRewardRequest request, UUID companyId) {
        if (request == null) return null;

        return Reward.builder()
                .companyId(companyId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .pointsRequired(request.getPointsCost())
                .category(request.getRewardType())
                .rewardType(request.getRewardType())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .status("active")
                .isActive(true)
                .build();
    }
}
