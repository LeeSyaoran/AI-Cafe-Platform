package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_credits", indexes = {
    @Index(name = "idx_user_credits_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserCredit extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    @Builder.Default
    private Double balance = 0.0;

    @Column(name = "lifetime_earned")
    @Builder.Default
    private Double lifetimeEarned = 0.0;

    @Column(name = "lifetime_used")
    @Builder.Default
    private Double lifetimeUsed = 0.0;

    @Column(name = "last_transaction_at")
    private Instant lastTransactionAt;
}
