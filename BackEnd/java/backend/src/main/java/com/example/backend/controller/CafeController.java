package com.example.backend.controller;

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
    public ResponseEntity<ApiResponse<List<Cafe>>> getCafes(@RequestParam UUID companyId) {
        List<Cafe> cafes = cafeService.getCafesByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.ok(cafes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cafe>> getCafe(@PathVariable UUID id) {
        Cafe cafe = cafeService.getCafe(id);
        return ResponseEntity.ok(ApiResponse.ok(cafe));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Cafe>> getCafeBySlug(@PathVariable String slug) {
        Cafe cafe = cafeService.getCafeBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(cafe));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<Cafe>>> getNearbyCafes(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5000") int radius) {
        List<Cafe> cafes = cafeService.getNearbyCafes(lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.ok(cafes));
    }
}