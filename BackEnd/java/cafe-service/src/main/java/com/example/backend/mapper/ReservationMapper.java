package com.example.backend.mapper;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.entity.Cafe;
import com.example.backend.entity.Seat;
import com.example.backend.entity.SeatReservation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReservationMapper {

    public SeatReservation toEntity(CreateReservationRequest request) {
        if (request == null) return null;

        String code = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return SeatReservation.builder()
                .reservationCode(code)
                .seatId(request.getSeatId())
                .userId(request.getUserId())
                .orderId(request.getOrderId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .guestCount(request.getGuestCount() != null ? request.getGuestCount() : 1)
                .status("confirmed")
                .notes(request.getNotes())
                .build();
    }

    public ReservationResponse toResponse(SeatReservation reservation, Seat seat, Cafe cafe) {
        if (reservation == null) return null;

        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationCode(reservation.getReservationCode())
                .seatId(reservation.getSeatId())
                .seatNumber(seat != null ? seat.getSeatNumber() : null)
                .zone(seat != null ? seat.getZone() : null)
                .cafeId(cafe != null ? cafe.getId() : (seat != null ? seat.getCafeId() : null))
                .cafeName(cafe != null ? cafe.getName() : null)
                .userId(reservation.getUserId())
                .orderId(reservation.getOrderId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .guestCount(reservation.getGuestCount())
                .status(reservation.getStatus())
                .notes(reservation.getNotes())
                .checkedInAt(reservation.getCheckedInAt())
                .checkedOutAt(reservation.getCheckedOutAt())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
