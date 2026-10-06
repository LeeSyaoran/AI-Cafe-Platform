package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loyalty_transactions", indexes = {
    @Index(name = "idx_loyalty_txn_user_id", columnList = "user_id"),
    @Index(name = "idx_loyalty_txn_type", columnList = "type"),
    @Index(name = "idx_loyalty_txn_created", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String type; // earn, redeem, expire, adjust, bonus, tier_upgrade

    @Column(nullable = false)
    private Integer points;

    @Column(name = "balance_after")
    private Integer balanceAfter;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "reward_id")
    private UUID rewardId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    @Builder.Default
    private Boolean expired = false;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
