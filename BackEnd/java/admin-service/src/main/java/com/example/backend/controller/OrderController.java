package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> getOrders(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID cafeId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Order> orders = orderService.getOrders(companyId, cafeId, status, page, size);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request) {

        Order order = orderService.updateOrderStatus(id, request.getStatus(), request.getStaffNote());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<Order>> assignOrder(
            @PathVariable UUID id,
            @RequestBody AssignOrderRequest request) {

        Order order = orderService.assignOrder(id, request.getStaffId(), request.getCafeId());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/{id}/note")
    public ResponseEntity<ApiResponse<Order>> updateOrderNote(
            @PathVariable UUID id,
            @RequestBody UpdateNoteRequest request) {

        Order order = orderService.updateOrderNote(id, request.getNote());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PutMapping("/{id}/items/{itemId}/status")
    public ResponseEntity<ApiResponse<OrderItem>> updateItemStatus(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @RequestBody UpdateItemStatusRequest request) {

        OrderItem item = orderService.updateOrderItemStatus(id, itemId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) CancelOrderRequest request) {

        String reason = request != null ? request.getReason() : "Cancelled by admin";
        Order order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<Order>> refundOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) RefundOrderRequest request) {

        String reason = request != null ? request.getReason() : "Refund requested by admin";
        Order order = orderService.refundOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }
}
