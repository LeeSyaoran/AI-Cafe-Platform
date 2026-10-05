package com.example.backend.repository;

import com.example.backend.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, UUID> {

    Optional<Cafe> findBySlug(String slug);

    List<Cafe> findByCompanyIdAndIsActiveTrue(UUID companyId);

    List<Cafe> findByStatus(String status);
}

@Repository
interface CompanyRepository extends JpaRepository<com.example.backend.entity.Company, java.util.UUID> {
    Optional<com.example.backend.entity.Company> findBySlug(String slug);
}

@Repository
interface AddressRepository extends JpaRepository<com.example.backend.entity.Address, java.util.UUID> {
    List<com.example.backend.entity.Address> findByUserId(java.util.UUID userId);
}

@Repository
interface SeatRepository extends JpaRepository<com.example.backend.entity.Seat, java.util.UUID> {
    List<com.example.backend.entity.Seat> findByCafeId(java.util.UUID cafeId);
}