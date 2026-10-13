package com.example.backend.controller;

import com.example.backend.dto.request.CreateReservationRequest;
import com.example.backend.dto.request.UpdateReservationStatusRequest;
import com.example.backend.dto.response.ReservationResponse;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getReservationById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getReservationByCode(code)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getUserReservations(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getUserReservations(userId)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReservationStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.updateStatus(id, request)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        reservationService.cancelReservation(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
