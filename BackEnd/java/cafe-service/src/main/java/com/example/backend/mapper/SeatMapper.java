package com.example.backend.mapper;

import com.example.backend.dto.response.SeatAvailabilityResponse;
import com.example.backend.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatAvailabilityResponse toAvailabilityResponse(Seat seat, boolean isTimeSlotAvailable) {
        if (seat == null) return null;

        return SeatAvailabilityResponse.builder()
                .seatId(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .zone(seat.getZone())
                .seatType(seat.getSeatType())
                .capacity(seat.getCapacity())
                .hourlyRate(seat.getHourlyRate())
                .isAvailable(isTimeSlotAvailable && Boolean.TRUE.equals(seat.getIsAvailable()) && "available".equalsIgnoreCase(seat.getStatus()))
                .posX(seat.getPosX())
                .posY(seat.getPosY())
                .build();
    }
}
