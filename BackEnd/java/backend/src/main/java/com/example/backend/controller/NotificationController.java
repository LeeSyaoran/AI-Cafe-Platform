package com.example.backend.controller;

import com.example.backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            @RequestHeader("X-User-ID") UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "notifications", java.util.List.of(),
                "unreadCount", 0
        )));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@RequestHeader("X-User-ID") UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

@MessageMapping("/order/{cafeId}")
class WebSocketOrderController {

    @SendTo("/topic/orders/{cafeId}")
    public Map<String, Object> handleOrderUpdate(Map<String, Object> orderUpdate) {
        return orderUpdate;
    }
}