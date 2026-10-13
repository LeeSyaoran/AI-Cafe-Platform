package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "promotions", indexes = {
    @Index(name = "idx_promotions_company_id", columnList = "company_id"),
    @Index(name = "idx_promotions_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Promotion extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "min_order_amount")
    private Double minOrderAmount;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column
    @Builder.Default
    private String status = "active";

    @Column(name = "code")
    private String code;

    public String getDiscountType() {
        return discountType != null ? discountType : type;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
        this.type = discountType;
    }

    public String getStatus() {
        return isActive != null && isActive ? "active" : "inactive";
    }

    public void setStatus(String status) {
        this.isActive = "active".equals(status);
    }
}
