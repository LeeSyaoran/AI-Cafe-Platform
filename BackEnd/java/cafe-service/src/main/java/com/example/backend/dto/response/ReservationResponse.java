package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private UUID id;
    private String reservationCode;
    private UUID seatId;
    private String seatNumber;
    private String zone;
    private UUID cafeId;
    private String cafeName;
    private UUID userId;
    private UUID orderId;
    private Instant startTime;
    private Instant endTime;
    private Integer guestCount;
    private String status;
    private String notes;
    private Instant checkedInAt;
    private Instant checkedOutAt;
    private Instant createdAt;
}
