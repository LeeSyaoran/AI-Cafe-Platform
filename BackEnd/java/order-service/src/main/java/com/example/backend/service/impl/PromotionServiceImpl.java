package com.example.backend.service.impl;

import com.example.backend.entity.Cart;
import com.example.backend.entity.Promotion;
import com.example.backend.exception.BadRequestException;
import com.example.backend.repository.PromotionRepository;
import com.example.backend.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promotion> getPromotionById(UUID id) {
        return promotionRepository.findById(id);
    }

    @Override
    public double applyPromotion(Cart cart, String promoCode) {
        Promotion promo = promotionRepository.findByCode(promoCode)
                .orElseThrow(() -> new BadRequestException("INVALID_PROMO", "Invalid promotion code: " + promoCode));

        Instant now = Instant.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) {
            throw new BadRequestException("PROMO_NOT_STARTED", "Promotion has not started yet");
        }
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) {
            throw new BadRequestException("PROMO_EXPIRED", "Promotion has expired");
        }
        if (promo.getUsageLimit() != null && promo.getUsedCount() >= promo.getUsageLimit()) {
            throw new BadRequestException("PROMO_LIMIT_REACHED", "Promotion usage limit has been reached");
        }

        double subtotal = cart.getSubtotal();
        if (subtotal < (promo.getMinOrderAmount() != null ? promo.getMinOrderAmount() : 0)) {
            throw new BadRequestException("MIN_ORDER_NOT_MET", "Minimum order amount not met for this promotion");
        }

        double discount;
        if ("percentage".equalsIgnoreCase(promo.getDiscountType())) {
            discount = subtotal * (promo.getDiscountValue() / 100);
            if (promo.getMaxDiscountAmount() != null && discount > promo.getMaxDiscountAmount()) {
                discount = promo.getMaxDiscountAmount();
            }
        } else {
            discount = promo.getDiscountValue();
        }

        return discount;
    }
}
