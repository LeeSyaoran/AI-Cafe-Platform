package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_payment_tx_payment_id", columnList = "payment_id"),
    @Index(name = "idx_payment_tx_type", columnList = "transaction_type")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // CREATE, CALLBACK, REFUND, CANCEL

    @Column(nullable = false)
    private String status; // SUCCESS, FAILED

    @Column(columnDefinition = "TEXT")
    private String requestData;

    @Column(columnDefinition = "TEXT")
    private String responseData;

    @Column
    private String provider; // VNPAY, MOMO, ZALOPAY

    @Column(name = "provider_transaction_id")
    private String providerTransactionId;

    @Column
    private String errorCode;

    @Column
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
