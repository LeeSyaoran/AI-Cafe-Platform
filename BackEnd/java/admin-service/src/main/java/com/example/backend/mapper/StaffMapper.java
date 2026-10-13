package com.example.backend.mapper;

import com.example.backend.dto.request.CreateStaffRequest;
import com.example.backend.dto.request.UpdateStaffRequest;
import com.example.backend.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StaffMapper {

    public Staff toEntity(UUID companyId, CreateStaffRequest request) {
        if (request == null) return null;

        String staffCode = request.getStaffCode();
        if (staffCode == null || staffCode.isBlank()) {
            staffCode = "STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        return Staff.builder()
                .userId(request.getUserId())
                .companyId(companyId)
                .cafeId(request.getCafeId())
                .staffCode(staffCode)
                .role(request.getRole() != null ? request.getRole() : "cashier")
                .department(request.getDepartment())
                .hireDate(request.getHireDate())
                .salaryType(request.getSalaryType())
                .salaryAmount(request.getSalaryAmount())
                .status("active")
                .isActive(true)
                .build();
    }

    public void updateEntity(Staff staff, UpdateStaffRequest request) {
        if (staff == null || request == null) return;

        if (request.getCafeId() != null) staff.setCafeId(request.getCafeId());
        if (request.getRole() != null) staff.setRole(request.getRole());
        if (request.getDepartment() != null) staff.setDepartment(request.getDepartment());
        if (request.getTerminationDate() != null) staff.setTerminationDate(request.getTerminationDate());
        if (request.getSalaryType() != null) staff.setSalaryType(request.getSalaryType());
        if (request.getSalaryAmount() != null) staff.setSalaryAmount(request.getSalaryAmount());
        if (request.getStatus() != null) staff.setStatus(request.getStatus());
        if (request.getIsActive() != null) staff.setIsActive(request.getIsActive());
    }
}
