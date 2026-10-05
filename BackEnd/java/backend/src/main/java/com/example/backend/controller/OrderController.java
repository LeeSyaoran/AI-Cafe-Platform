package com.example.backend.controller;

import com.example.backend.entity.Order;
import com.example.backend.request.CreateOrderRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        Order order = orderService.createOrder(
                userId, request.getCompanyId(), request.getCafeId(),
                request.getOrderType(), request.getDeliveryAddressId(),
                request.getCustomerNote(), request.getPromoCode());

        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Order> orders = orderService.getUserOrders(userId, page, size);
        List<OrderResponse> responses = orders.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(@PathVariable UUID id) {
        Order order = orderService.confirmOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @PostMapping("/{id}/prepare")
    public ResponseEntity<ApiResponse<OrderResponse>> startPreparing(@PathVariable UUID id) {
        Order order = orderService.startPreparing(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @PostMapping("/{id}/ready")
    public ResponseEntity<ApiResponse<OrderResponse>> markReady(@PathVariable UUID id) {
        Order order = orderService.markReady(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(@PathVariable UUID id) {
        Order order = orderService.completeOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        Order order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .companyId(order.getCompanyId())
                .cafeId(order.getCafeId())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .customerNote(order.getCustomerNote())
                .estimatedReadyTime(order.getEstimatedReadyTime())
                .createdAt(order.getCreatedAt())
                .confirmedAt(order.getConfirmedAt())
                .completedAt(order.getCompletedAt())
                .cancelledAt(order.getCancelledAt())
                .build();
    }
}

@lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
class OrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private UUID companyId;
    private UUID cafeId;
    private UUID seatId;
    private String orderType;
    private String status;
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double deliveryFee;
    private Double totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String customerNote;
    private java.time.Instant estimatedReadyTime;
    private java.time.Instant createdAt;
    private java.time.Instant confirmedAt;
    private java.time.Instant preparingAt;
    private java.time.Instant readyAt;
    private java.time.Instant completedAt;
    private java.time.Instant cancelledAt;
}