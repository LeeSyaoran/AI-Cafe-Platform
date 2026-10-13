package com.example.backend.repository;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import com.example.backend.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, UUID> {
    List<Cafe> findByCompanyId(UUID companyId);
    List<Cafe> findByStatus(String status);
    List<Cafe> findByNameContainingIgnoreCase(String name);
    Optional<Cafe> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
