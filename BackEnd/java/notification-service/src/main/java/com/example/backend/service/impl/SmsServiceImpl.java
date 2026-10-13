package com.example.backend.service.impl;

import com.example.backend.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${notification.sms.provider:mock}")
    private String provider;

    @Override
    @Async
    public void send(String phone, String message) {
        try {
            switch (provider.toLowerCase()) {
                case "twilio" -> sendViaTwilio(phone, message);
                case "vnpt" -> sendViaVNPT(phone, message);
                case "viettel" -> sendViaViettel(phone, message);
                default -> log.info("Mock SMS to {}: {}", phone, message);
            }
            log.info("SMS sent to {}: {}", phone, message);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phone, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOtp(String phone, String otp) {
        String message = String.format("[AI Cafe] Ma xac minh cua ban la: %s. Ma co hieu luc trong 5 phut.", otp);
        send(phone, message);
    }

    @Override
    @Async
    public void sendOrderStatus(String phone, String orderNumber, String status) {
        String message = String.format("[AI Cafe] Don hang #%s da duoc %s.", orderNumber, status);
        send(phone, message);
    }

    private void sendViaTwilio(String phone, String message) {
        log.info("Sending via Twilio to {}", phone);
    }

    private void sendViaVNPT(String phone, String message) {
        log.info("Sending via VNPT to {}", phone);
    }

    private void sendViaViettel(String phone, String message) {
        log.info("Sending via Viettel to {}", phone);
    }
}
