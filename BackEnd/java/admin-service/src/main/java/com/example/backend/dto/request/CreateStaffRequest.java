package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateStaffRequest {

    @NotNull(message = "userId is required")
    private UUID userId;

    private UUID cafeId;

    private String staffCode;

    @NotBlank(message = "role is required")
    private String role; // manager, supervisor, cashier, barista, server, delivery

    private String department;

    private LocalDate hireDate;

    private String salaryType;

    private BigDecimal salaryAmount;
}
