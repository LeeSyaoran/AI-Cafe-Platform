package com.example.backend.controller;

import com.example.backend.entity.MembershipPlan;
import com.example.backend.entity.Subscription;
import com.example.backend.request.CreateSubscriptionRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // GET /v1/subscriptions/plans
    @GetMapping("/subscriptions/plans")
    public ResponseEntity<ApiResponse<List<MembershipPlan>>> getPlans() {
        List<MembershipPlan> plans = subscriptionService.getActivePlans();
        return ResponseEntity.ok(ApiResponse.ok(plans));
    }

    // GET /v1/subscriptions/plans/:id
    @GetMapping("/subscriptions/plans/{id}")
    public ResponseEntity<ApiResponse<MembershipPlan>> getPlan(@PathVariable UUID id) {
        MembershipPlan plan = subscriptionService.getPlan(id);
        return ResponseEntity.ok(ApiResponse.ok(plan));
    }

    // GET /v1/subscriptions/me
    @GetMapping("/subscriptions/me")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @RequestHeader("X-User-ID") UUID userId) {
        Subscription subscription = subscriptionService.getUserSubscription(userId);
        if (subscription == null) {
            return ResponseEntity.ok(ApiResponse.ok(null));
        }
        return ResponseEntity.ok(ApiResponse.ok(toResponse(subscription)));
    }

    // POST /v1/subscriptions
    @PostMapping("/subscriptions")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @Valid @RequestBody CreateSubscriptionRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        Subscription subscription = subscriptionService.subscribe(
                userId,
                request.getPlanId(),
                request.getBillingCycle(),
                request.getPaymentMethod()
        );

        return ResponseEntity.ok(ApiResponse.ok(toResponse(subscription)));
    }

    // POST /v1/subscriptions/cancel
    @PostMapping("/subscriptions/cancel")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription(
            @RequestBody(required = false) java.util.Map<String, String> body,
            @RequestHeader("X-User-ID") UUID userId) {

        String reason = body != null ? body.get("reason") : null;
        Subscription subscription = subscriptionService.cancelSubscription(userId, reason);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(subscription)));
    }

    // POST /v1/subscriptions/pause
    @PostMapping("/subscriptions/pause")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> pauseSubscription(
            @RequestHeader("X-User-ID") UUID userId) {
        Subscription subscription = subscriptionService.pauseSubscription(userId);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(subscription)));
    }

    // POST /v1/subscriptions/resume
    @PostMapping("/subscriptions/resume")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> resumeSubscription(
            @RequestHeader("X-User-ID") UUID userId) {
        Subscription subscription = subscriptionService.resumeSubscription(userId);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(subscription)));
    }

    private SubscriptionResponse toResponse(Subscription s) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .planId(s.getPlan().getId())
                .planName(s.getPlan().getName())
                .status(s.getStatus())
                .billingCycle(s.getBillingCycle())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .creditsRemaining(s.getCreditsRemaining())
                .creditsTotal(s.getCreditsTotal())
                .autoRenew(s.getAutoRenew())
                .build();
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class SubscriptionResponse {
        private UUID id;
        private UUID planId;
        private String planName;
        private String status;
        private String billingCycle;
        private java.time.Instant startDate;
        private java.time.Instant endDate;
        private Integer creditsRemaining;
        private Integer creditsTotal;
        private Boolean autoRenew;
    }
}
