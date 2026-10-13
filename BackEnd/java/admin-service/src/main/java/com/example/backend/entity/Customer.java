package com.example.backend.entity;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "loyalty_tier_id")
    private UUID loyaltyTierId;

    @Column(name = "loyalty_points")
    @Builder.Default
    private Long loyaltyPoints = 0L;

    @Column(name = "lifetime_points")
    @Builder.Default
    private Long lifetimePoints = 0L;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent", precision = 14, scale = 0)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "avg_order_value", precision = 12, scale = 0)
    @Builder.Default
    private BigDecimal avgOrderValue = BigDecimal.ZERO;

    @Column(name = "visit_count")
    @Builder.Default
    private Integer visitCount = 0;

    @Column(name = "last_visit_at")
    private Instant lastVisitAt;

    @Column(name = "first_order_at")
    private Instant firstOrderAt;

    @Column(name = "preferred_payment", length = 50)
    private String preferredPayment;

    @Column(name = "preferred_cafe_id")
    private UUID preferredCafeId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 20)
    @Builder.Default
    private String status = "active"; // active, vip, churned, blacklisted

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
