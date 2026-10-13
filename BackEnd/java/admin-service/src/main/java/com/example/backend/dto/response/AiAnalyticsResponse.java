package com.example.backend.dto.response;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalyticsResponse {
    private long totalSessions;
    private long activeSessions;
    private long totalTokens;
    private BigDecimal totalCost;
    private List<Map<String, Object>> usageByModel;
    private List<Map<String, Object>> usageByProvider;
}
