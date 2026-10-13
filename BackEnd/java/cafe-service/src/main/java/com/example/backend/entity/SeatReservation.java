package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seat_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeatReservation extends BaseEntity {

    @Column(name = "reservation_code", length = 20, unique = true)
    private String reservationCode;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "guest_count")
    @Builder.Default
    private Integer guestCount = 1;

    @Column(length = 20)
    @Builder.Default
    private String status = "pending"; // pending, confirmed, checked_in, completed, cancelled, no_show

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "checked_in_at")
    private Instant checkedInAt;

    @Column(name = "checked_out_at")
    private Instant checkedOutAt;
}
