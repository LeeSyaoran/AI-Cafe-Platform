package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "membership_plans", indexes = {
    @Index(name = "idx_membership_plans_code", columnList = "code", unique = true),
    @Index(name = "idx_membership_plans_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MembershipPlan extends BaseEntity {

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
}
