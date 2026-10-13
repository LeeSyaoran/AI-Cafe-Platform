package com.example.backend.service.impl;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AiManagementServiceImpl implements AiManagementService {

    private final AiSessionRepository aiSessionRepository;
    private final AiUsageRepository aiUsageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AiSession> getAllSessions(String sessionType, Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return aiSessionRepository.findByIsActiveTrue();
        }
        if (sessionType != null && !sessionType.isBlank()) {
            return aiSessionRepository.findBySessionType(sessionType);
        }
        return aiSessionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AiSession getSessionById(UUID id) {
        return aiSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("AI Session not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiSession> getSessionsByUserId(UUID userId) {
        return aiSessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void terminateSession(UUID id) {
        AiSession session = getSessionById(id);
        session.setIsActive(false);
        aiSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiUsage> getUsageHistory(UUID userId, UUID sessionId, String modelId) {
        if (userId != null) {
            return aiUsageRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        if (sessionId != null) {
            return aiUsageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        }
        if (modelId != null && !modelId.isBlank()) {
            return aiUsageRepository.findByModelId(modelId);
        }
        return aiUsageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AiAnalyticsResponse getAiAnalytics() {
        long totalSessions = aiSessionRepository.count();
        long activeSessions = aiSessionRepository.countByIsActiveTrue();
        Long totalTokens = aiUsageRepository.sumTotalTokens();
        BigDecimal totalCost = aiUsageRepository.sumTotalCost();

        List<Object[]> rawModelStats = aiUsageRepository.findUsageStatsByModel();
        List<Map<String, Object>> usageByModel = new ArrayList<>();
        for (Object[] row : rawModelStats) {
            Map<String, Object> item = new HashMap<>();
            item.put("model", row[0] != null ? row[0] : "unknown");
            item.put("calls", row[1]);
            item.put("tokens", row[2] != null ? row[2] : 0L);
            item.put("cost", row[3] != null ? row[3] : BigDecimal.ZERO);
            usageByModel.add(item);
        }

        List<Object[]> rawProviderStats = aiUsageRepository.findUsageStatsByProvider();
        List<Map<String, Object>> usageByProvider = new ArrayList<>();
        for (Object[] row : rawProviderStats) {
            Map<String, Object> item = new HashMap<>();
            item.put("provider", row[0] != null ? row[0] : "unknown");
            item.put("calls", row[1]);
            item.put("tokens", row[2] != null ? row[2] : 0L);
            item.put("cost", row[3] != null ? row[3] : BigDecimal.ZERO);
            usageByProvider.add(item);
        }

        return AiAnalyticsResponse.builder()
                .totalSessions(totalSessions)
                .activeSessions(activeSessions)
                .totalTokens(totalTokens != null ? totalTokens : 0L)
                .totalCost(totalCost != null ? totalCost : BigDecimal.ZERO)
                .usageByModel(usageByModel)
                .usageByProvider(usageByProvider)
                .build();
    }
}
