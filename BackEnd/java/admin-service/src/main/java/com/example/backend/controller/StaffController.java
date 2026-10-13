package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Staff>>> getStaffs(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Staff> staffs = staffService.getStaffs(companyId, cafeId, pageRequest);
        return ResponseEntity.ok(ApiResponse.ok(staffs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Staff>> getStaffById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getStaffById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Staff>> createStaff(
            @RequestHeader("X-Company-ID") UUID companyId,
            @Valid @RequestBody CreateStaffRequest request) {

        Staff created = staffService.createStaff(companyId, request);
        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Staff>> updateStaff(
            @PathVariable UUID id,
            @RequestBody UpdateStaffRequest request) {

        Staff updated = staffService.updateStaff(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateStaff(@PathVariable UUID id) {
        staffService.deactivateStaff(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
