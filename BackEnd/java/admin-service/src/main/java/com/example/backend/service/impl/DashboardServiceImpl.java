package com.example.backend.service.impl;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService  {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardOverview getOverview(UUID companyId, UUID cafeId, String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (period) {
            case "today" -> now;
            case "week" -> now.minusDays(7);
            case "month" -> now.minusDays(30);
            case "year" -> now.minusDays(365);
            default -> now.minusDays(30);
        };

        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        long totalOrders = orderRepository.countOrdersInPeriod(companyId, cafeId, startInstant);
        double totalRevenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, startInstant);
        long totalCustomers = userRepository.countNewCustomersInPeriod(companyId, startInstant);
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        List<Object[]> statusResults = orderRepository.countOrdersByStatus(companyId, cafeId, startInstant);
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Object[] row : statusResults) {
            ordersByStatus.put((String) row[0], ((Number) row[1]).longValue());
        }

        // Calculate growth rates
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, now);
        LocalDate prevStart = startDate.minusDays(daysBetween);
        Instant prevStartInstant = prevStart.atStartOfDay(ZoneId.systemDefault()).toInstant();

        double prevRevenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, prevStartInstant);
        double revenueGrowth = prevRevenue > 0 ? ((totalRevenue - prevRevenue) / prevRevenue) * 100 : 0;

        long prevOrders = orderRepository.countOrdersInPeriod(companyId, cafeId, prevStartInstant);
        double orderGrowth = prevOrders > 0 ? ((double) (totalOrders - prevOrders) / prevOrders) * 100 : 0;

        return DashboardOverview.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalCustomers(totalCustomers)
                .averageOrderValue(averageOrderValue)
                .revenueGrowth(revenueGrowth)
                .orderGrowth(orderGrowth)
                .ordersByStatus(ordersByStatus)
                .period(period)
                .startDate(startInstant)
                .endDate(now.atStartOfDay(ZoneId.systemDefault()).toInstant())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RevenueChartData> getRevenueChart(UUID companyId, UUID cafeId, String period) {
        LocalDate now = LocalDate.now();
        List<RevenueChartData> data = new ArrayList<>();

        int days = switch (period) {
            case "week" -> 7;
            case "month" -> 30;
            case "year" -> 12;
            default -> 30;
        };

        if ("year".equals(period)) {
            for (int i = 11; i >= 0; i--) {
                LocalDate month = now.minusMonths(i);
                Instant start = month.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                Instant end = month.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

                double revenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, start);
                long orders = orderRepository.countOrdersInPeriod(companyId, cafeId, start);

                data.add(RevenueChartData.builder()
                        .date(month.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .revenue(revenue)
                        .orders(orders)
                        .build());
            }
        } else {
            for (int i = days - 1; i >= 0; i--) {
                LocalDate day = now.minusDays(i);
                Instant start = day.atStartOfDay(ZoneId.systemDefault()).toInstant();

                double revenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, start);
                long orders = orderRepository.countOrdersInPeriod(companyId, cafeId, start);

                data.add(RevenueChartData.builder()
                        .date(day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .revenue(revenue)
                        .orders(orders)
                        .build());
            }
        }

        return data;
    }

    @Transactional(readOnly = true)
    public List<TopProductData> getTopProducts(UUID companyId, UUID cafeId, int limit) {
        List<Object[]> results = orderRepository.findTopProducts(companyId, cafeId);
        return results.stream()
                .map(row -> TopProductData.builder()
                        .productId((UUID) row[0])
                        .productName("Product")
                        .quantitySold(((Number) row[1]).longValue())
                        .revenue(((Number) row[2]).doubleValue())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getQuickStats(UUID companyId, UUID cafeId) {
        Map<String, Object> stats = new HashMap<>();
        Instant todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        stats.put("pendingOrders", orderRepository.countByStatus(companyId, cafeId, "pending"));
        stats.put("preparingOrders", orderRepository.countByStatus(companyId, cafeId, "preparing"));
        stats.put("readyOrders", orderRepository.countByStatus(companyId, cafeId, "ready"));
        stats.put("todayOrders", orderRepository.countOrdersInPeriod(companyId, cafeId, todayStart));
        stats.put("todayRevenue", orderRepository.sumRevenueInPeriod(companyId, cafeId, todayStart));

        return stats;
    }
}
