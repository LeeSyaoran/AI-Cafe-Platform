package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRewardRequest {

    private java.util.UUID companyId;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Reward type is required")
    private String rewardType;

    private String discountType;
    private Double discountValue;

    @NotNull(message = "Points cost is required")
    private Integer pointsCost;

    private Integer validityDays;
    private String applicableTiers;
}
