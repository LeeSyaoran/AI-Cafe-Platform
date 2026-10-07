package com.example.backend.service;

import com.example.backend.entity.Payment;
import com.example.backend.entity.PaymentTransaction;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final VnPayService vnPayService;
    private final MoMoService moMoService;
    private final ZaloPayService zaloPayService;

    @Transactional
    public Payment createPayment(UUID orderId, UUID userId, UUID companyId, Double amount, String method, String returnUrl) {
        // Check if payment already exists for this order
        paymentRepository.findByOrderId(orderId).ifPresent(existing -> {
            if ("pending".equals(existing.getStatus()) || "processing".equals(existing.getStatus())) {
                throw new ApiException(HttpStatus.CONFLICT.value(), "PAYMENT_EXISTS", "Payment already exists for this order");
            }
        });

        String transactionId = generateTransactionId();
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .companyId(companyId)
                .transactionId(transactionId)
                .paymentMethod(method)
                .status("pending")
                .amount(amount)
                .currency("VND")
                .returnUrl(returnUrl)
                .expiredAt(Instant.now().plusSeconds(900)) // 15 minutes
                .build();

        // Generate payment URL based on method
        String paymentUrl = switch (method.toUpperCase()) {
            case "VNPAY" -> vnPayService.createPaymentUrl(payment);
            case "MOMO" -> moMoService.createPaymentUrl(payment);
            case "ZALOPAY" -> zaloPayService.createPaymentUrl(payment);
            default -> throw new ApiException(HttpStatus.BAD_REQUEST.value(), "INVALID_METHOD", "Invalid payment method");
        };

        payment.setPaymentUrl(paymentUrl);
        payment = paymentRepository.save(payment);

        // Log transaction
        logTransaction(payment.getId(), "CREATE", "SUCCESS", method, null, null);

        log.info("Created payment {} for order {} with method {}", payment.getId(), orderId, method);
        return payment;
    }

    @Transactional
    public void handleCallback(String method, String data) {
        log.info("Received callback from {}: {}", method, data);

        Payment payment = switch (method.toUpperCase()) {
            case "VNPAY" -> vnPayService.verifyCallback(data);
            case "MOMO" -> moMoService.verifyCallback(data);
            case "ZALOPAY" -> zaloPayService.verifyCallback(data);
            default -> throw new ApiException(HttpStatus.BAD_REQUEST.value(), "INVALID_METHOD", "Invalid payment method");
        };

        paymentRepository.save(payment);
        logTransaction(payment.getId(), "CALLBACK", "SUCCESS", method, null, null);

        // TODO: Update order status, send notification
        log.info("Payment {} completed successfully for order {}", payment.getId(), payment.getOrderId());
    }

    @Transactional
    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), "PAYMENT_NOT_FOUND", "Payment not found"));
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByOrder(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), "PAYMENT_NOT_FOUND", "Payment not found for this order"));
    }

    @Transactional
    public void cancelPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);

        if (!"pending".equals(payment.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "CANNOT_CANCEL", "Only pending payments can be cancelled");
        }

        payment.setStatus("cancelled");
        paymentRepository.save(payment);
        logTransaction(payment.getId(), "CANCEL", "SUCCESS", payment.getPaymentMethod(), null, null);

        log.info("Payment {} cancelled", paymentId);
    }

    @Transactional
    public void cancelExpiredPayments() {
        Instant now = Instant.now();
        var expiredPayments = paymentRepository.findExpiredPayments("pending", now);

        for (Payment payment : expiredPayments) {
            payment.setStatus("cancelled");
            payment.setNote("Expired - auto cancelled");
            paymentRepository.save(payment);
            logTransaction(payment.getId(), "CANCEL", "SUCCESS", payment.getPaymentMethod(), null, "Auto cancelled - expired");
        }

        log.info("Cancelled {} expired payments", expiredPayments.size());
    }

    private void logTransaction(UUID paymentId, String type, String status, String provider,
                                String providerTxId, String error) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .paymentId(paymentId)
                .type(type)
                .status(status)
                .provider(provider)
                .providerTransactionId(providerTxId)
                .errorMessage(error)
                .build();
        transactionRepository.save(tx);
    }

    private String generateTransactionId() {
        return "TX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
