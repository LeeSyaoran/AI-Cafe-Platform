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

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoyaltyTierRequest {
    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotBlank(message = "Tier name is required")
    private String name;

    private String nameVi;

    @NotNull(message = "Min points is required")
    private Integer minPoints;

    private Integer maxPoints;
    private BigDecimal discountPercent;
    private BigDecimal pointsMultiplier;
    private String color;
    private String icon;
    private Integer sortOrder;
}
