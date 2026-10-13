package com.example.backend.controller;

import com.example.backend.dto.request.CreatePaymentRequest;
import com.example.backend.dto.response.PaymentMethodResponse;
import com.example.backend.dto.response.PaymentResponse;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader(value = "X-User-ID", required = false) UUID userId) {
        if (userId == null) {
            throw new UnauthorizedException("UNAUTHORIZED", "Missing X-User-ID header");
        }
        PaymentResponse response = paymentService.createPayment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPayment(id)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByOrder(orderId)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(@PathVariable UUID id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getPaymentMethods() {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentMethods()));
    }

    @PostMapping("/callback/{method}")
    public ResponseEntity<ApiResponse<String>> handleCallback(
            @PathVariable String method,
            @RequestBody String data) {
        paymentService.handleCallback(method, data);
        return ResponseEntity.ok(ApiResponse.ok("Callback processed successfully"));
    }
}
