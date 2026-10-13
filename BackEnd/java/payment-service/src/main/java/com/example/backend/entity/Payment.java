package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_user_id", columnList = "user_id"),
    @Index(name = "idx_payments_order_id", columnList = "order_id"),
    @Index(name = "idx_payments_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_payments_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    @Builder.Default
    private String status = "pending";

    @Column(nullable = false)
    private Double amount;

    @Column(name = "currency", nullable = false)
    @Builder.Default
    private String currency = "VND";

    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "return_url")
    private String returnUrl;

    @Column(name = "callback_data", columnDefinition = "TEXT")
    private String callbackData;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(columnDefinition = "TEXT")
    private String note;
}
