package com.example.backend.repository;

import com.example.backend.entity.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, UUID> {

    Optional<UserCredit> findByUserId(UUID userId);

    @Modifying
    @Query("UPDATE UserCredit c SET c.balance = c.balance + :amount, " +
           "c.lifetimeEarned = c.lifetimeEarned + :amount, " +
           "c.lastTransactionAt = CURRENT_TIMESTAMP WHERE c.userId = :userId")
    int addCredits(UUID userId, Double amount);

    @Modifying
    @Query("UPDATE UserCredit c SET c.balance = c.balance - :amount, " +
           "c.lifetimeUsed = c.lifetimeUsed + :amount, " +
           "c.lastTransactionAt = CURRENT_TIMESTAMP WHERE c.userId = :userId AND c.balance >= :amount")
    int deductCredits(UUID userId, Double amount);
}

package com.example.backend.repository;

import com.example.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, java.util.UUID> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true, r.revokedAt = CURRENT_TIMESTAMP WHERE r.userId = :userId")
    void revokeAllByUserId(java.util.UUID userId);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true, r.revokedAt = CURRENT_TIMESTAMP WHERE r.token = :token")
    void revokeByToken(String token);
}