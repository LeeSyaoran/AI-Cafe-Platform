package com.example.backend.controller;

import com.example.backend.entity.Payment;
import com.example.backend.request.CreatePaymentRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST /v1/payments
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        Payment payment = paymentService.createPayment(
                request.getOrderId(),
                userId,
                request.getCompanyId(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getReturnUrl()
        );

        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }

    // GET /v1/payments/:id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }

    // GET /v1/payments/order/:orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(@PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }

    // POST /v1/payments/:id/cancel
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(@PathVariable UUID id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.ok(ApiResponse.ok(java.util.Map.of("message", "Payment cancelled")));
    }

    // GET /v1/payments/methods
    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<java.util.List<PaymentMethodInfo>>> getPaymentMethods() {
        return ResponseEntity.ok(ApiResponse.ok(java.util.List.of(
            new PaymentMethodInfo("VNPAY", "VNPay", "Thanh toán qua VNPay"),
            new PaymentMethodInfo("MOMO", "MoMo", "Thanh toán qua MoMo"),
            new PaymentMethodInfo("ZALOPAY", "ZaloPay", "Thanh toán qua ZaloPay"),
            new PaymentMethodInfo("CREDIT", "Tài khoản", "Thanh toán bằng tài khoản"),
            new PaymentMethodInfo("COD", "Tiền mặt", "Thanh toán khi nhận hàng")
        )));
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentUrl(payment.getPaymentUrl())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public record PaymentMethodInfo(String code, String name, String description) {}
}
