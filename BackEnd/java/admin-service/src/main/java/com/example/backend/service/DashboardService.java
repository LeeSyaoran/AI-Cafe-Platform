package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public interface DashboardService {

    DashboardOverview getOverview(UUID companyId, UUID cafeId, String period);

    List<RevenueChartData> getRevenueChart(UUID companyId, UUID cafeId, String period);

    List<TopProductData> getTopProducts(UUID companyId, UUID cafeId, int limit);

    Map<String, Object> getQuickStats(UUID companyId, UUID cafeId);
}
