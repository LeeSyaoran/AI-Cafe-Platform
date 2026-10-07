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

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.companyId = :companyId AND o.cafeId = :cafeId AND o.createdAt >= :startDate")
    long countOrdersInPeriod(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("startDate") Instant startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.companyId = :companyId AND o.cafeId = :cafeId AND o.createdAt >= :startDate AND o.status = 'completed'")
    double sumRevenueInPeriod(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("startDate") Instant startDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.companyId = :companyId AND o.cafeId = :cafeId AND o.createdAt >= :startDate GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("startDate") Instant startDate);

    @Query("SELECT oi.productId, SUM(oi.quantity) as totalQty, SUM(oi.price * oi.quantity) as totalAmount " +
           "FROM OrderItem oi JOIN oi.order o WHERE o.companyId = :companyId AND o.cafeId = :cafeId " +
           "GROUP BY oi.productId ORDER BY totalQty DESC")
    List<Object[]> findTopProducts(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.companyId = :companyId AND o.cafeId = :cafeId AND o.status = :status")
    long countByStatus(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("status") String status);
}
