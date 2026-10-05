package com.example.backend.controller;

import com.example.backend.response.ApiResponse;
import com.example.backend.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final PushNotificationService pushService;

    // POST /v1/notifications/devices - Register device token
    @PostMapping("/devices")
    public ResponseEntity<ApiResponse<String>> registerDevice(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody RegisterDeviceRequest request) {

        pushService.registerDeviceToken(userId.toString(), request.getToken(), request.getPlatform());
        return ResponseEntity.ok(ApiResponse.ok("Device registered"));
    }

    // DELETE /v1/notifications/devices/:token - Remove device token
    @DeleteMapping("/devices/{token}")
    public ResponseEntity<ApiResponse<String>> removeDevice(
            @RequestHeader("X-User-ID") UUID userId,
            @PathVariable String token) {

        pushService.removeDeviceToken(userId.toString(), token);
        return ResponseEntity.ok(ApiResponse.ok("Device removed"));
    }

    // POST /v1/notifications/test - Test notification (dev only)
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> testNotification(
            @RequestHeader("X-User-ID") UUID userId) {

        pushService.sendPushNotification(
                userId.toString(),
                "Test Notification",
                "This is a test notification from AI Café",
                null
        );
        return ResponseEntity.ok(ApiResponse.ok("Test notification sent"));
    }

    @lombok.Data
    public static class RegisterDeviceRequest {
        private String token;
        private String platform; // ios, android, web
    }
}
