package com.example.backend.service.impl;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService  {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<Order> getOrders(UUID companyId, UUID cafeId, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null && !status.isEmpty()) {
            return orderRepository.findByCompanyIdAndStatus(companyId, status, pageRequest);
        }
        if (cafeId != null) {
            return orderRepository.findByCafeId(cafeId, pageRequest);
        }
        return orderRepository.findByCompanyId(companyId, pageRequest);
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, String newStatus, String staffNote) {
        Order order = getOrder(orderId);
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        if (staffNote != null) {
            order.setStaffNote(staffNote);
        }

        Instant now = Instant.now();
        switch (newStatus) {
            case "confirmed" -> order.setConfirmedAt(now);
            case "preparing" -> order.setPreparingAt(now);
            case "ready" -> order.setReadyAt(now);
            case "completed" -> order.setCompletedAt(now);
            case "cancelled" -> order.setCancelledAt(now);
        }

        log.info("Order {} status updated to {}", orderId, newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order assignOrder(UUID orderId, UUID staffId, UUID cafeId) {
        Order order = getOrder(orderId);
        String note = (order.getStaffNote() != null ? order.getStaffNote() + "\n" : "") +
                "Assigned to staff: " + staffId;
        order.setStaffNote(note);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderNote(UUID orderId, String note) {
        Order order = getOrder(orderId);
        order.setStaffNote(note);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderItem updateOrderItemStatus(UUID orderId, UUID itemId, String itemStatus) {
        Order order = getOrder(orderId);

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemId));

        item.setItemStatus(itemStatus);
        if ("ready".equals(itemStatus)) {
            item.setReadyAt(Instant.now());
        }

        orderRepository.save(order);
        return item;
    }

    @Transactional
    public Order cancelOrder(UUID orderId, String reason) {
        Order order = getOrder(orderId);

        if (!canCancel(order.getStatus())) {
            throw new BadRequestException("CANNOT_CANCEL",
                    "Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.setStatus("cancelled");
        order.setCancellationReason(reason);
        order.setCancelledAt(Instant.now());

        log.info("Order {} cancelled. Reason: {}", orderId, reason);
        return orderRepository.save(order);
    }

    @Transactional
    public Order refundOrder(UUID orderId, String reason) {
        Order order = getOrder(orderId);

        if (!"completed".equals(order.getStatus()) && !"cancelled".equals(order.getStatus())) {
            throw new BadRequestException("CANNOT_REFUND",
                    "Only completed or cancelled orders can be refunded");
        }

        order.setPaymentStatus("refunded");
        log.info("Order {} refund initiated. Reason: {}", orderId, reason);
        return orderRepository.save(order);
    }

    // Dashboard queries
    @Transactional(readOnly = true)
    public long countOrdersInPeriod(UUID companyId, UUID cafeId, Instant startDate) {
        return orderRepository.countOrdersInPeriod(companyId, cafeId, startDate);
    }

    @Transactional(readOnly = true)
    public double sumRevenueInPeriod(UUID companyId, UUID cafeId, Instant startDate) {
        return orderRepository.sumRevenueInPeriod(companyId, cafeId, startDate);
    }

    @Transactional(readOnly = true)
    public List<Object[]> countOrdersByStatus(UUID companyId, UUID cafeId, Instant startDate) {
        return orderRepository.countOrdersByStatus(companyId, cafeId, startDate);
    }

    @Transactional(readOnly = true)
    public long countByStatus(UUID companyId, UUID cafeId, String status) {
        return orderRepository.countByStatus(companyId, cafeId, status);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        boolean valid = switch (currentStatus) {
            case "pending" -> List.of("confirmed", "cancelled").contains(newStatus);
            case "confirmed" -> List.of("preparing", "cancelled").contains(newStatus);
            case "preparing" -> List.of("ready", "cancelled").contains(newStatus);
            case "ready" -> List.of("completed", "cancelled").contains(newStatus);
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException("INVALID_TRANSITION",
                    "Cannot transition from " + currentStatus + " to " + newStatus);
        }
    }

    private boolean canCancel(String status) {
        return List.of("pending", "confirmed").contains(status);
    }
}
