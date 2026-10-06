package com.example.backend.repository;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByCompanyId(UUID companyId, Pageable pageable);

    Page<Order> findByCafeId(UUID cafeId, Pageable pageable);

    Page<Order> findByCompanyIdAndStatus(UUID companyId, String status, Pageable pageable);

    Optional<Order> findByIdWithItems(UUID id);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.companyId = :companyId AND o.createdAt >= :startDate")
    long countOrdersInPeriod(@Param("companyId") UUID companyId, @Param("startDate") Instant startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.companyId = :companyId AND o.createdAt >= :startDate AND o.status = 'completed'")
    double sumRevenueInPeriod(@Param("companyId") UUID companyId, @Param("startDate") Instant startDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.companyId = :companyId AND o.createdAt >= :startDate GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("companyId") UUID companyId, @Param("startDate") Instant startDate);

    @Query("SELECT oi.productId, SUM(oi.quantity) as totalQty, SUM(oi.lineTotal) as totalAmount " +
           "FROM OrderItem oi WHERE oi.order.companyId = :companyId " +
           "GROUP BY oi.productId ORDER BY totalQty DESC")
    List<Object[]> findTopProducts(@Param("companyId") UUID companyId, Pageable pageable);
}
