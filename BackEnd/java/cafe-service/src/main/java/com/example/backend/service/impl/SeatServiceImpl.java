package com.example.backend.service.impl;

import com.example.backend.dto.request.CheckAvailabilityRequest;
import com.example.backend.dto.response.SeatAvailabilityResponse;
import com.example.backend.entity.Seat;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.SeatMapper;
import com.example.backend.repository.SeatRepository;
import com.example.backend.repository.SeatReservationRepository;
import com.example.backend.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatReservationRepository reservationRepository;
    private final SeatMapper seatMapper;

    @Override
    public List<Seat> getSeatsByCafe(UUID cafeId, String zone, String status) {
        if (zone != null && !zone.isBlank()) {
            return seatRepository.findByCafeIdAndZone(cafeId, zone);
        }
        if (status != null && !status.isBlank()) {
            return seatRepository.findByCafeIdAndStatus(cafeId, status);
        }
        return seatRepository.findByCafeId(cafeId);
    }

    @Override
    public Seat getSeatById(UUID seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));
    }

    @Override
    public List<SeatAvailabilityResponse> checkAvailability(CheckAvailabilityRequest request) {
        List<Seat> seats;
        if (request.getGuests() != null && request.getGuests() > 0) {
            seats = seatRepository.findByCafeIdAndCapacityGreaterThanEqual(request.getCafeId(), request.getGuests());
        } else {
            seats = seatRepository.findByCafeId(request.getCafeId());
        }

        if (request.getZone() != null && !request.getZone().isBlank()) {
            seats = seats.stream().filter(s -> request.getZone().equalsIgnoreCase(s.getZone())).collect(Collectors.toList());
        }

        return seats.stream().map(seat -> {
            boolean hasConflict = !reservationRepository.findConflictingReservations(
                    seat.getId(), request.getStartTime(), request.getEndTime()).isEmpty();
            return seatMapper.toAvailabilityResponse(seat, !hasConflict);
        }).collect(Collectors.toList());
    }
}
