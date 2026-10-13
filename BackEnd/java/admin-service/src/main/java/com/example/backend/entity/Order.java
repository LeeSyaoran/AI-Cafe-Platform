package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user_id", columnList = "user_id"),
    @Index(name = "idx_orders_company_id", columnList = "company_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "cafe_id")
    private UUID cafeId;

    @Column(name = "order_number", unique = true)
    private String orderNumber;

    @Column(nullable = false)
    @Builder.Default
    private String status = "pending";

    @Column(name = "order_type", nullable = false)
    @Builder.Default
    private String orderType = "dine_in";

    @Column(name = "table_id")
    private UUID tableId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "subtotal", nullable = false)
    @Builder.Default
    private Double subtotal = 0.0;

    @Column(name = "discount_amount")
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(name = "tax_amount")
    @Builder.Default
    private Double taxAmount = 0.0;

    @Column(name = "delivery_fee")
    @Builder.Default
    private Double deliveryFee = 0.0;

    @Column(name = "total_amount", nullable = false)
    @Builder.Default
    private Double totalAmount = 0.0;

    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(name = "points_redeemed")
    @Builder.Default
    private Integer pointsRedeemed = 0;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    @Builder.Default
    private String paymentStatus = "pending";

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "staff_note", columnDefinition = "TEXT")
    private String staffNote;

    @Column(name = "preparing_at")
    private Instant preparingAt;

    @Column(name = "ready_at")
    private Instant readyAt;
}
