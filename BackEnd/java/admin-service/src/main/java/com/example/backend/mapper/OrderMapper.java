package com.example.backend.mapper;

import com.example.backend.dto.request.CancelOrderRequest;
import com.example.backend.dto.request.UpdateNoteRequest;
import com.example.backend.dto.request.UpdateStatusRequest;
import com.example.backend.entity.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderMapper {

    public void applyStatusUpdate(Order order, UpdateStatusRequest request) {
        if (order == null || request == null) return;

        order.setStatus(request.getStatus());
        if ("completed".equalsIgnoreCase(request.getStatus())) {
            order.setCompletedAt(Instant.now());
        }
    }

    public void applyCancellation(Order order, CancelOrderRequest request) {
        if (order == null || request == null) return;

        order.setStatus("cancelled");
        order.setCancellationReason(request.getReason());
        order.setCancelledAt(Instant.now());
    }

    public void applyNoteUpdate(Order order, UpdateNoteRequest request) {
        if (order == null || request == null) return;

        order.setStaffNote(request.getNote());
    }
}
