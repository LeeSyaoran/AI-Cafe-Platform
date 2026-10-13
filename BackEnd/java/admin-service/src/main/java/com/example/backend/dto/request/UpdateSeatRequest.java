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
public class UpdateSeatRequest {
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
