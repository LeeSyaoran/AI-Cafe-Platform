package com.example.backend.service.impl;

import com.example.backend.entity.LoyaltyAccount;
import com.example.backend.entity.MembershipPlan;
import com.example.backend.entity.Subscription;
import com.example.backend.entity.UserCredit;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.LoyaltyAccountRepository;
import com.example.backend.repository.MembershipPlanRepository;
import com.example.backend.repository.SubscriptionRepository;
import com.example.backend.repository.UserCreditRepository;
import com.example.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MembershipPlanRepository planRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final UserCreditRepository creditRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlan> getActivePlans() {
        return planRepository.findByStatusOrderBySortOrder("active");
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipPlan getPlan(UUID planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", planId));
    }

    @Override
    @Transactional(readOnly = true)
    public Subscription getUserSubscription(UUID userId) {
        return subscriptionRepository.findByUserIdWithPlan(userId).orElse(null);
    }

    @Override
    @Transactional
    public Subscription subscribe(UUID userId, UUID planId, String billingCycle, String paymentMethod) {
        subscriptionRepository.findByUserIdWithPlan(userId).ifPresent(existing -> {
            if ("active".equalsIgnoreCase(existing.getStatus()) || "trial".equalsIgnoreCase(existing.getStatus())) {
                throw new ConflictException("ALREADY_SUBSCRIBED", "User already has an active subscription");
            }
        });

        MembershipPlan plan = getPlan(planId);
        Instant now = Instant.now();

        Instant endDate = "yearly".equalsIgnoreCase(billingCycle)
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
        ensureLoyaltyAccount(userId);
        addCredits(userId, plan.getCreditsMonthly() != null ? plan.getCreditsMonthly() : 0);

        log.info("Created subscription {} for user {} with plan {}", subscription.getId(), userId, plan.getCode());
        return subscription;
    }

    @Override
    @Transactional
    public Subscription cancelSubscription(UUID userId, String reason) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null) {
            throw new ResourceNotFoundException("Subscription", "userId", userId);
        }

        subscription.setStatus("cancelled");
        subscription.setCancelledAt(Instant.now());
        subscription.setCancellationReason(reason);
        subscription.setAutoRenew(false);

        log.info("Cancelled subscription {} for user {}", subscription.getId(), userId);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription pauseSubscription(UUID userId) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null) {
            throw new ResourceNotFoundException("Subscription", "userId", userId);
        }

        subscription.setStatus("paused");
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription resumeSubscription(UUID userId) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null || !"paused".equalsIgnoreCase(subscription.getStatus())) {
            throw new BadRequestException("NOT_PAUSED", "Subscription is not currently paused");
        }

        subscription.setStatus("active");
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public boolean useCredits(UUID userId, int amount) {
        Subscription subscription = getUserSubscription(userId);
        if (subscription == null || !"active".equalsIgnoreCase(subscription.getStatus())) {
            return false;
        }

        int remaining = subscription.getCreditsRemaining() != null ? subscription.getCreditsRemaining() : 0;
        if (remaining < amount) {
            return false;
        }

        subscription.setCreditsRemaining(remaining - amount);
        subscriptionRepository.save(subscription);
        return true;
    }

    @Override
    @Transactional
    public void renewSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));

        MembershipPlan plan = subscription.getPlan();
        Instant newEndDate = "yearly".equalsIgnoreCase(subscription.getBillingCycle())
                ? subscription.getEndDate().plusSeconds(365 * 24 * 60 * 60L)
                : subscription.getEndDate().plusSeconds(30 * 24 * 60 * 60L);

        subscription.setStartDate(subscription.getEndDate());
        subscription.setEndDate(newEndDate);
        subscription.setCreditsRemaining(plan.getCreditsMonthly());
        subscription.setCreditsTotal(plan.getCreditsMonthly());
        subscription.setNextBillingDate(newEndDate);

        subscriptionRepository.save(subscription);
        addCredits(subscription.getUserId(), plan.getCreditsMonthly() != null ? plan.getCreditsMonthly() : 0);

        log.info("Renewed subscription {} for user {}", subscription.getId(), subscription.getUserId());
    }

    @Override
    @Transactional
    public void processExpiredSubscriptions() {
        Instant now = Instant.now();
        List<Subscription> expired = subscriptionRepository.findExpiredSubscriptions(now);

        for (Subscription subscription : expired) {
            if (Boolean.TRUE.equals(subscription.getAutoRenew())) {
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
                    .points(0)
                    .lifetimePoints(0)
                    .totalOrders(0)
                    .totalSpent(0.0)
                    .build();
            loyaltyAccountRepository.save(account);
        }
    }

    private void addCredits(UUID userId, int amount) {
        UserCredit credit = creditRepository.findByUserId(userId)
                .orElse(UserCredit.builder().userId(userId).balance(0.0).lifetimeEarned(0.0).lifetimeUsed(0.0).build());

        double currentBal = credit.getBalance() != null ? credit.getBalance() : 0.0;
        double currentEarned = credit.getLifetimeEarned() != null ? credit.getLifetimeEarned() : 0.0;

        credit.setBalance(currentBal + amount);
        credit.setLifetimeEarned(currentEarned + amount);
        credit.setLastTransactionAt(Instant.now());
        creditRepository.save(credit);
    }
}
