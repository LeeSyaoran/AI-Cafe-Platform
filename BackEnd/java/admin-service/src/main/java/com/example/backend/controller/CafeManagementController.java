package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import com.example.backend.entity.Cafe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class CafeManagementController {

    private final CafeManagementService cafeManagementService;

    // --- CAFE APIS ---

    @GetMapping("/cafes")
    public ResponseEntity<List<Cafe>> listCafes(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(cafeManagementService.getAllCafes(companyId, status, search));
    }

    @GetMapping("/cafes/{id}")
    public ResponseEntity<Cafe> getCafe(@PathVariable UUID id) {
        return ResponseEntity.ok(cafeManagementService.getCafeById(id));
    }

    @PostMapping("/cafes")
    public ResponseEntity<Cafe> createCafe(@Valid @RequestBody CreateCafeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeManagementService.createCafe(request));
    }

    @PutMapping("/cafes/{id}")
    public ResponseEntity<Cafe> updateCafe(@PathVariable UUID id, @RequestBody UpdateCafeRequest request) {
        return ResponseEntity.ok(cafeManagementService.updateCafe(id, request));
    }

    @DeleteMapping("/cafes/{id}")
    public ResponseEntity<Map<String, String>> deleteCafe(@PathVariable UUID id) {
        cafeManagementService.deleteCafe(id);
        return ResponseEntity.ok(Map.of("message", "Cafe deleted successfully", "id", id.toString()));
    }

    // --- SEATS APIS ---

    @GetMapping("/cafes/{cafeId}/seats")
    public ResponseEntity<List<Seat>> listSeats(
            @PathVariable UUID cafeId,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(cafeManagementService.getSeatsByCafe(cafeId, zone, status));
    }

    @GetMapping("/seats/{seatId}")
    public ResponseEntity<Seat> getSeat(@PathVariable UUID seatId) {
        return ResponseEntity.ok(cafeManagementService.getSeatById(seatId));
    }

    @PostMapping("/seats")
    public ResponseEntity<Seat> createSeat(@Valid @RequestBody CreateSeatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeManagementService.createSeat(request));
    }

    @PutMapping("/seats/{seatId}")
    public ResponseEntity<Seat> updateSeat(@PathVariable UUID seatId, @RequestBody UpdateSeatRequest request) {
        return ResponseEntity.ok(cafeManagementService.updateSeat(seatId, request));
    }

    @PatchMapping("/seats/{seatId}/status")
    public ResponseEntity<Seat> updateSeatStatus(@PathVariable UUID seatId, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "available");
        return ResponseEntity.ok(cafeManagementService.updateSeatStatus(seatId, status));
    }

    @DeleteMapping("/seats/{seatId}")
    public ResponseEntity<Map<String, String>> deleteSeat(@PathVariable UUID seatId) {
        cafeManagementService.deleteSeat(seatId);
        return ResponseEntity.ok(Map.of("message", "Seat deleted successfully", "id", seatId.toString()));
    }
}
