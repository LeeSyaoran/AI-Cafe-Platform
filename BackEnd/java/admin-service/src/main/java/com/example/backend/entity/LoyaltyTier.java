package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loyalty_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LoyaltyTier extends BaseCreatedEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "name_vi", length = 50)
    private String nameVi;

    @Column(name = "min_points", nullable = false)
    @Builder.Default
    private Integer minPoints = 0;

    @Column(name = "max_points")
    private Integer maxPoints;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "points_multiplier", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal pointsMultiplier = BigDecimal.ONE;

    @Column(length = 20)
    private String color;

    @Column(length = 10)
    private String icon;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}
