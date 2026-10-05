package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PromotionRepository promotionRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(UUID userId, UUID companyId, UUID cafeId, String orderType,
                            UUID deliveryAddressId, String customerNote, String promoCode) {
        Cart cart = cartRepository.findByUserIdAndCafeId(userId, cafeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CART_EMPTY", "Cart is empty"));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CART_EMPTY", "Cart is empty");
        }

        String orderNumber = generateOrderNumber();
        double subtotal = cart.getSubtotal();
        double discountAmount = 0;

        if (promoCode != null && !promoCode.isEmpty()) {
            discountAmount = applyPromotion(cart, promoCode);
        }

        double taxRate = 0.10;
        double taxAmount = (subtotal - discountAmount) * taxRate;
        double totalAmount = subtotal - discountAmount + taxAmount;

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .companyId(companyId)
                .cafeId(cafeId)
                .orderType(orderType)
                .status("pending")
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .totalPaid(0.0)
                .paymentStatus("pending")
                .deliveryAddressId(deliveryAddressId)
                .customerNote(customerNote)
                .build();

        for (CartItem cartItem : items) {
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
                    .build();
            order.addItem(orderItem);
        }

        order = orderRepository.save(order);
        cartItemRepository.deleteAllByCartId(cart.getId());
        cartRepository.delete(cart);

        log.info("Created order {} for user {}", orderNumber, userId);
        return order;
    }

    @Transactional
    public Order getOrder(UUID orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));
    }

    @Transactional(readOnly = true)
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(UUID userId, int page, int size) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, page, size);
    }

    @Transactional
    public Order confirmOrder(UUID orderId) {
        Order order = getOrder(orderId);
        if (!"pending".equals(order.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Order cannot be confirmed");
        }
        order.setStatus("confirmed");
        order.setConfirmedAt(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order startPreparing(UUID orderId) {
        Order order = getOrder(orderId);
        if (!"confirmed".equals(order.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Order cannot be prepared");
        }
        order.setStatus("preparing");
        order.setPreparingAt(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order markReady(UUID orderId) {
        Order order = getOrder(orderId);
        if (!"preparing".equals(order.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Order is not being prepared");
        }
        order.setStatus("ready");
        order.setReadyAt(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order completeOrder(UUID orderId) {
        Order order = getOrder(orderId);
        if (!"ready".equals(order.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Order cannot be completed");
        }
        order.setStatus("completed");
        order.setCompletedAt(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID orderId, String reason) {
        Order order = getOrder(orderId);

        if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CANNOT_CANCEL", "Order cannot be cancelled");
        }

        if ("pending".equals(order.getStatus()) || "confirmed".equals(order.getStatus())) {
            order.setStatus("cancelled");
            order.setCancellationReason(reason);
            order.setCancelledAt(Instant.now());
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "TOO_LATE", "Order cannot be cancelled at this stage");
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void updatePaymentStatus(UUID orderId, String paymentStatus, String paymentMethod, UUID paymentId) {
        Order order = getOrder(orderId);
        order.setPaymentStatus(paymentStatus);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentId(paymentId);
        if ("paid".equals(paymentStatus)) {
            order.setTotalPaid(order.getTotalAmount());
        }
        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "ORD" + date + random;
    }

    private double applyPromotion(Cart cart, String promoCode) {
        Promotion promo = promotionRepository.findByCode(promoCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PROMO", "Invalid promotion code"));

        Instant now = Instant.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PROMO_NOT_STARTED", "Promotion has not started");
        }
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PROMO_EXPIRED", "Promotion has expired");
        }
        if (promo.getUsageLimit() != null && promo.getUsedCount() >= promo.getUsageLimit()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PROMO_LIMIT_REACHED", "Promotion usage limit reached");
        }

        double subtotal = cart.getSubtotal();
        if (subtotal < (promo.getMinOrderAmount() != null ? promo.getMinOrderAmount() : 0)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "MIN_ORDER_NOT_MET", "Minimum order amount not met");
        }

        double discount;
        if ("percentage".equals(promo.getDiscountType())) {
            discount = subtotal * (promo.getDiscountValue() / 100);
            if (promo.getMaxDiscountAmount() != null && discount > promo.getMaxDiscountAmount()) {
                discount = promo.getMaxDiscountAmount();
            }
        } else {
            discount = promo.getDiscountValue();
        }
        return discount;
    }
}