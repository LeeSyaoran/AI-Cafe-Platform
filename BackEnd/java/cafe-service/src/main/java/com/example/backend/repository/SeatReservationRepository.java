package com.example.backend.repository;

import com.example.backend.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, UUID> {
    List<SeatReservation> findByUserIdOrderByStartTimeDesc(UUID userId);
    List<SeatReservation> findBySeatIdOrderByStartTimeDesc(UUID seatId);
    Optional<SeatReservation> findByReservationCode(String reservationCode);

    @Query("SELECT r FROM SeatReservation r WHERE r.seatId = :seatId AND r.status NOT IN ('cancelled', 'no_show') " +
           "AND r.startTime < :endTime AND r.endTime > :startTime")
    List<SeatReservation> findConflictingReservations(
            @Param("seatId") UUID seatId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);
}
