package com.example.backend.service;

import com.example.backend.dto.request.CreatePaymentRequest;
import com.example.backend.dto.response.PaymentMethodResponse;
import com.example.backend.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(UUID userId, CreatePaymentRequest request);

    PaymentResponse getPayment(UUID paymentId);

    PaymentResponse getPaymentByOrder(UUID orderId);

    void cancelPayment(UUID paymentId);

    void handleCallback(String method, String data);

    void cancelExpiredPayments();

    List<PaymentMethodResponse> getPaymentMethods();
}
