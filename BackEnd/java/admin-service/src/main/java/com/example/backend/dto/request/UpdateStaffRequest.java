package com.example.backend.dto.request;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateStaffRequest {

    private UUID cafeId;
    private String role;
    private String department;
    private LocalDate terminationDate;
    private String salaryType;
    private BigDecimal salaryAmount;
    private String status;
    private Boolean isActive;
}
