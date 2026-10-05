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
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /v1/orders
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        Order order = orderService.createOrder(
                userId,
                request.getCompanyId(),
                request.getCafeId(),
                request.getOrderType(),
                request.getDeliveryAddressId(),
                request.getCustomerNote(),
                request.getPromoCode()
        );

        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // GET /v1/orders
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<java.util.List<OrderResponse>>> getOrders(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Order> orders = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(orders.stream().map(this::toResponse).toList()));
    }

    // GET /v1/orders/:id
    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // GET /v1/orders/number/:orderNumber
    @GetMapping("/orders/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // PUT /v1/orders/:id/confirm (Staff)
    @PutMapping("/orders/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(@PathVariable UUID id) {
        Order order = orderService.confirmOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // PUT /v1/orders/:id/preparing (Staff)
    @PutMapping("/orders/{id}/preparing")
    public ResponseEntity<ApiResponse<OrderResponse>> startPreparing(@PathVariable UUID id) {
        Order order = orderService.startPreparing(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // PUT /v1/orders/:id/ready (Staff)
    @PutMapping("/orders/{id}/ready")
    public ResponseEntity<ApiResponse<OrderResponse>> markReady(@PathVariable UUID id) {
        Order order = orderService.markReady(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // PUT /v1/orders/:id/complete (Staff)
    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(@PathVariable UUID id) {
        Order order = orderService.completeOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }

    // POST /v1/orders/:id/cancel
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) java.util.Map<String, String> body) {

        String reason = body != null ? body.get("reason") : null;
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
                .customerNote(order.getCustomerNote())
                .createdAt(order.getCreatedAt())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OrderResponse {
        private UUID id;
        private String orderNumber;
        private UUID userId;
        private UUID companyId;
        private UUID cafeId;
        private String orderType;
        private String status;
        private Double subtotal;
        private Double discountAmount;
        private Double taxAmount;
        private Double totalAmount;
        private String paymentStatus;
        private String customerNote;
        private java.time.Instant createdAt;
    }
}
