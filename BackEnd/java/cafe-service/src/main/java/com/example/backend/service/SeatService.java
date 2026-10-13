package com.example.backend.service;

import com.example.backend.dto.request.CheckAvailabilityRequest;
import com.example.backend.dto.response.SeatAvailabilityResponse;
import com.example.backend.entity.Seat;

import java.util.List;
import java.util.UUID;

public interface SeatService {
    List<Seat> getSeatsByCafe(UUID cafeId, String zone, String status);
    Seat getSeatById(UUID seatId);
    List<SeatAvailabilityResponse> checkAvailability(CheckAvailabilityRequest request);
}
