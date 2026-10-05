package com.example.backend.service;

import com.example.backend.entity.Cafe;
import com.example.backend.repository.CafeRepository;
import com.example.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;

    @Transactional(readOnly = true)
    public List<Cafe> getCafesByCompany(UUID companyId) {
        return cafeRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Transactional(readOnly = true)
    public Cafe getCafe(UUID id) {
        return cafeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cafe not found"));
    }

    @Transactional(readOnly = true)
    public Cafe getCafeBySlug(String slug) {
        return cafeRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Cafe not found"));
    }

    @Transactional(readOnly = true)
    public List<Cafe> getNearbyCafes(Double lat, Double lng, int radiusMeters) {
        // Simple distance-based filter (in production, use PostGIS)
        List<Cafe> allCafes = cafeRepository.findByStatus("active");
        return allCafes.stream()
                .filter(c -> {
                    if (c.getLatitude() == null || c.getLongitude() == null) return false;
                    double d = haversineDistance(lat, lng, c.getLatitude(), c.getLongitude());
                    return d <= radiusMeters;
                })
                .toList();
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}