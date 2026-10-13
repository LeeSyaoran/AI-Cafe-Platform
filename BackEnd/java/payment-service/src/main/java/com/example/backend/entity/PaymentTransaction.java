package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_payment_txn_payment_id", columnList = "payment_id"),
    @Index(name = "idx_payment_txn_type", columnList = "type"),
    @Index(name = "idx_payment_txn_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentTransaction extends BaseCreatedEntity {

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private String type; // create, callback, refund

    @Column(nullable = false)
    private String status; // success, failed, pending

    private String provider;

    @Column(name = "provider_transaction_id")
    private String providerTransactionId;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData;

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;

    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
