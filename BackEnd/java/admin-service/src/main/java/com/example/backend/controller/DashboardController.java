package com.example.backend.controller;

import com.example.backend.response.ApiResponse;
import com.example.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /v1/admin/dashboard/overview
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardService.DashboardOverview>> getOverview(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "month") String period) {

        DashboardService.DashboardOverview overview = dashboardService.getOverview(companyId, cafeId, period);
        return ResponseEntity.ok(ApiResponse.ok(overview));
    }

    // GET /v1/admin/dashboard/revenue-chart
    @GetMapping("/revenue-chart")
    public ResponseEntity<ApiResponse<List<DashboardService.RevenueChartData>>> getRevenueChart(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "month") String period) {

        List<DashboardService.RevenueChartData> chart = dashboardService.getRevenueChart(companyId, cafeId, period);
        return ResponseEntity.ok(ApiResponse.ok(chart));
    }

    // GET /v1/admin/dashboard/top-products
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<DashboardService.TopProductData>>> getTopProducts(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "10") int limit) {

        List<DashboardService.TopProductData> products = dashboardService.getTopProducts(companyId, cafeId, limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    // GET /v1/admin/dashboard/quick-stats
    @GetMapping("/quick-stats")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getQuickStats(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId) {

        java.util.Map<String, Object> stats = dashboardService.getQuickStats(companyId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
