package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeBranchResponse {
    private UUID id;
    private String name;
    private String slug;
    private String address;
    private String province;
    private String district;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String openingHours;
    private String imageUrl;
    private String status;
    private long totalSeats;
    private long availableSeats;
}
