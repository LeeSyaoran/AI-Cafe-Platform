package com.example.backend.controller;

import com.example.backend.dto.response.CafeBranchResponse;
import com.example.backend.entity.Cafe;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CafeBranchResponse>>> getCafes(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(cafeService.searchCafes(companyId, province, search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cafe>> getCafe(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(cafeService.getCafeById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Cafe>> getCafeBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(cafeService.getCafeBySlug(slug)));
    }
}