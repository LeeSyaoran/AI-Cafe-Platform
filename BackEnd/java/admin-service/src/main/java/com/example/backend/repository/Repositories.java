package com.example.backend.repository;

import com.example.backend.entity.*;
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
    long countOrdersInPeriod(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("startDate") Instant startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.companyId = :companyId AND o.createdAt >= :startDate AND o.status = 'completed'")
    double sumRevenueInPeriod(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("startDate") Instant startDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.companyId = :companyId AND o.createdAt >= :startDate GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("companyId") UUID companyId, @Param("cafeId") UUID cafeId, @Param("startDate") Instant startDate);

    long countByStatus(UUID companyId, UUID cafeId, String status);

    @Query("SELECT oi.productId, p.name, SUM(oi.quantity), SUM(oi.lineTotal) FROM OrderItem oi JOIN oi.order o JOIN Product p ON oi.productId = p.id WHERE o.companyId = :companyId GROUP BY oi.productId, p.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopProducts(UUID companyId, UUID cafeId, int limit);
}

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    long countNewCustomersInPeriod(@Param("companyId") UUID companyId, @Param("startDate") Instant startDate);
}

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByOrderId(UUID orderId);
}

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCompanyIdAndIsActiveTrue(UUID companyId);
    List<Product> findByCategoryIdAndIsActiveTrue(UUID categoryId);
}

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByCompanyIdOrderBySortOrder(UUID companyId);
    Optional<Category> findBySlug(String slug);
}

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Optional<Promotion> findByCode(String code);
}

@Repository
public interface RewardRepository extends JpaRepository<Reward, UUID> {

    Optional<Reward> findByCode(String code);
}
