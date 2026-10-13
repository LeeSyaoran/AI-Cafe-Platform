package com.example.backend.service.impl;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.request.UpdateReservationStatusRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.entity.Cafe;
import com.example.backend.entity.Seat;
import com.example.backend.entity.SeatReservation;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.ReservationMapper;
import com.example.backend.repository.CafeRepository;
import com.example.backend.repository.SeatRepository;
import com.example.backend.repository.SeatReservationRepository;
import com.example.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final SeatReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final CafeRepository cafeRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponse createReservation(CreateReservationRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }
        if (request.getStartTime().isBefore(Instant.now())) {
            throw new BadRequestException("Start time cannot be in the past");
        }

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + request.getSeatId()));

        if (!Boolean.TRUE.equals(seat.getIsAvailable()) || "maintenance".equalsIgnoreCase(seat.getStatus())) {
            throw new ConflictException("Seat " + seat.getSeatNumber() + " is currently not available for booking");
        }

        List<SeatReservation> conflicts = reservationRepository.findConflictingReservations(
                seat.getId(), request.getStartTime(), request.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new ConflictException("Seat " + seat.getSeatNumber() + " is already booked for this time range");
        }

        SeatReservation reservation = reservationMapper.toEntity(request);
        SeatReservation saved = reservationRepository.save(reservation);

        Cafe cafe = cafeRepository.findById(seat.getCafeId()).orElse(null);
        return reservationMapper.toResponse(saved, seat, cafe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getUserReservations(UUID userId) {
        return reservationRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(res -> {
                    Seat seat = seatRepository.findById(res.getSeatId()).orElse(null);
                    Cafe cafe = seat != null ? cafeRepository.findById(seat.getCafeId()).orElse(null) : null;
                    return reservationMapper.toResponse(res, seat, cafe);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(UUID id) {
        SeatReservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
        Seat seat = seatRepository.findById(res.getSeatId()).orElse(null);
        Cafe cafe = seat != null ? cafeRepository.findById(seat.getCafeId()).orElse(null) : null;
        return reservationMapper.toResponse(res, seat, cafe);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationByCode(String code) {
        SeatReservation res = reservationRepository.findByReservationCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with code: " + code));
        Seat seat = seatRepository.findById(res.getSeatId()).orElse(null);
        Cafe cafe = seat != null ? cafeRepository.findById(seat.getCafeId()).orElse(null) : null;
        return reservationMapper.toResponse(res, seat, cafe);
    }

    @Override
    public ReservationResponse updateStatus(UUID id, UpdateReservationStatusRequest request) {
        SeatReservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        res.setStatus(request.getStatus());
        if ("checked_in".equalsIgnoreCase(request.getStatus())) {
            res.setCheckedInAt(Instant.now());
        } else if ("completed".equalsIgnoreCase(request.getStatus())) {
            res.setCheckedOutAt(Instant.now());
        }
        if (request.getNotes() != null) {
            res.setNotes(request.getNotes());
        }

        SeatReservation updated = reservationRepository.save(res);
        Seat seat = seatRepository.findById(res.getSeatId()).orElse(null);
        Cafe cafe = seat != null ? cafeRepository.findById(seat.getCafeId()).orElse(null) : null;
        return reservationMapper.toResponse(updated, seat, cafe);
    }

    @Override
    public void cancelReservation(UUID id, String reason) {
        SeatReservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        res.setStatus("cancelled");
        if (reason != null && !reason.isBlank()) {
            res.setNotes((res.getNotes() != null ? res.getNotes() + " | Cancel reason: " : "Cancel reason: ") + reason);
        }
        reservationRepository.save(res);
    }
}
