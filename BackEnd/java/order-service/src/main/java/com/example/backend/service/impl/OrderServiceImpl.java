package com.example.backend.service.impl;

import com.example.backend.dto.request.CreateOrderRequest;
import com.example.backend.dto.response.OrderResponse;
import com.example.backend.entity.Cart;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.OrderMapper;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.service.OrderService;
import com.example.backend.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PromotionService promotionService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserIdAndCafeId(userId, request.getCafeId())
                .orElseThrow(() -> new BadRequestException("CART_EMPTY", "Cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("CART_EMPTY", "Cart is empty");
        }

        String orderNumber = generateOrderNumber();
        double subtotal = cart.getSubtotal();
        double discountAmount = 0;

        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            discountAmount = promotionService.applyPromotion(cart, request.getPromoCode());
        }

        double taxRate = 0.10;
        double taxAmount = (subtotal - discountAmount) * taxRate;
        double totalAmount = subtotal - discountAmount + taxAmount;

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .companyId(request.getCompanyId())
                .cafeId(request.getCafeId())
                .orderType(request.getOrderType())
                .status("pending")
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .totalPaid(0.0)
                .paymentStatus("pending")
                .deliveryAddressId(request.getDeliveryAddressId())
                .customerNote(request.getCustomerNote())
                .build();

        for (var cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .variantId(cartItem.getVariantId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .optionsJson(cartItem.getOptionsJson())
                    .modifiersJson(cartItem.getModifiersJson())
                    .notes(cartItem.getNotes())
                    .lineTotal(cartItem.getLineTotal())
                    .itemStatus("pending")
                    .build();
            order.addItem(orderItem);
        }

        order = orderRepository.save(order);

        cartItemRepository.deleteAllByCartId(cart.getId());
        cartRepository.delete(cart);

        log.info("Created order {} for user {}", orderNumber, userId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(UUID userId, int page, int size) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        return orders.stream().map(orderMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public OrderResponse confirmOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        if (!"pending".equals(order.getStatus())) {
            throw new BadRequestException("INVALID_STATUS", "Order cannot be confirmed in current status: " + order.getStatus());
        }
        order.setStatus("confirmed");
        order.setConfirmedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse startPreparing(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        if (!"confirmed".equals(order.getStatus())) {
            throw new BadRequestException("INVALID_STATUS", "Order cannot be prepared in current status: " + order.getStatus());
        }
        order.setStatus("preparing");
        order.setPreparingAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse markReady(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        if (!"preparing".equals(order.getStatus())) {
            throw new BadRequestException("INVALID_STATUS", "Order is not being prepared");
        }
        order.setStatus("ready");
        order.setReadyAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse completeOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        if (!"ready".equals(order.getStatus())) {
            throw new BadRequestException("INVALID_STATUS", "Order cannot be completed until ready");
        }
        order.setStatus("completed");
        order.setCompletedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason) {
        Order order = findOrderOrThrow(orderId);

        if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
            throw new BadRequestException("CANNOT_CANCEL", "Order cannot be cancelled in status: " + order.getStatus());
        }

        if ("pending".equals(order.getStatus()) || "confirmed".equals(order.getStatus())) {
            order.setStatus("cancelled");
            order.setCancellationReason(reason);
            order.setCancelledAt(Instant.now());
        } else {
            throw new BadRequestException("TOO_LATE", "Order cannot be cancelled at this stage: " + order.getStatus());
        }

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void updatePaymentStatus(UUID orderId, String paymentStatus, String paymentMethod, UUID paymentId) {
        Order order = findOrderOrThrow(orderId);
        order.setPaymentStatus(paymentStatus);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentId(paymentId);

        if ("paid".equalsIgnoreCase(paymentStatus)) {
            order.setTotalPaid(order.getTotalAmount());
        }

        orderRepository.save(order);
    }

    private Order findOrderOrThrow(UUID orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "ORD" + date + random;
    }
}
