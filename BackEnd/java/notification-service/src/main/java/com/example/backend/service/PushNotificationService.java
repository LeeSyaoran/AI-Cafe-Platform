package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    @Value("${notification.push.provider:mock}")
    private String provider;

    @Value("${notification.push.fcm-server-key:}")
    private String fcmServerKey;

    @Value("${notification.push.apns-key:}")
    private String apnsKey;

    @Async
    public void sendPushNotification(String userId, String title, String body, Map<String, String> data) {
        try {
            switch (provider.toLowerCase()) {
                case "fcm" -> sendViaFCM(userId, title, body, data);
                case "apns" -> sendViaAPNS(userId, title, body, data);
                default -> {
                    log.info("Mock Push to user {}: {} - {}", userId, title, body);
                }
            }
            log.info("Push notification sent to user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Failed to send push notification to {}: {}", userId, e.getMessage());
        }
    }

    @Async
    public void sendOrderNotification(String userId, String orderNumber, String status, String message) {
        Map<String, String> data = Map.of(
                "type", "order",
                "order_number", orderNumber,
                "status", status
        );
        String title = "Đơn hàng #" + orderNumber;
        sendPushNotification(userId, title, message != null ? message : "Cập nhật đơn hàng", data);
    }

    @Async
    public void sendPointsNotification(String userId, long points, String message) {
        Map<String, String> data = Map.of(
                "type", "loyalty",
                "points", String.valueOf(points)
        );
        String title = "Bạn nhận được " + points + " điểm!";
        sendPushNotification(userId, title, message != null ? message : "Cảm ơn bạn đã mua sắm tại AI Café", data);
    }

    @Async
    public void sendPromotionNotification(String userId, String title, String description, String promoCode) {
        Map<String, String> data = Map.of(
                "type", "promotion",
                "promo_code", promoCode
        );
        sendPushNotification(userId, title, description, data);
    }

    @Async
    public void sendChatNotification(String userId, String senderName, String messagePreview) {
        Map<String, String> data = Map.of(
                "type", "chat",
                "sender", senderName
        );
        String title = "Tin nhắn mới từ " + senderName;
        sendPushNotification(userId, title, messagePreview, data);
    }

    private void sendViaFCM(String userId, String title, String body, Map<String, String> data) {
        // Firebase Cloud Messaging integration
        // https://firebase.google.com/docs/cloud-messaging
        log.info("Sending FCM notification to user: {}", userId);
    }

    private void sendViaAPNS(String userId, String title, String body, Map<String, String> data) {
        // Apple Push Notification Service integration
        // https://developer.apple.com/documentation/usernotifications
        log.info("Sending APNS notification to user: {}", userId);
    }

    // Device token management
    public void registerDeviceToken(String userId, String token, String platform) {
        // Store device token for user
        // Platform: ios, android, web
        log.info("Registered {} device token for user {}", platform, userId);
    }

    public void removeDeviceToken(String userId, String token) {
        // Remove device token
        log.info("Removed device token for user {}", userId);
    }
}
