package com.example.backend.controller;

import com.example.backend.dto.request.CheckAvailabilityRequest;
import com.example.backend.dto.response.SeatAvailabilityResponse;
import com.example.backend.entity.Seat;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/cafes/{cafeId}/seats")
    public ResponseEntity<ApiResponse<List<Seat>>> getSeatsByCafe(
            @PathVariable UUID cafeId,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(seatService.getSeatsByCafe(cafeId, zone, status)));
    }

    @GetMapping("/seats/{seatId}")
    public ResponseEntity<ApiResponse<Seat>> getSeatById(@PathVariable UUID seatId) {
        return ResponseEntity.ok(ApiResponse.ok(seatService.getSeatById(seatId)));
    }

    @PostMapping("/seats/availability")
    public ResponseEntity<ApiResponse<List<SeatAvailabilityResponse>>> checkAvailability(
            @Valid @RequestBody CheckAvailabilityRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(seatService.checkAvailability(request)));
    }
}
