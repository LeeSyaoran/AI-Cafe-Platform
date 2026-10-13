package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    Page<Order> getOrders(UUID companyId, UUID cafeId, String status, int page, int size);

    Order getOrder(UUID orderId);

    Order updateOrderStatus(UUID orderId, String newStatus, String staffNote);

    Order assignOrder(UUID orderId, UUID staffId, UUID cafeId);

    Order updateOrderNote(UUID orderId, String note);

    OrderItem updateOrderItemStatus(UUID orderId, UUID itemId, String itemStatus);

    Order cancelOrder(UUID orderId, String reason);

    Order refundOrder(UUID orderId, String reason);

    long countOrdersInPeriod(UUID companyId, UUID cafeId, Instant startDate);

    double sumRevenueInPeriod(UUID companyId, UUID cafeId, Instant startDate);

    List<Object[]> countOrdersByStatus(UUID companyId, UUID cafeId, Instant startDate);

    long countByStatus(UUID companyId, UUID cafeId, String status);
}
