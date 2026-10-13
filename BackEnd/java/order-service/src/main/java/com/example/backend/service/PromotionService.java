package com.example.backend.service;

import com.example.backend.entity.Cart;
import com.example.backend.entity.Promotion;

import java.util.Optional;
import java.util.UUID;

public interface PromotionService {

    Optional<Promotion> getPromotionByCode(String code);

    Optional<Promotion> getPromotionById(UUID id);

    double applyPromotion(Cart cart, String promoCode);
}
