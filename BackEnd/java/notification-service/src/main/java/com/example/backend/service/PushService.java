package com.example.backend.service;

import java.util.Map;

public interface PushService {

    void sendPush(String userId, String title, String body, Map<String, String> data);

    void sendOrderNotification(String userId, String orderNumber, String status, String message);

    void sendPointsNotification(String userId, long points, String message);

    void sendPromotionNotification(String userId, String title, String description, String promoCode);
}
