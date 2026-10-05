package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_phone", columnList = "phone"),
    @Index(name = "idx_users_company", columnList = "company_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ADMIN, MANAGER, STAFF, CUSTOMER

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column
    private String phone;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    @Column
    private String gender;

    @Column
    @Builder.Default
    private String status = "active"; // active, inactive, banned

    @Column
    private String provider; // local, google, facebook

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @Column
    @Builder.Default
    private String language = "vi";

    @Column
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "last_login")
    private Instant lastLogin;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}

@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_companies_slug", columnList = "slug", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @Column
    @Builder.Default
    private String status = "active";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}

@Entity
@Table(name = "cafes", indexes = {
    @Index(name = "idx_cafes_company", columnList = "company_id"),
    @Index(name = "idx_cafes_slug", columnList = "slug", unique = true),
    @Index(name = "idx_cafes_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column
    private String phone;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "opening_time")
    private String openingTime;

    @Column(name = "closing_time")
    private String closingTime;

    @Column
    @Builder.Default
    private String status = "active";

    @Column(name = "is_pickup_available")
    @Builder.Default
    private Boolean isPickupAvailable = true;

    @Column(name = "is_delivery_available")
    @Builder.Default
    private Boolean isDeliveryAvailable = true;

    @Column(name = "min_order_amount")
    @Builder.Default
    private Double minOrderAmount = 0.0;

    @Column(name = "delivery_fee")
    @Builder.Default
    private Double deliveryFee = 0.0;

    @Column(name = "estimated_delivery_time")
    @Builder.Default
    private Integer estimatedDeliveryTime = 30;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}

@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_addresses_user", columnList = "user_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fullAddress;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String phone;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column
    @Builder.Default
    private Boolean isDefault = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

@Entity
@Table(name = "seats", indexes = {
    @Index(name = "idx_seats_cafe", columnList = "cafe_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cafe_id", nullable = false)
    private UUID cafeId;

    @Column(nullable = false)
    private String name;

    @Column
    private String type; // table, booth, vip, outdoor

    @Column
    private Integer capacity;

    @Column
    @Builder.Default
    private String status = "available"; // available, occupied, reserved, maintenance

    @Column
    private String position;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}