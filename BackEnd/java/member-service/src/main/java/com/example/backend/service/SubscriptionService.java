package com.example.backend.service;

import com.example.backend.entity.MembershipPlan;
import com.example.backend.entity.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    List<MembershipPlan> getActivePlans();

    MembershipPlan getPlan(UUID planId);

    Subscription getUserSubscription(UUID userId);

    Subscription subscribe(UUID userId, UUID planId, String billingCycle, String paymentMethod);

    Subscription cancelSubscription(UUID userId, String reason);

    Subscription pauseSubscription(UUID userId);

    Subscription resumeSubscription(UUID userId);

    boolean useCredits(UUID userId, int amount);

    void renewSubscription(UUID subscriptionId);

    void processExpiredSubscriptions();
}
