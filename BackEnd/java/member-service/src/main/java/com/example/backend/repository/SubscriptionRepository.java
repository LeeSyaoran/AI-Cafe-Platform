package com.example.backend.repository;

import com.example.backend.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByUserId(UUID userId);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.plan WHERE s.userId = :userId")
    Optional<Subscription> findByUserIdWithPlan(UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'active' AND s.endDate < :now")
    List<Subscription> findExpiredSubscriptions(Instant now);
}
