package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface StaffService {

    Page<Staff> getStaffs(UUID companyId, UUID cafeId, Pageable pageable);

    List<Staff> getStaffsByCompany(UUID companyId);

    Staff getStaffById(UUID id);

    Staff createStaff(UUID companyId, CreateStaffRequest request);

    Staff updateStaff(UUID id, UpdateStaffRequest request);

    void deactivateStaff(UUID id);
}
