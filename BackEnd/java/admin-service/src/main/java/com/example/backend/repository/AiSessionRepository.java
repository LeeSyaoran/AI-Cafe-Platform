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
public interface AiSessionRepository extends JpaRepository<AiSession, UUID> {
    List<AiSession> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<AiSession> findBySessionType(String sessionType);
    List<AiSession> findByIsActiveTrue();
    long countByIsActiveTrue();

    @Query("SELECT COALESCE(SUM(s.totalTokens), 0) FROM AiSession s")
    Long sumTotalTokens();

    @Query("SELECT COALESCE(SUM(s.totalCost), 0) FROM AiSession s")
    BigDecimal sumTotalCost();
}
