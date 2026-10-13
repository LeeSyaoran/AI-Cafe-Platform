package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "cafes", indexes = {
    @Index(name = "idx_cafes_company_id", columnList = "company_id"),
    @Index(name = "idx_cafes_slug", columnList = "slug"),
    @Index(name = "idx_cafes_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cafe extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "opening_hours")
    private String openingHours; // JSON: {"mon": "07:00-22:00", ...}

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "images_json", columnDefinition = "TEXT")
    private String imagesJson;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column
    @Builder.Default
    private Boolean deliveryEnabled = false;

    @Column
    @Builder.Default
    private Boolean pickupEnabled = true;

    @Column
    @Builder.Default
    private String status = "open"; // open, busy, closed, temporarily_closed
}
