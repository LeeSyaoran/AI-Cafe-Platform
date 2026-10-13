package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;


import java.util.List;
import java.util.UUID;

public interface AiManagementService {
    List<AiSession> getAllSessions(String sessionType, Boolean activeOnly);
    AiSession getSessionById(UUID id);
    List<AiSession> getSessionsByUserId(UUID userId);
    void terminateSession(UUID id);

    List<AiUsage> getUsageHistory(UUID userId, UUID sessionId, String modelId);
    AiAnalyticsResponse getAiAnalytics();
}
