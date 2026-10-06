package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reward_redemptions", indexes = {
    @Index(name = "idx_redemptions_user_id", columnList = "user_id"),
    @Index(name = "idx_redemptions_reward_id", columnList = "reward_id"),
    @Index(name = "idx_redemptions_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "reward_id", nullable = false)
    private UUID rewardId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(nullable = false)
    private Integer pointsSpent;

    @Column(nullable = false)
    private String status; // pending, used, expired, cancelled

    @Column(name = "code")
    private String code;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "valid_from")
    private Instant validFrom;

    @Column(name = "valid_until")
    private Instant validUntil;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
