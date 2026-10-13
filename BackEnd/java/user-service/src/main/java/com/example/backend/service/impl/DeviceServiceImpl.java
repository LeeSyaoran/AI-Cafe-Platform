package com.example.backend.service.impl;

import com.example.backend.dto.request.RegisterDeviceRequest;
import com.example.backend.entity.UserDevice;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.UserDeviceRepository;
import com.example.backend.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final UserDeviceRepository userDeviceRepository;

    @Override
    @Transactional
    public UserDevice registerDevice(UUID userId, RegisterDeviceRequest request) {
        List<UserDevice> existing = userDeviceRepository.findByFcmToken(request.getFcmToken());
        UserDevice device;

        if (!existing.isEmpty()) {
            device = existing.get(0);
            device.setUserId(userId);
            device.setDeviceType(request.getDeviceType());
            device.setDeviceId(request.getDeviceId());
            device.setActive(true);
            device.setLastActiveAt(Instant.now());
        } else {
            device = UserDevice.builder()
                    .userId(userId)
                    .deviceType(request.getDeviceType())
                    .deviceId(request.getDeviceId())
                    .fcmToken(request.getFcmToken())
                    .active(true)
                    .lastActiveAt(Instant.now())
                    .build();
        }

        return userDeviceRepository.save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDevice> getUserDevices(UUID userId) {
        return userDeviceRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    @Transactional
    public void deactivateDevice(UUID deviceId) {
        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("UserDevice", "id", deviceId));
        device.setActive(false);
        userDeviceRepository.save(device);
    }
}
