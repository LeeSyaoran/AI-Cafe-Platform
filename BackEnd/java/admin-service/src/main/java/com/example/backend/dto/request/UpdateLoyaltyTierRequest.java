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

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoyaltyTierRequest {
    private String name;
    private String nameVi;
    private Integer minPoints;
    private Integer maxPoints;
    private BigDecimal discountPercent;
    private BigDecimal pointsMultiplier;
    private String color;
    private String icon;
    private Integer sortOrder;
}
