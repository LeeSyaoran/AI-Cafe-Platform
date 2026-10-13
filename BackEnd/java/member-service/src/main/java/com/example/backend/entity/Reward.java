package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rewards", indexes = {
    @Index(name = "idx_rewards_company_id", columnList = "company_id"),
    @Index(name = "idx_rewards_category", columnList = "category"),
    @Index(name = "idx_rewards_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reward extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category; // discount, free_item, free_delivery, upgrade

    @Column(name = "points_required")
    private Integer pointsRequired;

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
}
