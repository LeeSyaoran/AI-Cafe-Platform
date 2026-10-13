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

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverview {
    private long totalOrders;
    private double totalRevenue;
    private long totalCustomers;
    private double averageOrderValue;
    private double revenueGrowth;
    private double orderGrowth;
    private Map<String, Long> ordersByStatus;
    private String period;
    private Instant startDate;
    private Instant endDate;
}
