package com.example.backend.controller;

import com.example.backend.dto.request.CreateOrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<OrderResponse> orders = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrder(id)));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderByNumber(orderNumber)));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.confirmOrder(id)));
    }

    @PutMapping("/{id}/preparing")
    public ResponseEntity<ApiResponse<OrderResponse>> startPreparing(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.startPreparing(id)));
    }

    @PutMapping("/{id}/ready")
    public ResponseEntity<ApiResponse<OrderResponse>> markReady(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.markReady(id)));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.completeOrder(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(ApiResponse.ok(orderService.cancelOrder(id, reason)));
    }
}
