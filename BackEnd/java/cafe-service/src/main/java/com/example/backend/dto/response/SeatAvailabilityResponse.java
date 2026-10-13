package com.example.backend.dto.response;

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
public class SeatAvailabilityResponse {
    private UUID seatId;
    private String seatNumber;
    private String zone;
    private String seatType;
    private Integer capacity;
    private BigDecimal hourlyRate;
    private Boolean isAvailable;
    private Integer posX;
    private Integer posY;
}
