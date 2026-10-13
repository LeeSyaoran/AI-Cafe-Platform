package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_addresses_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Address extends BaseCreatedEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String label; // Home, Work, Other

    @Column(nullable = false)
    private String address;

    @Column
    private String province;

    @Column
    private String district;

    @Column
    private String ward;

    @Column
    private String phone;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
}
