package com.example.backend.mapper;

import com.example.backend.dto.response.SubscriptionResponse;
import com.example.backend.entity.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionResponse toResponse(Subscription s) {
        if (s == null) {
            return null;
        }

        return SubscriptionResponse.builder()
                .id(s.getId())
                .planId(s.getPlan() != null ? s.getPlan().getId() : null)
                .planName(s.getPlan() != null ? s.getPlan().getName() : null)
                .status(s.getStatus())
                .billingCycle(s.getBillingCycle())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .creditsRemaining(s.getCreditsRemaining())
                .creditsTotal(s.getCreditsTotal())
                .autoRenew(s.getAutoRenew())
                .build();
    }
}
