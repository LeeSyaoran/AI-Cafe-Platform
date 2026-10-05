package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushService;

    /**
     * Send order confirmation notification
     */
    public void notifyOrderConfirmed(String userId, String email, String phone, String orderNumber,
                                    String orderDetails, double total) {
        // Send email
        if (email != null && !email.isEmpty()) {
            emailService.sendOrderConfirmation(email, orderNumber, orderDetails, total);
        }

        // Send SMS
        if (phone != null && !phone.isEmpty()) {
            smsService.sendOrderStatus(phone, orderNumber, "xac nhan");
        }

        // Send push notification
        pushService.sendOrderNotification(userId, orderNumber, "confirmed",
                "Đơn hàng đã được xác nhận và đang được chuẩn bị");
    }

    /**
     * Send order ready notification
     */
    public void notifyOrderReady(String userId, String email, String phone, String orderNumber) {
        // Send email
        if (email != null && !email.isEmpty()) {
            emailService.sendOrderReady(email, orderNumber);
        }

        // Send SMS
        if (phone != null && !phone.isEmpty()) {
            smsService.sendOrderStatus(phone, orderNumber, "san sang");
        }

        // Send push notification
        pushService.sendOrderNotification(userId, orderNumber, "ready",
                "Đơn hàng đã sẵn sàng để nhận");
    }

    /**
     * Send payment success notification
     */
    public void notifyPaymentSuccess(String userId, String email, String orderNumber, double amount) {
        // Send email
        if (email != null && !email.isEmpty()) {
            emailService.sendPaymentSuccess(email, orderNumber, amount);
        }

        // Send push notification
        pushService.sendOrderNotification(userId, orderNumber, "paid",
                "Thanh toán thành công " + formatCurrency(amount));
    }

    /**
     * Send points earned notification
     */
    public void notifyPointsEarned(String userId, long points, String orderNumber) {
        pushService.sendPointsNotification(userId, points,
                "Cảm ơn bạn đã mua sắm! Đơn hàng #" + orderNumber);
    }

    /**
     * Send tier upgrade notification
     */
    public void notifyTierUpgrade(String userId, String newTier) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "tier_upgrade");
        data.put("new_tier", newTier);

        pushService.sendPushNotification(userId,
                "Chúc mừng bạn đã thăng hạng!",
                "Bạn đã được nâng lên hạng " + newTier,
                data);
    }

    /**
     * Send promotion notification
     */
    public void notifyPromotion(String userId, String title, String description, String promoCode) {
        pushService.sendPromotionNotification(userId, title, description, promoCode);
    }

    /**
     * Send OTP for verification
     */
    public void sendOtp(String phone, String otp) {
        smsService.sendOtp(phone, otp);
    }

    /**
     * Send welcome email
     */
    public void sendWelcome(String email, String name) {
        emailService.sendWelcomeEmail(email, name);
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VND", amount);
    }
}
