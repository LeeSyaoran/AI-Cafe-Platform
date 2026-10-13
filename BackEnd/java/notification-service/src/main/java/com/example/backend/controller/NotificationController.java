package com.example.backend.controller;

import com.example.backend.dto.request.RegisterDeviceRequest;
import com.example.backend.dto.request.SendNotificationRequest;
import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.PushService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final PushService pushService;

    @PostMapping("/devices")
    public ResponseEntity<ApiResponse<String>> registerDevice(
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody RegisterDeviceRequest request) {
        log.info("Device registered for user {}: token {}", userId, request.getToken());
        return ResponseEntity.ok(ApiResponse.ok("Device registered successfully"));
    }

    @DeleteMapping("/devices/{token}")
    public ResponseEntity<ApiResponse<String>> removeDevice(
            @RequestHeader("X-User-ID") UUID userId,
            @PathVariable String token) {
        log.info("Device removed for user {}: token {}", userId, token);
        return ResponseEntity.ok(ApiResponse.ok("Device removed successfully"));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> testNotification(
            @RequestHeader("X-User-ID") UUID userId) {
        pushService.sendPush(userId.toString(), "Test Notification",
                "This is a test notification from AI Café Platform", null);
        return ResponseEntity.ok(ApiResponse.ok("Test notification sent successfully"));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        pushService.sendPush(
                request.getUserId() != null ? request.getUserId().toString() : "broadcast",
                request.getTitle(),
                request.getBody(),
                request.getData()
        );
        NotificationResponse response = NotificationResponse.builder()
                .id(UUID.randomUUID().toString())
                .status("SENT")
                .message("Notification dispatched")
                .sentAt(Instant.now())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
