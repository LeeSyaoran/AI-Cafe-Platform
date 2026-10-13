package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCafeRequest {
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
    private Boolean isActive;
    private Boolean deliveryEnabled;
    private Boolean pickupEnabled;
    private String status;
}
