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
    @Index(name = "idx_loyalty_user_id", columnList = "user_id"),
    @Index(name = "idx_loyalty_tier", columnList = "tier")
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

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    @Builder.Default
    private String tier = "bronze"; // bronze, silver, gold, platinum

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    @Column(name = "lifetime_points")
    @Builder.Default
    private Integer lifetimePoints = 0;

    @Column(name = "points_to_next_tier")
    @Builder.Default
    private Integer pointsToNextTier = 100;

    @Column(name = "next_tier")
    @Builder.Default
    private String nextTier = "silver";

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent")
    @Builder.Default
    private Double totalSpent = 0.0;

    @Column(name = "birthday_bonus_claimed")
    @Builder.Default
    private Boolean birthdayBonusClaimed = false;

    @Column(name = "anniversary_bonus_claimed")
    @Builder.Default
    private Boolean anniversaryBonusClaimed = false;

    @Column(name = "tier_upgraded_at")
    private Instant tierUpgradedAt;

    @Column(name = "tier_expires_at")
    private Instant tierExpiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
