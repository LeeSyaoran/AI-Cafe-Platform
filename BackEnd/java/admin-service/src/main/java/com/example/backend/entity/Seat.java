package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Seat extends BaseEntity {

    @Column(name = "cafe_id", nullable = false)
    private UUID cafeId;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    @Column(length = 100)
    @Builder.Default
    private String zone = "Standard";

    @Column(name = "seat_type", length = 20)
    @Builder.Default
    private String seatType = "table"; // table, booth, vip, outdoor, private, bar

    @Builder.Default
    private Integer capacity = 4;

    @Column(name = "min_capacity")
    @Builder.Default
    private Integer minCapacity = 1;

    @Column(name = "max_capacity")
    @Builder.Default
    private Integer maxCapacity = 8;

    @Column(name = "pos_x")
    private Integer posX;

    @Column(name = "pos_y")
    private Integer posY;

    @Column(name = "pos_width")
    private Integer posWidth;

    @Column(name = "pos_height")
    private Integer posHeight;

    @Column(name = "hourly_rate", precision = 10, scale = 0)
    private BigDecimal hourlyRate;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(length = 20)
    @Builder.Default
    private String status = "available"; // available, reserved, occupied, maintenance
}
