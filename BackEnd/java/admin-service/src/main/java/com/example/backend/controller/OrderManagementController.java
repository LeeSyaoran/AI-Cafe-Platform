package com.example.backend.controller;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/orders")
@RequiredArgsConstructor
public class OrderManagementController {

    private final OrderManagementService orderService;

    // GET /v1/admin/orders
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

    // GET /v1/admin/orders/:id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // PUT /v1/admin/orders/:id/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request) {

        Order order = orderService.updateOrderStatus(id, request.getStatus(), request.getStaffNote());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // PUT /v1/admin/orders/:id/assign
    @PutMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<Order>> assignOrder(
            @PathVariable UUID id,
            @RequestBody AssignOrderRequest request) {

        Order order = orderService.assignOrder(id, request.getStaffId(), request.getCafeId());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // PUT /v1/admin/orders/:id/note
    @PutMapping("/{id}/note")
    public ResponseEntity<ApiResponse<Order>> updateOrderNote(
            @PathVariable UUID id,
            @RequestBody UpdateNoteRequest request) {

        Order order = orderService.updateOrderNote(id, request.getNote());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // PUT /v1/admin/orders/:id/items/:itemId/status
    @PutMapping("/{id}/items/{itemId}/status")
    public ResponseEntity<ApiResponse<OrderItem>> updateItemStatus(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @RequestBody UpdateItemStatusRequest request) {

        OrderItem item = orderService.updateOrderItemStatus(id, itemId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    // POST /v1/admin/orders/:id/cancel
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) CancelOrderRequest request) {

        String reason = request != null ? request.getReason() : "Cancelled by admin";
        Order order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // POST /v1/admin/orders/:id/refund
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<Order>> refundOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) RefundOrderRequest request) {

        String reason = request != null ? request.getReason() : "Refund requested by admin";
        Order order = orderService.refundOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // Request DTOs
    @lombok.Data
    public static class UpdateStatusRequest {
        private String status;
        private String staffNote;
    }

    @lombok.Data
    public static class AssignOrderRequest {
        private UUID staffId;
        private UUID cafeId;
    }

    @lombok.Data
    public static class UpdateNoteRequest {
        private String note;
    }

    @lombok.Data
    public static class UpdateItemStatusRequest {
        private String status;
    }

    @lombok.Data
    public static class CancelOrderRequest {
        private String reason;
    }

    @lombok.Data
    public static class RefundOrderRequest {
        private String reason;
    }
}
