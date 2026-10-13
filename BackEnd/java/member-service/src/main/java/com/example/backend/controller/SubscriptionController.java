package com.example.backend.controller;

import com.example.backend.dto.request.CreateSubscriptionRequest;
import com.example.backend.dto.response.SubscriptionResponse;
import com.example.backend.entity.MembershipPlan;
import com.example.backend.entity.Subscription;
import com.example.backend.mapper.SubscriptionMapper;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;

    @GetMapping("/subscriptions/plans")
    public ResponseEntity<ApiResponse<List<MembershipPlan>>> getPlans() {
        List<MembershipPlan> plans = subscriptionService.getActivePlans();
        return ResponseEntity.ok(ApiResponse.ok(plans));
    }

    @GetMapping("/subscriptions/plans/{id}")
    public ResponseEntity<ApiResponse<MembershipPlan>> getPlan(@PathVariable UUID id) {
        MembershipPlan plan = subscriptionService.getPlan(id);
        return ResponseEntity.ok(ApiResponse.ok(plan));
    }

    @GetMapping("/subscriptions/me")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @RequestHeader("X-User-ID") UUID userId) {
        Subscription subscription = subscriptionService.getUserSubscription(userId);
        return ResponseEntity.ok(ApiResponse.ok(subscription != null ? subscriptionMapper.toResponse(subscription) : null));
    }

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
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(subscriptionMapper.toResponse(subscription)));
    }

    @PostMapping("/subscriptions/cancel")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription(
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader("X-User-ID") UUID userId) {
        String reason = body != null ? body.get("reason") : null;
        Subscription subscription = subscriptionService.cancelSubscription(userId, reason);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionMapper.toResponse(subscription)));
    }

    @PostMapping("/subscriptions/pause")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> pauseSubscription(
            @RequestHeader("X-User-ID") UUID userId) {
        Subscription subscription = subscriptionService.pauseSubscription(userId);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionMapper.toResponse(subscription)));
    }

    @PostMapping("/subscriptions/resume")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> resumeSubscription(
            @RequestHeader("X-User-ID") UUID userId) {
        Subscription subscription = subscriptionService.resumeSubscription(userId);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionMapper.toResponse(subscription)));
    }
}
