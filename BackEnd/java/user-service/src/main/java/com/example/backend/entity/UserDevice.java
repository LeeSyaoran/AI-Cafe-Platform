package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_devices", indexes = {
    @Index(name = "idx_user_devices_user_id", columnList = "user_id"),
    @Index(name = "idx_user_devices_token", columnList = "fcm_token")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDevice extends BaseCreatedEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_type")
    @Builder.Default
    private String deviceType = "android";

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column
    @Builder.Default
    private Boolean active = true;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;
}
