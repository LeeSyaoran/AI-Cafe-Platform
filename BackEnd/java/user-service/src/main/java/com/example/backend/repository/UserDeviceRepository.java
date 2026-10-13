package com.example.backend.repository;

import com.example.backend.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    List<UserDevice> findByUserIdAndActiveTrue(UUID userId);

    List<UserDevice> findByFcmToken(String fcmToken);
}
