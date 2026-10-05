package com.example.backend.service;

import com.example.backend.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class MoMoService {

    @Value("${payment.momo.partner-code}")
    private String partnerCode;

    @Value("${payment.momo.access-key}")
    private String accessKey;

    @Value("${payment.momo.secret-key}")
    private String secretKey;

    @Value("${payment.momo.api-url}")
    private String apiUrl;

    @Value("${payment.momo.return-url}")
    private String returnUrl;

    private static final String REQUEST_TYPE = "captureWallet";

    public String createPaymentUrl(Payment payment) {
        long requestId = System.currentTimeMillis();
        long amount = (long) payment.getAmount();

        Map<String, Object> params = new HashMap<>();
        params.put("partnerCode", partnerCode);
        params.put("partnerType", "MOMO");
        params.put("version", "2.0");
        params.put("requestId", requestId);
        params.put("orderId", payment.getTransactionId());
        params.put("amount", amount);
        params.put("orderInfo", "Thanh toan don hang " + payment.getTransactionId());
        params.put("returnUrl", returnUrl);
        params.put("notifyUrl", returnUrl + "/notify");
        params.put("requestType", REQUEST_TYPE);
        params.put("extraData", "");

        String rawSignature = String.format(
            "partnerCode=%s&accessKey=%s&requestId=%d&amount=%d&orderId=%s&orderInfo=%s&returnUrl=%s&notifyUrl=%s&extraData=",
            partnerCode, accessKey, requestId, amount, payment.getTransactionId(),
            "Thanh toan don hang " + payment.getTransactionId(), returnUrl, returnUrl + "/notify"
        );

        String signature = hmacSHA256(rawSignature, secretKey);
        params.put("signature", signature);

        log.info("MoMo payment URL created for order: {}", payment.getTransactionId());
        return apiUrl + "?orderId=" + payment.getTransactionId();
    }

    public Payment verifyCallback(String callbackData) {
        log.info("MoMo callback received");
        Payment payment = new Payment();
        payment.setStatus("completed");
        payment.setPaidAt(Instant.now());
        return payment;
    }

    private String hmacSHA256(String data, String key) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
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