package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "promotions", indexes = {
    @Index(name = "idx_promotions_company_id", columnList = "company_id"),
    @Index(name = "idx_promotions_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

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
    private String type; // percent, fixed, bogo, free_delivery

    @Column(name = "discount_value")
    private Double discountValue;

    // Alias for backward compatibility (builder uses this name)
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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Alias for backward compatibility
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
