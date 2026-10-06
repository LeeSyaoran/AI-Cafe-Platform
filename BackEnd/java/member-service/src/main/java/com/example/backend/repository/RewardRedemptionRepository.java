package com.example.backend.repository;

import com.example.backend.entity.RewardRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, UUID> {

    List<RewardRedemption> findByUserId(UUID userId);

    List<RewardRedemption> findByUserIdAndStatus(UUID userId, String status);
}
