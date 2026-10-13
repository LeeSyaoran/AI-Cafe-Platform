package com.example.backend.service.impl;

import com.example.backend.service.EmailService;
import com.example.backend.service.NotificationService;
import com.example.backend.service.PushService;
import com.example.backend.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final PushService pushService;

    @Override
    public void notifyOrderConfirmed(UUID userId, String email, String phone, String orderNumber,
                                    String orderDetails, double total) {
        if (email != null && !email.isEmpty()) {
            emailService.sendOrderConfirmation(email, orderNumber, orderDetails, total);
        }
        if (phone != null && !phone.isEmpty()) {
            smsService.sendOrderStatus(phone, orderNumber, "xac nhan");
        }
        pushService.sendOrderNotification(userId.toString(), orderNumber, "confirmed",
                "Đơn hàng đã được xác nhận và đang được chuẩn bị");
    }

    @Override
    public void notifyOrderReady(UUID userId, String email, String phone, String orderNumber) {
        if (email != null && !email.isEmpty()) {
            emailService.sendOrderReady(email, orderNumber);
        }
        if (phone != null && !phone.isEmpty()) {
            smsService.sendOrderStatus(phone, orderNumber, "san sang");
        }
        pushService.sendOrderNotification(userId.toString(), orderNumber, "ready",
                "Đơn hàng đã sẵn sàng để nhận");
    }

    @Override
    public void notifyPaymentSuccess(UUID userId, String email, String orderNumber, double amount) {
        if (email != null && !email.isEmpty()) {
            emailService.sendPaymentSuccess(email, orderNumber, amount);
        }
        pushService.sendOrderNotification(userId.toString(), orderNumber, "paid",
                "Thanh toán thành công " + formatCurrency(amount));
    }

    @Override
    public void notifyPointsEarned(UUID userId, long points, String orderNumber) {
        pushService.sendPointsNotification(userId.toString(), points,
                "Cảm ơn bạn đã mua sắm! Đơn hàng #" + orderNumber);
    }

    @Override
    public void notifyTierUpgrade(UUID userId, String newTier) {
        Map<String, String> data = Map.of(
                "type", "tier_upgrade",
                "new_tier", newTier
        );
        pushService.sendPush(userId.toString(),
                "Chúc mừng bạn đã thăng hạng!",
                "Bạn đã được nâng lên hạng " + newTier, data);
    }

    @Override
    public void notifyPromotion(UUID userId, String title, String description, String promoCode) {
        pushService.sendPromotionNotification(userId.toString(), title, description, promoCode);
    }

    @Override
    public void sendOtp(String phone, String otp) {
        smsService.sendOtp(phone, otp);
    }

    @Override
    public void sendWelcome(String email, String name) {
        emailService.sendWelcome(email, name);
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VND", amount);
    }
}
