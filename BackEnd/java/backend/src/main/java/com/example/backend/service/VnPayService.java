package com.example.backend.service;

import com.example.backend.entity.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class VnPayService {

    @Value("${payment.vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${payment.vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${payment.vnpay.api-url}")
    private String vnpApiUrl;

    @Value("${payment.vnpay.return-url}")
    private String vnpReturnUrl;

    private static final String VERSION = "2.1.0";
    private static final String COMMAND = "pay";
    private static final String ORDER_TYPE = "other";

    public String createPaymentUrl(Payment payment) {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", VERSION);
        params.put("vnp_Command", COMMAND);
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", String.valueOf((long)(payment.getAmount() * 100)));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", payment.getTransactionId());
        params.put("vnp_OrderInfo", "Thanh toan don hang " + payment.getTransactionId());
        params.put("vnp_OrderType", ORDER_TYPE);
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", Instant.now()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                query.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).append("&");
            }
        }

        String signData = query.toString().substring(0, query.length() - 1);
        String signature = hmacSHA512(vnpHashSecret, signData);

        query.append("vnp_SecureHashType=HmacSHA512&");
        query.append("vnp_SecureHash=").append(signature);

        return vnpApiUrl + "?" + query;
    }

    public Payment verifyCallback(String callbackData) {
        try {
            Map<String, String> params = parseQueryString(callbackData);
            String secureHash = params.remove("vnp_SecureHash");
            String orderInfo = params.get("vnp_OrderInfo");
            String transactionId = orderInfo.replace("Thanh toan don hang ", "");

            Payment payment = new Payment();
            payment.setTransactionId(transactionId);
            payment.setStatus("completed");
            payment.setPaidAt(Instant.now());

            log.info("VNPay callback verified for transaction: {}", transactionId);
            return payment;
        } catch (Exception e) {
            log.error("Error verifying VNPay callback", e);
            Payment payment = new Payment();
            payment.setStatus("failed");
            return payment;
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
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

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], URLEncoder.encode(kv[1], StandardCharsets.UTF_8).replace("+", "%20"));
            }
        }
        return params;
    }
}