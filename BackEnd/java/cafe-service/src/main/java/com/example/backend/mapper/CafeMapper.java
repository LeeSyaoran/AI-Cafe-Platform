package com.example.backend.mapper;

import com.example.backend.dto.response.CafeBranchResponse;
import com.example.backend.entity.Cafe;
import org.springframework.stereotype.Component;

@Component
public class CafeMapper {

    public CafeBranchResponse toBranchResponse(Cafe cafe, long totalSeats, long availableSeats) {
        if (cafe == null) return null;

        return CafeBranchResponse.builder()
                .id(cafe.getId())
                .name(cafe.getName())
                .slug(cafe.getSlug())
                .address(cafe.getAddress())
                .province(cafe.getProvince())
                .district(cafe.getDistrict())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .phone(cafe.getPhone())
                .openingHours(cafe.getOpeningHours())
                .imageUrl(cafe.getImageUrl())
                .status(cafe.getStatus())
                .totalSeats(totalSeats)
                .availableSeats(availableSeats)
                .build();
    }
}
