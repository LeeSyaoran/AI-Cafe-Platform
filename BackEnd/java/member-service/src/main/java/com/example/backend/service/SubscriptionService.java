package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MembershipPlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final UserCreditRepository creditRepository;

    @Transactional(readOnly = true)
    public List<MembershipPlan> getActivePlans() {
        return planRepository.findByStatusOrderBySortOrder("active");
    }

    @Transactional(readOnly = true)
    public MembershipPlan getPlan(UUID planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), "PLAN_NOT_FOUND", "Plan not found"));
    }

    @Transactional(readOnly = true)
    public Subscription getUserSubscription(UUID userId) {
        return subscriptionRepository.findByUserIdWithPlan(userId)
                .orElse(null);
    }

    @Transactional
    public Subscription subscribe(UUID userId, UUID planId, String billingCycle, String paymentMethod) {
        // Check if user already has subscription
        subscriptionRepository.findByUserIdWithPlan(userId).ifPresent(existing -> {
            if ("active".equals(existing.getStatus()) || "trial".equals(existing.getStatus())) {
                throw new ApiException(HttpStatus.CONFLICT.value(), "ALREADY_SUBSCRIBED", "User already has an active subscription");
            }
        });

        MembershipPlan plan = getPlan(planId);
        Instant now = Instant.now();

        // Calculate dates based on billing cycle
        Instant endDate = "yearly".equals(billingCycle)
                ? now.plusSeconds(365 * 24 * 60 * 60L)
                : now.plusSeconds(30 * 24 * 60 * 60L);

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .plan(plan)
                .status("active")
                .billingCycle(billingCycle)
                .startDate(now)
                .endDate(endDate)
                .creditsRemaining(plan.getCreditsMonthly())
                .creditsTotal(plan.getCreditsMonthly())
                .autoRenew(true)
                .build();

        subscription = subscriptionRepository.save(subscription);

        // Create loyalty account if not exists
        ensureLoyaltyAccount(userId);

        // Add credits
        addCredits(userId, plan.getCreditsMonthly());

        log.info("Created subscription {} for user {} with plan {}", subscription.getId(), userId, plan.getCode());
        return subscription;
    }

    @Transactional
    public Subscription cancelSubscription(UUID userId, String reason) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.value(), "NO_SUBSCRIPTION", "No active subscription found");
        }

        subscription.setStatus("cancelled");
        subscription.setCancelledAt(Instant.now());
        subscription.setCancellationReason(reason);
        subscription.setAutoRenew(false);

        log.info("Cancelled subscription {} for user {}", subscription.getId(), userId);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription pauseSubscription(UUID userId) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null) {
            throw new ApiException(HttpStatus.NOT_FOUND.value(), "NO_SUBSCRIPTION", "No active subscription found");
        }

        subscription.setStatus("paused");
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription resumeSubscription(UUID userId) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null || !"paused".equals(subscription.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "NOT_PAUSED", "Subscription is not paused");
        }

        subscription.setStatus("active");
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public boolean useCredits(UUID userId, int amount) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null || !"active".equals(subscription.getStatus())) {
            return false;
        }

        if (subscription.getCreditsRemaining() < amount) {
            return false;
        }

        subscription.setCreditsRemaining(subscription.getCreditsRemaining() - amount);
        subscriptionRepository.save(subscription);
        return true;
    }

    @Transactional
    public void renewSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), "SUBSCRIPTION_NOT_FOUND", "Subscription not found"));

        MembershipPlan plan = subscription.getPlan();
        Instant now = Instant.now();
        Instant newEndDate = "yearly".equals(subscription.getBillingCycle())
                ? subscription.getEndDate().plusSeconds(365 * 24 * 60 * 60L)
                : subscription.getEndDate().plusSeconds(30 * 24 * 60 * 60L);

        subscription.setStartDate(subscription.getEndDate());
        subscription.setEndDate(newEndDate);
        subscription.setCreditsRemaining(plan.getCreditsMonthly());
        subscription.setCreditsTotal(plan.getCreditsMonthly());
        subscription.setNextBillingDate(newEndDate);

        subscriptionRepository.save(subscription);

        // Add credits
        addCredits(subscription.getUserId(), plan.getCreditsMonthly());

        log.info("Renewed subscription {} for user {}", subscription.getId(), subscription.getUserId());
    }

    @Transactional
    public void processExpiredSubscriptions() {
        Instant now = Instant.now();
        List<Subscription> expired = subscriptionRepository.findExpiredSubscriptions(now);

        for (Subscription subscription : expired) {
            if (subscription.getAutoRenew()) {
                // Auto renew
                renewSubscription(subscription.getId());
            } else {
                subscription.setStatus("expired");
                subscriptionRepository.save(subscription);
                log.info("Subscription {} expired for user {}", subscription.getId(), subscription.getUserId());
            }
        }
    }

    private void ensureLoyaltyAccount(UUID userId) {
        if (loyaltyAccountRepository.findByUserId(userId).isEmpty()) {
            LoyaltyAccount account = LoyaltyAccount.builder()
                    .userId(userId)
                    .tier("bronze")
                    .build();
            loyaltyAccountRepository.save(account);
        }
    }

    private void addCredits(UUID userId, int amount) {
        UserCredit credit = creditRepository.findByUserId(userId)
                .orElse(UserCredit.builder().userId(userId).balance(0.0).lifetimeEarned(0.0).lifetimeUsed(0.0).build());

        credit.setBalance(credit.getBalance() + amount);
        credit.setLifetimeEarned(credit.getLifetimeEarned() + amount);
        credit.setLastTransactionAt(Instant.now());
        creditRepository.save(credit);
    }
}
