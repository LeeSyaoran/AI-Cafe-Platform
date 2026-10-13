package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import java.util.List;
import java.util.UUID;

public interface PromotionService {

    Promotion createPromotion(Promotion promotion);

    Promotion updatePromotion(UUID promotionId, Promotion updates);

    void deletePromotion(UUID promotionId);

    List<Promotion> getPromotions();

    Promotion getPromotion(UUID promotionId);

    Reward createReward(Reward reward);

    Reward updateReward(UUID rewardId, Reward updates);

    List<Reward> getRewards();

    Reward getReward(UUID rewardId);
}
