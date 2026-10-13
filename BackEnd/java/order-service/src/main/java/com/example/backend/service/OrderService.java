package com.example.backend.service;

import com.example.backend.dto.request.CreateOrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID userId, CreateOrderRequest request);

    OrderResponse getOrder(UUID orderId);

    OrderResponse getOrderByNumber(String orderNumber);

    List<OrderResponse> getUserOrders(UUID userId, int page, int size);

    OrderResponse confirmOrder(UUID orderId);

    OrderResponse startPreparing(UUID orderId);

    OrderResponse markReady(UUID orderId);

    OrderResponse completeOrder(UUID orderId);

    OrderResponse cancelOrder(UUID orderId, String reason);

    void updatePaymentStatus(UUID orderId, String paymentStatus, String paymentMethod, UUID paymentId);
}
