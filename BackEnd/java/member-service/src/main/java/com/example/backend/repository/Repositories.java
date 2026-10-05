package com.example.backend.repository;

import com.example.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByUserId(UUID userId);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.plan WHERE s.userId = :userId")
    Optional<Subscription> findByUserIdWithPlan(UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'active' AND s.endDate < :now")
    List<Subscription> findExpiredSubscriptions(Instant now);
}

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {

    List<MembershipPlan> findByStatusOrderBySortOrder(String status);
}

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

@Repository
public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, UUID> {

    Optional<LoyaltyAccount> findByUserId(UUID userId);
}

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {

    List<LoyaltyTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId, int limit);
}

@Repository
public interface RewardRepository extends JpaRepository<Reward, UUID> {

    Optional<Reward> findByCode(String code);

    @Query("SELECT r FROM Reward r WHERE r.status = 'active' AND (r.startDate IS NULL OR r.startDate <= :now) AND (r.endDate IS NULL OR r.endDate >= :now)")
    List<Reward> findActiveRewards(Instant now);
}

@Repository
public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, UUID> {

    List<RewardRedemption> findByUserId(UUID userId);

    List<RewardRedemption> findByUserIdAndStatus(UUID userId, String status);
}

@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, UUID> {

    Optional<UserCredit> findByUserId(UUID userId);
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
