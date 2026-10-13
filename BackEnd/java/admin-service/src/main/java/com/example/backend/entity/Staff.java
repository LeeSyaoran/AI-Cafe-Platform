package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "staffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Staff extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "cafe_id")
    private UUID cafeId;

    @Column(name = "staff_code", unique = true, length = 50)
    private String staffCode;

    @Column(nullable = false, length = 50)
    private String role; // owner, manager, supervisor, cashier, barista, server, delivery

    @Column(length = 100)
    private String department;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "salary_type", length = 50)
    private String salaryType;

    @Column(name = "salary_amount", precision = 12, scale = 2)
    private BigDecimal salaryAmount;

    @Column(length = 50)
    @Builder.Default
    private String status = "active";

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
