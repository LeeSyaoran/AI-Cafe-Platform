package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderNotification(UUID cafeId, String event, Map<String, Object> data) {
        Map<String, Object> notification = Map.of(
                "event", event,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/orders/" + cafeId, notification);
        log.info("Sent {} notification to cafe {}", event, cafeId);
    }

    public void sendUserNotification(UUID userId, String title, String message) {
        log.info("Sending notification to user {}: {}", userId, title);
    }
}