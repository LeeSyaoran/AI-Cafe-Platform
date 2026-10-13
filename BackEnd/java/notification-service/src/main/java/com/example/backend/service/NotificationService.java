package com.example.backend.service;

import java.util.UUID;

public interface NotificationService {

    void notifyOrderConfirmed(UUID userId, String email, String phone, String orderNumber, String orderDetails, double total);

    void notifyOrderReady(UUID userId, String email, String phone, String orderNumber);

    void notifyPaymentSuccess(UUID userId, String email, String orderNumber, double amount);

    void notifyPointsEarned(UUID userId, long points, String orderNumber);

    void notifyTierUpgrade(UUID userId, String newTier);

    void notifyPromotion(UUID userId, String title, String description, String promoCode);

    void sendOtp(String phone, String otp);

    void sendWelcome(String email, String name);
}
