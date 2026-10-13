package com.example.backend.service.impl;

import com.example.backend.dto.request.CreatePaymentRequest;
import com.example.backend.dto.response.PaymentMethodResponse;
import com.example.backend.dto.response.PaymentResponse;
import com.example.backend.entity.Payment;
import com.example.backend.entity.PaymentTransaction;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.gateway.MoMoGateway;
import com.example.backend.gateway.VnPayGateway;
import com.example.backend.gateway.ZaloPayGateway;
import com.example.backend.mapper.PaymentMapper;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.repository.PaymentTransactionRepository;
import com.example.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final VnPayGateway vnPayGateway;
    private final MoMoGateway moMoGateway;
    private final ZaloPayGateway zaloPayGateway;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(UUID userId, CreatePaymentRequest request) {
        paymentRepository.findByOrderId(request.getOrderId()).ifPresent(existing -> {
            if ("pending".equalsIgnoreCase(existing.getStatus()) || "processing".equalsIgnoreCase(existing.getStatus())) {
                throw new ConflictException("PAYMENT_EXISTS", "Pending payment already exists for order: " + request.getOrderId());
            }
        });

        String transactionId = generateTransactionId();
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(userId)
                .companyId(request.getCompanyId())
                .transactionId(transactionId)
                .paymentMethod(request.getPaymentMethod())
                .status("pending")
                .amount(request.getAmount())
                .currency("VND")
                .returnUrl(request.getReturnUrl())
                .expiredAt(Instant.now().plusSeconds(900)) // 15 minutes
                .build();

        String paymentUrl = switch (request.getPaymentMethod().toUpperCase()) {
            case "VNPAY" -> vnPayGateway.createPaymentUrl(payment);
            case "MOMO" -> moMoGateway.createPaymentUrl(payment);
            case "ZALOPAY" -> zaloPayGateway.createPaymentUrl(payment);
            default -> throw new BadRequestException("INVALID_METHOD", "Unsupported payment method: " + request.getPaymentMethod());
        };

        payment.setPaymentUrl(paymentUrl);
        payment = paymentRepository.save(payment);

        logTransaction(payment.getId(), "CREATE", "SUCCESS", request.getPaymentMethod(), null, null);
        log.info("Created payment {} for order {} with method {}", payment.getId(), request.getOrderId(), request.getPaymentMethod());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void handleCallback(String method, String data) {
        log.info("Received callback from provider {}: {}", method, data);

        Payment payment = switch (method.toUpperCase()) {
            case "VNPAY" -> vnPayGateway.verifyCallback(data);
            case "MOMO" -> moMoGateway.verifyCallback(data);
            case "ZALOPAY" -> zaloPayGateway.verifyCallback(data);
            default -> throw new BadRequestException("INVALID_METHOD", "Unsupported callback provider: " + method);
        };

        paymentRepository.save(payment);
        logTransaction(payment.getId(), "CALLBACK", "SUCCESS", method, null, null);
        log.info("Payment {} completed successfully for order {}", payment.getId(), payment.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (!"pending".equalsIgnoreCase(payment.getStatus())) {
            throw new BadRequestException("CANNOT_CANCEL", "Only pending payments can be cancelled");
        }

        payment.setStatus("cancelled");
        paymentRepository.save(payment);
        logTransaction(payment.getId(), "CANCEL", "SUCCESS", payment.getPaymentMethod(), null, null);
        log.info("Payment {} cancelled", paymentId);
    }

    @Override
    @Transactional
    public void cancelExpiredPayments() {
        Instant now = Instant.now();
        List<Payment> expiredPayments = paymentRepository.findExpiredPayments("pending", now);

        for (Payment payment : expiredPayments) {
            payment.setStatus("cancelled");
            payment.setNote("Expired - auto cancelled");
            paymentRepository.save(payment);
            logTransaction(payment.getId(), "CANCEL", "SUCCESS", payment.getPaymentMethod(), null, "Auto cancelled - expired");
        }

        log.info("Cancelled {} expired payments", expiredPayments.size());
    }

    @Override
    public List<PaymentMethodResponse> getPaymentMethods() {
        return List.of(
                new PaymentMethodResponse("VNPAY", "VNPay", "Thanh toan qua VNPay QR / ATM / The quoc te"),
                new PaymentMethodResponse("MOMO", "MoMo", "Thanh toan qua vi MoMo"),
                new PaymentMethodResponse("ZALOPAY", "ZaloPay", "Thanh toan qua vi ZaloPay"),
                new PaymentMethodResponse("CREDIT", "Tai khoan tich luy", "Thanh toan bang so du tai khoan ca phe"),
                new PaymentMethodResponse("COD", "Tien mat", "Thanh toan khi nhan hang tai quan")
        );
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
