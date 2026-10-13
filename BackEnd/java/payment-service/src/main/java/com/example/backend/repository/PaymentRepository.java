package com.example.backend.repository;

import com.example.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findByUserId(UUID userId);

    List<Payment> findByUserIdAndStatus(UUID userId, String status);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.expiredAt < :now")
    List<Payment> findExpiredPayments(@Param("status") String status, @Param("now") Instant now);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userId = :userId AND p.status = 'completed' AND p.createdAt BETWEEN :start AND :end")
    Double sumAmountByUserAndPeriod(@Param("userId") UUID userId, @Param("start") Instant start, @Param("end") Instant end);

    List<Payment> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
