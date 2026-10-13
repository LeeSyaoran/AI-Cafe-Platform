package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reward_redemptions", indexes = {
    @Index(name = "idx_redemptions_user_id", columnList = "user_id"),
    @Index(name = "idx_redemptions_reward_id", columnList = "reward_id"),
    @Index(name = "idx_redemptions_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RewardRedemption extends BaseCreatedEntity {

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
}
