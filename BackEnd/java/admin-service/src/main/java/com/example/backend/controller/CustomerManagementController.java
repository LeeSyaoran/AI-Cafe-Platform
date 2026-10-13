package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

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
public class CustomerManagementController {

    private final CustomerManagementService customerManagementService;

    // --- CUSTOMER APIS ---

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> listCustomers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID loyaltyTierId,
            @RequestParam(required = false) UUID cafeId) {
        return ResponseEntity.ok(customerManagementService.getAllCustomers(status, loyaltyTierId, cafeId));
    }

    @GetMapping("/customers/{userId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable UUID userId) {
        return ResponseEntity.ok(customerManagementService.getCustomerByUserId(userId));
    }

    @PutMapping("/customers/{userId}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable UUID userId,
            @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerManagementService.updateCustomer(userId, request));
    }

    @PostMapping("/customers/{userId}/adjust-points")
    public ResponseEntity<Customer> adjustPoints(
            @PathVariable UUID userId,
            @Valid @RequestBody AdjustPointsRequest request) {
        return ResponseEntity.ok(customerManagementService.adjustPoints(userId, request));
    }

    @PatchMapping("/customers/{userId}/status")
    public ResponseEntity<Customer> updateCustomerStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "active");
        return ResponseEntity.ok(customerManagementService.updateCustomerStatus(userId, status));
    }

    // --- LOYALTY TIER APIS ---

    @GetMapping("/loyalty-tiers")
    public ResponseEntity<List<LoyaltyTier>> listLoyaltyTiers(@RequestParam(required = false) UUID companyId) {
        return ResponseEntity.ok(customerManagementService.getAllTiers(companyId));
    }

    @GetMapping("/loyalty-tiers/{id}")
    public ResponseEntity<LoyaltyTier> getLoyaltyTier(@PathVariable UUID id) {
        return ResponseEntity.ok(customerManagementService.getTierById(id));
    }

    @PostMapping("/loyalty-tiers")
    public ResponseEntity<LoyaltyTier> createLoyaltyTier(@Valid @RequestBody CreateLoyaltyTierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerManagementService.createTier(request));
    }

    @PutMapping("/loyalty-tiers/{id}")
    public ResponseEntity<LoyaltyTier> updateLoyaltyTier(
            @PathVariable UUID id,
            @RequestBody UpdateLoyaltyTierRequest request) {
        return ResponseEntity.ok(customerManagementService.updateTier(id, request));
    }

    @DeleteMapping("/loyalty-tiers/{id}")
    public ResponseEntity<Map<String, String>> deleteLoyaltyTier(@PathVariable UUID id) {
        customerManagementService.deleteTier(id);
        return ResponseEntity.ok(Map.of("message", "Loyalty tier deleted successfully", "id", id.toString()));
    }
}
