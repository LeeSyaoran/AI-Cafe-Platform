package com.example.backend.service;

import com.example.backend.dto.request.RegisterDeviceRequest;
import com.example.backend.entity.UserDevice;

import java.util.List;
import java.util.UUID;

public interface DeviceService {

    UserDevice registerDevice(UUID userId, RegisterDeviceRequest request);

    List<UserDevice> getUserDevices(UUID userId);

    void deactivateDevice(UUID deviceId);
}
