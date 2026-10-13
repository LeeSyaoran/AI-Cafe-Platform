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
public class CreateSeatRequest {
    @NotNull(message = "Cafe ID is required")
    private UUID cafeId;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    private String zone;
    private String seatType;
    private Integer capacity;
    private Integer minCapacity;
    private Integer maxCapacity;
    private Integer posX;
    private Integer posY;
    private Integer posWidth;
    private Integer posHeight;
    private BigDecimal hourlyRate;
    private Boolean isAvailable;
    private String status;
}
