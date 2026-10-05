package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${notification.sms.provider:mock}")
    private String provider;

    @Value("${notification.sms.api-key:}")
    private String apiKey;

    @Value("${notification.sms.sender-name:AICafe}")
    private String senderName;

    @Async
    public void sendSms(String phone, String message) {
        try {
            // In production, integrate with SMS provider (Twilio, VNPT, Viettel, etc.)
            switch (provider.toLowerCase()) {
                case "twilio" -> sendViaTwilio(phone, message);
                case "vnpt" -> sendViaVNPT(phone, message);
                case "viettel" -> sendViaViettel(phone, message);
                default -> {
                    log.info("Mock SMS to {}: {}", phone, message);
                }
            }
            log.info("SMS sent to {}: {}", phone, message);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phone, e.getMessage());
        }
    }

    @Async
    public void sendOtp(String phone, String otp) {
        String message = String.format("[AI Cafe] Ma xac minh cua ban la: %s. Ma co hieu luc trong 5 phut.", otp);
        sendSms(phone, message);
    }

    @Async
    public void sendOrderStatus(String phone, String orderNumber, String status) {
        String message = String.format("[AI Cafe] Don hang #%s da duoc %s.", orderNumber, status);
        sendSms(phone, message);
    }

    private void sendViaTwilio(String phone, String message) {
        // Twilio integration
        log.info("Sending via Twilio to {}", phone);
    }

    private void sendViaVNPT(String phone, String message) {
        // VNPT SMS integration
        log.info("Sending via VNPT to {}", phone);
    }

    private void sendViaViettel(String phone, String message) {
        // Viettel SMS integration
        log.info("Sending via Viettel to {}", phone);
    }
}
