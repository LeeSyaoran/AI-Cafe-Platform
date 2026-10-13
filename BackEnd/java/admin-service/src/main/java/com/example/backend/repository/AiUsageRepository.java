package com.example.backend.repository;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AiUsageRepository extends JpaRepository<AiUsage, UUID> {
    List<AiUsage> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<AiUsage> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
    List<AiUsage> findByModelId(String modelId);

    @Query("SELECT COALESCE(SUM(u.totalTokens), 0) FROM AiUsage u")
    Long sumTotalTokens();

    @Query("SELECT COALESCE(SUM(u.cost), 0) FROM AiUsage u")
    BigDecimal sumTotalCost();

    @Query("SELECT u.modelId, COUNT(u), SUM(u.totalTokens), SUM(u.cost) FROM AiUsage u GROUP BY u.modelId")
    List<Object[]> findUsageStatsByModel();

    @Query("SELECT u.provider, COUNT(u), SUM(u.totalTokens), SUM(u.cost) FROM AiUsage u GROUP BY u.provider")
    List<Object[]> findUsageStatsByProvider();
}
