package com.example.backend.service;

import com.example.backend.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ZaloPayService {

    @Value("${payment.zalopay.app-id}")
    private String appId;

    @Value("${payment.zalopay.key1}")
    private String key1;

    @Value("${payment.zalopay.key2}")
    private String key2;

    @Value("${payment.zalopay.api-url}")
    private String apiUrl;

    @Value("${payment.zalopay.return-url}")
    private String returnUrl;

    public String createPaymentUrl(Payment payment) {
        // ZaloPay requires specific format for order
        Map<String, Object> order = new java.util.HashMap<>();
        order.put("app_id", Long.parseLong(appId));
        order.put("app_trans_id", payment.getTransactionId()); // Format: yymmdd_xxxxxx
        order.put("app_user", payment.getUserId().toString());
        order.put("app_time", System.currentTimeMillis());
        double amountDouble = payment.getAmount() != null ? payment.getAmount() : 0.0;
        order.put("amount", (long) amountDouble);
        order.put("app_callback_url", returnUrl);
        order.put("embed_data", "{}");
        order.put("item", "[]");
        order.put("description", "Thanh toan don hang " + payment.getTransactionId());

        // Calculate checksum
        // In production: sign with HMAC-SHA256
        String data = appId + "|" + order.get("app_trans_id") + "|" + order.get("app_user")
            + "|" + order.get("amount") + "|" + order.get("app_time") + "|"
            + order.get("app_callback_url") + "|" + key1;

        String mac = hmacSHA256(data, key1);
        order.put("mac", mac);

        log.info("ZaloPay payment URL created for order: {}", payment.getTransactionId());

        // In production, call ZaloPay API
        return apiUrl + "?orderId=" + payment.getTransactionId();
    }

    public Payment verifyCallback(String callbackData) {
        log.info("ZaloPay callback received");
        Payment payment = new Payment();
        payment.setStatus("completed");
        payment.setPaidAt(java.time.Instant.now());
        return payment;
    }

    private String hmacSHA256(String data, String key) {
        try {
            javax.crypto.Mac hmac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }
}
