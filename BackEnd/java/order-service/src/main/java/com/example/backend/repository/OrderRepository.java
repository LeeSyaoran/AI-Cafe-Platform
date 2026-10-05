package com.example.backend.repository;

import com.example.backend.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(UUID id);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.cafeId = :cafeId ORDER BY o.createdAt DESC")
    List<Order> findByCafeIdOrderByCreatedAtDesc(UUID cafeId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.companyId = :companyId ORDER BY o.createdAt DESC")
    List<Order> findByCompanyIdOrderByCreatedAtDesc(UUID companyId, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    long countByUserIdAndStatus(UUID userId, String status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.userId = :userId AND o.status = 'completed'")
    Double sumTotalAmountByUserId(UUID userId);
}
