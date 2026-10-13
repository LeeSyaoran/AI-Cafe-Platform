package com.example.backend.repository;

import com.example.backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByCafeId(UUID cafeId);
    List<Seat> findByCafeIdAndZone(UUID cafeId, String zone);
    List<Seat> findByCafeIdAndStatus(UUID cafeId, String status);
    List<Seat> findByCafeIdAndCapacityGreaterThanEqual(UUID cafeId, Integer capacity);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.cafeId = :cafeId AND s.isAvailable = true")
    long countAvailableSeats(@Param("cafeId") UUID cafeId);
}
