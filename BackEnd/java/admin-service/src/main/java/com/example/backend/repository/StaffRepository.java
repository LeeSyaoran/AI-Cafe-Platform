package com.example.backend.repository;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findByCompanyId(UUID companyId);

    List<Staff> findByCompanyIdAndCafeId(UUID companyId, UUID cafeId);

    Page<Staff> findByCompanyId(UUID companyId, Pageable pageable);

    Page<Staff> findByCompanyIdAndCafeId(UUID companyId, UUID cafeId, Pageable pageable);

    Optional<Staff> findByStaffCode(String staffCode);

    Optional<Staff> findByUserId(UUID userId);
}
