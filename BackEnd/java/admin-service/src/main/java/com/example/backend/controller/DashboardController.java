package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverview>> getOverview(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "month") String period) {

        DashboardOverview overview = dashboardService.getOverview(companyId, cafeId, period);
        return ResponseEntity.ok(ApiResponse.ok(overview));
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<ApiResponse<List<RevenueChartData>>> getRevenueChart(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "month") String period) {

        List<RevenueChartData> chart = dashboardService.getRevenueChart(companyId, cafeId, period);
        return ResponseEntity.ok(ApiResponse.ok(chart));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<TopProductData>>> getTopProducts(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "10") int limit) {

        List<TopProductData> products = dashboardService.getTopProducts(companyId, cafeId, limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/quick-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuickStats(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId) {

        Map<String, Object> stats = dashboardService.getQuickStats(companyId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
