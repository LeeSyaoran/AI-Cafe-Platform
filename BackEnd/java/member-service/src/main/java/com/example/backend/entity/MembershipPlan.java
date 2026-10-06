package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "membership_plans", indexes = {
    @Index(name = "idx_membership_plans_code", columnList = "code"),
    @Index(name = "idx_membership_plans_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_monthly")
    private Double priceMonthly;

    @Column(name = "price_yearly")
    private Double priceYearly;

    @Column(name = "billing_cycle")
    @Builder.Default
    private String billingCycle = "monthly";

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "credits_monthly")
    private Integer creditsMonthly;

    @Column(name = "discount_percent")
    @Builder.Default
    private Double discountPercent = 0.0;

    @Column(name = "free_delivery")
    @Builder.Default
    private Boolean freeDelivery = false;

    @Column(name = "priority_support")
    @Builder.Default
    private Boolean prioritySupport = false;

    @Column
    @Builder.Default
    private Integer sortOrder = 0;

    @Column
    @Builder.Default
    private String status = "active";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
