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

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, int page, int size);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Order> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);

    List<Order> findByCafeIdOrderByCreatedAtDesc(UUID cafeId);

    List<Order> findByStatusOrderByCreatedAtAsc(String status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    long countByUserIdAndStatus(UUID userId, String status);
}

package com.example.backend.repository;

import com.example.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserIdAndCafeId(UUID userId, UUID cafeId);

    void deleteByUserIdAndCafeId(UUID userId, UUID cafeId);
}

package com.example.backend.repository;

import com.example.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCartId(UUID cartId);

    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    void deleteAllByCartId(UUID cartId);

    int countByCartId(UUID cartId);
}

package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByCompanyIdAndStatusOrderBySortOrderAsc(UUID companyId, String status);

    List<Product> findByCategoryIdAndIsActiveTrueOrderBySortOrderAsc(UUID categoryId);

    @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.isActive = true ORDER BY p.isBestSeller DESC, p.sortOrder ASC")
    List<Product> findFeaturedProducts(UUID companyId, Pageable pageable);

    List<Product> findByCompanyIdAndIsActiveTrue(UUID companyId);
}

package com.example.backend.repository;

import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByCompanyIdAndIsActiveTrueOrderBySortOrderAsc(UUID companyId);

    List<Category> findByParentId(UUID parentId);
}

package com.example.backend.repository;

import com.example.backend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Optional<Promotion> findByCode(String code);

    boolean existsByCode(String code);
}

package com.example.backend.repository;

import com.example.backend.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, UUID> {

    Optional<Cafe> findBySlug(String slug);

    List<Cafe> findByCompanyIdAndIsActiveTrue(UUID companyId);

    List<Cafe> findByStatus(String status);
}

package com.example.backend.repository;

import com.example.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, java.util.UUID> {

    Optional<Company> findBySlug(String slug);
}