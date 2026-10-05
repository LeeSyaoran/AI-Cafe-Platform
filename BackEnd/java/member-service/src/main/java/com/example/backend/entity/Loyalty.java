package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loyalty_accounts", indexes = {
    @Index(name = "idx_loyalty_accounts_user_id", columnList = "user_id"),
    @Index(name = "idx_loyalty_accounts_tier", columnList = "tier")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    @Builder.Default
    private String tier = "bronze"; // bronze, silver, gold, platinum

    @Column(name = "points_balance", nullable = false)
    @Builder.Default
    private Long pointsBalance = 0L;

    @Column(name = "points_lifetime", nullable = false)
    @Builder.Default
    private Long pointsLifetime = 0L;

    @Column(name = "points_used", nullable = false)
    @Builder.Default
    private Long pointsUsed = 0L;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent")
    @Builder.Default
    private Double totalSpent = 0.0;

    @Column(name = "tier_expires_at")
    private Instant tierExpiresAt;

    @Column(name = "last_earn_at")
    private Instant lastEarnAt;

    @Column(name = "last_redeem_at")
    private Instant lastRedeemAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}

@Entity
@Table(name = "loyalty_transactions", indexes = {
    @Index(name = "idx_loyalty_tx_user_id", columnList = "user_id"),
    @Index(name = "idx_loyalty_tx_type", columnList = "type"),
    @Index(name = "idx_loyalty_tx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String type; // earn, redeem, expire, adjust, refund

    @Column(nullable = false)
    private Long points;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "reward_id")
    private UUID rewardId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    @Builder.Default
    private String status = "completed"; // pending, completed, cancelled

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

@Entity
@Table(name = "rewards", indexes = {
    @Index(name = "idx_rewards_code", columnList = "code"),
    @Index(name = "idx_rewards_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "reward_type")
    private String rewardType; // discount, free_item, free_delivery, credit

    @Column(name = "discount_type")
    private String discountType; // percentage, fixed_amount

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "min_order_amount")
    private Double minOrderAmount;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "free_item_id")
    private UUID freeItemId;

    @Column
    @Builder.Default
    private Integer pointsCost = 0;

    @Column
    @Builder.Default
    private Integer quantity = 1; // null = unlimited

    @Column(name = "quantity_remaining")
    private Integer quantityRemaining;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "validity_days")
    @Builder.Default
    private Integer validityDays = 30;

    @Column(name = "applicable_tiers")
    @Builder.Default
    private String applicableTiers = "bronze,silver,gold,platinum";

    @Column
    @Builder.Default
    private String status = "active"; // active, inactive, expired

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

@Entity
@Table(name = "reward_redemptions", indexes = {
    @Index(name = "idx_redemptions_user_id", columnList = "user_id"),
    @Index(name = "idx_redemptions_reward_id", columnList = "reward_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "reward_id", nullable = false)
    private UUID rewardId;

    @Column(name = "points_spent", nullable = false)
    private Long pointsSpent;

    @Column(name = "order_id")
    private UUID orderId;

    @Column
    @Builder.Default
    private String status = "active"; // active, used, expired, cancelled

    @Column(name = "redeemed_at")
    private Instant redeemedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
