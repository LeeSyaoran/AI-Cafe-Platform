package com.example.backend.service.impl;

import com.example.backend.dto.request.CreateStaffRequest;
import com.example.backend.dto.request.UpdateStaffRequest;
import com.example.backend.entity.Staff;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.StaffMapper;
import com.example.backend.repository.StaffRepository;
import com.example.backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Staff> getStaffs(UUID companyId, UUID cafeId, Pageable pageable) {
        if (cafeId != null) {
            return staffRepository.findByCompanyIdAndCafeId(companyId, cafeId, pageable);
        }
        return staffRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffsByCompany(UUID companyId) {
        return staffRepository.findByCompanyId(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getStaffById(UUID id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
    }

    @Override
    @Transactional
    public Staff createStaff(UUID companyId, CreateStaffRequest request) {
        if (request.getStaffCode() != null && staffRepository.findByStaffCode(request.getStaffCode()).isPresent()) {
            throw new BadRequestException("Staff code already exists: " + request.getStaffCode());
        }

        Staff staff = staffMapper.toEntity(companyId, request);
        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public Staff updateStaff(UUID id, UpdateStaffRequest request) {
        Staff staff = getStaffById(id);
        staffMapper.updateEntity(staff, request);
        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public void deactivateStaff(UUID id) {
        Staff staff = getStaffById(id);
        staff.setStatus("inactive");
        staff.setIsActive(false);
        staffRepository.save(staff);
        log.info("Deactivated staff {}", id);
    }
}
