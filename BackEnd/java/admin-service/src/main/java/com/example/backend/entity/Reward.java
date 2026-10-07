package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rewards", indexes = {
    @Index(name = "idx_rewards_company_id", columnList = "company_id"),
    @Index(name = "idx_rewards_category", columnList = "category"),
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

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category; // discount, free_item, free_delivery, upgrade

    // Builder-friendly field name (same as category)
    @Column(name = "reward_type")
    private String rewardType;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "points_required")
    private Integer pointsRequired;

    @Column(name = "code")
    private String code;

    @Column(columnDefinition = "TEXT")
    private String benefits; // JSON

    @Column(name = "min_tier")
    @Builder.Default
    private String minTier = "bronze"; // bronze, silver, gold, platinum

    @Column(name = "max_redemptions_per_user")
    @Builder.Default
    private Integer maxRedemptionsPerUser = 1;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column
    @Builder.Default
    private String status = "active"; // active, inactive, expired

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Alias methods for backward compatibility
    public Integer getPointsCost() {
        return pointsRequired;
    }

    public void setPointsCost(Integer pointsCost) {
        this.pointsRequired = pointsCost;
    }

    public Integer getQuantity() {
        return totalQuantity;
    }

    public void setQuantity(Integer quantity) {
        this.totalQuantity = quantity;
    }

    public Integer getQuantityRemaining() {
        return remainingQuantity;
    }

    public void setQuantityRemaining(Integer remaining) {
        this.remainingQuantity = remaining;
    }

    public String getRewardType() {
        return rewardType != null ? rewardType : category;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
        // Don't auto-set category - they are separate fields
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getValidityDays() {
        return null; // Not stored
    }

    public void setValidityDays(Integer validityDays) {
        // No-op
    }

    public String getApplicableTiers() {
        return null;
    }

    public void setApplicableTiers(String applicableTiers) {
        // No-op
    }
}
