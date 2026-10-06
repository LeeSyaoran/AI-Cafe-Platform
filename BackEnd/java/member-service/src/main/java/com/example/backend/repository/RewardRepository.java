package com.example.backend.repository;

import com.example.backend.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardRepository extends JpaRepository<Reward, UUID> {

    Optional<Reward> findByCode(String code);

    @Query("SELECT r FROM Reward r WHERE r.status = 'active' AND (r.startDate IS NULL OR r.startDate <= :now) AND (r.endDate IS NULL OR r.endDate >= :now)")
    List<Reward> findActiveRewards(Instant now);
}
