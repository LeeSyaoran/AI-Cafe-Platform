package com.example.backend.controller;

import com.example.backend.entity.Payment;
import com.example.backend.request.CreatePaymentRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        Payment payment = paymentService.createPayment(
                request.getOrderId(), userId, request.getCompanyId(),
                request.getAmount(), request.getPaymentMethod(), request.getReturnUrl());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(@PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(@PathVariable UUID id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.ok(ApiResponse.ok(java.util.Map.of("message", "Payment cancelled")));
    }

    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<List<PaymentMethodInfo>>> getPaymentMethods() {
        return ResponseEntity.ok(ApiResponse.ok(List.of(
            new PaymentMethodInfo("VNPAY", "VNPay", "Thanh toán qua VNPay"),
            new PaymentMethodInfo("MOMO", "MoMo", "Thanh toán qua MoMo"),
            new PaymentMethodInfo("ZALOPAY", "ZaloPay", "Thanh toán qua ZaloPay"),
            new PaymentMethodInfo("CREDIT", "Tài khoản", "Thanh toán bằng tài khoản"),
            new PaymentMethodInfo("COD", "Tiền mặt", "Thanh toán khi nhận hàng")
        )));
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId()).orderId(p.getOrderId()).transactionId(p.getTransactionId())
                .paymentMethod(p.getPaymentMethod()).status(p.getStatus()).amount(p.getAmount())
                .currency(p.getCurrency()).paymentUrl(p.getPaymentUrl())
                .paidAt(p.getPaidAt()).createdAt(p.getCreatedAt()).build();
    }

    public record PaymentMethodInfo(String code, String name, String description) {}
}

@lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private String transactionId;
    private String paymentMethod;
    private String status;
    private Double amount;
    private String currency;
    private String paymentUrl;
    private java.time.Instant paidAt;
    private java.time.Instant createdAt;
}