package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public DashboardOverview getOverview(UUID companyId, UUID cafeId, String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case "today" -> startDate = now;
            case "week" -> startDate = now.minusDays(7);
            case "month" -> startDate = now.minusDays(30);
            case "year" -> startDate = now.minusDays(365);
            default -> startDate = now.minusDays(30);
        }

        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        // Get counts and sums
        long totalOrders = orderRepository.countOrdersInPeriod(companyId, cafeId, startInstant);
        double totalRevenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, startInstant);
        long totalCustomers = userRepository.countNewCustomersInPeriod(companyId, startInstant);
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        // Get order status breakdown
        List<Object[]> statusResults = orderRepository.countOrdersByStatus(companyId, cafeId, startInstant);
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Object[] row : statusResults) {
            ordersByStatus.put((String) row[0], ((Number) row[1]).longValue());
        }

        // Calculate growth rates (compare with previous period)
        LocalDate prevStart = startDate.minusDays(java.time.temporal.ChronoUnit.DAYS.between(startDate, now));
        Instant prevStartInstant = prevStart.atStartOfDay(ZoneId.systemDefault()).toInstant();

        double prevRevenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, prevStartInstant);
        double revenueGrowth = prevRevenue > 0 ? ((totalRevenue - prevRevenue) / prevRevenue) * 100 : 0;

        long prevOrders = orderRepository.countOrdersInPeriod(companyId, cafeId, prevStartInstant);
        double orderGrowth = prevOrders > 0 ? ((double)(totalOrders - prevOrders) / prevOrders) * 100 : 0;

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
            // Monthly data for year
            for (int i = 11; i >= 0; i--) {
                LocalDate month = now.minusMonths(i);
                Instant start = month.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                Instant end = month.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

                double revenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, start);
                long orders = orderRepository.countOrdersInPeriod(companyId, cafeId, start);

                data.add(RevenueChartData.builder()
                        .date(month.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
                        .revenue(revenue)
                        .orders(orders)
                        .build());
            }
        } else {
            // Daily data
            for (int i = days - 1; i >= 0; i--) {
                LocalDate day = now.minusDays(i);
                Instant start = day.atStartOfDay(ZoneId.systemDefault()).toInstant();
                Instant end = day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

                double revenue = orderRepository.sumRevenueInPeriod(companyId, cafeId, start);
                long orders = orderRepository.countOrdersInPeriod(companyId, cafeId, start);

                data.add(RevenueChartData.builder()
                        .date(day.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
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

        stats.put("pendingOrders", orderRepository.countByStatus(companyId, cafeId, "pending"));
        stats.put("preparingOrders", orderRepository.countByStatus(companyId, cafeId, "preparing"));
        stats.put("readyOrders", orderRepository.countByStatus(companyId, cafeId, "ready"));
        stats.put("todayOrders", orderRepository.countOrdersInPeriod(companyId, cafeId,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        stats.put("todayRevenue", orderRepository.sumRevenueInPeriod(companyId, cafeId,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return stats;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class DashboardOverview {
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

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class RevenueChartData {
        private String date;
        private double revenue;
        private long orders;
    }

    @lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
    public static class TopProductData {
        private UUID productId;
        private String productName;
        private long quantitySold;
        private double revenue;
    }
}
