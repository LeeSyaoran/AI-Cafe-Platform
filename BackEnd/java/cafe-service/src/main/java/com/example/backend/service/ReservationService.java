package com.example.backend.service;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.request.UpdateReservationStatusRequest;
import com.example.backend.dto.response.ReservationResponse;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
    ReservationResponse createReservation(CreateReservationRequest request);
    List<ReservationResponse> getUserReservations(UUID userId);
    ReservationResponse getReservationById(UUID id);
    ReservationResponse getReservationByCode(String code);
    ReservationResponse updateStatus(UUID id, UpdateReservationStatusRequest request);
    void cancelReservation(UUID id, String reason);
}
