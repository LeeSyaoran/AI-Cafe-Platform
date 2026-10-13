package com.example.backend.service.impl;

import com.example.backend.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    @Value("${notification.push.provider:mock}")
    private String provider;

    @Override
    @Async
    public void sendPush(String userId, String title, String body, Map<String, String> data) {
        try {
            switch (provider.toLowerCase()) {
                case "fcm" -> sendViaFCM(userId, title, body, data);
                case "apns" -> sendViaAPNS(userId, title, body, data);
                default -> log.info("Mock Push to user {}: {} - {}", userId, title, body);
            }
            log.info("Push notification sent to user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Failed to send push notification to {}: {}", userId, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderNotification(String userId, String orderNumber, String status, String message) {
        Map<String, String> data = Map.of(
                "type", "order",
                "order_number", orderNumber,
                "status", status
        );
        sendPush(userId, "Đơn hàng #" + orderNumber, message != null ? message : "Cập nhật đơn hàng", data);
    }

    @Override
    @Async
    public void sendPointsNotification(String userId, long points, String message) {
        Map<String, String> data = Map.of(
                "type", "loyalty",
                "points", String.valueOf(points)
        );
        sendPush(userId, "Bạn nhận được " + points + " điểm!",
                message != null ? message : "Cảm ơn bạn đã mua sắm tại AI Café", data);
    }

    @Override
    @Async
    public void sendPromotionNotification(String userId, String title, String description, String promoCode) {
        Map<String, String> data = Map.of(
                "type", "promotion",
                "promo_code", promoCode != null ? promoCode : ""
        );
        sendPush(userId, title, description, data);
    }

    private void sendViaFCM(String userId, String title, String body, Map<String, String> data) {
        log.info("Sending FCM notification to user: {}", userId);
    }

    private void sendViaAPNS(String userId, String title, String body, Map<String, String> data) {
        log.info("Sending APNS notification to user: {}", userId);
    }
}
