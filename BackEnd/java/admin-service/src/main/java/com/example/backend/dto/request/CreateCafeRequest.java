package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCafeRequest {
    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotBlank(message = "Name is required")
    private String name;

    private String slug;
    private String description;
    private String address;
    private String province;
    private String district;
    private String ward;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private String openingHours;
    private String imageUrl;
    private String imagesJson;
    private Boolean deliveryEnabled;
    private Boolean pickupEnabled;
    private String status;
}
