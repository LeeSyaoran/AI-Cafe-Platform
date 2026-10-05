package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductManagementService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionRepository promotionRepository;
    private final RewardRepository rewardRepository;

    // ============ Product Management ============

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID productId, Product updates) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found"));

        if (updates.getName() != null) product.setName(updates.getName());
        if (updates.getDescription() != null) product.setDescription(updates.getDescription());
        if (updates.getPrice() != null) product.setPrice(updates.getPrice());
        if (updates.getStock() != null) product.setStock(updates.getStock());
        if (updates.getImageUrl() != null) product.setImageUrl(updates.getImageUrl());
        if (updates.getStatus() != null) product.setStatus(updates.getStatus());
        if (updates.getIsFeatured() != null) product.setIsFeatured(updates.getIsFeatured());
        if (updates.getIsBestSeller() != null) product.setIsBestSeller(updates.getIsBestSeller());
        if (updates.getSortOrder() != null) product.setSortOrder(updates.getSortOrder());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found"));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts(UUID companyId, UUID categoryId, String status, int page, int size) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        }
        return productRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    // ============ Category Management ============

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(UUID categoryId, Category updates) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Category not found"));

        if (updates.getName() != null) category.setName(updates.getName());
        if (updates.getIcon() != null) category.setIcon(updates.getIcon());
        if (updates.getColor() != null) category.setColor(updates.getColor());
        if (updates.getSortOrder() != null) category.setSortOrder(updates.getSortOrder());
        if (updates.getIsActive() != null) category.setIsActive(updates.getIsActive());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Category not found"));
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories(UUID companyId) {
        return categoryRepository.findByCompanyIdOrderBySortOrder(companyId);
    }

    // ============ Promotion Management ============

    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion updatePromotion(UUID promotionId, Promotion updates) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Promotion not found"));

        if (updates.getName() != null) promotion.setName(updates.getName());
        if (updates.getDiscountType() != null) promotion.setDiscountType(updates.getDiscountType());
        if (updates.getDiscountValue() != null) promotion.setDiscountValue(updates.getDiscountValue());
        if (updates.getMinOrderAmount() != null) promotion.setMinOrderAmount(updates.getMinOrderAmount());
        if (updates.getMaxDiscountAmount() != null) promotion.setMaxDiscountAmount(updates.getMaxDiscountAmount());
        if (updates.getUsageLimit() != null) promotion.setUsageLimit(updates.getUsageLimit());
        if (updates.getStatus() != null) promotion.setStatus(updates.getStatus());

        return promotionRepository.save(promotion);
    }

    @Transactional
    public void deletePromotion(UUID promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Promotion not found"));
        promotion.setStatus("inactive");
        promotionRepository.save(promotion);
    }

    @Transactional(readOnly = true)
    public List<Promotion> getPromotions() {
        return promotionRepository.findAll();
    }

    // ============ Reward Management ============

    @Transactional
    public Reward createReward(Reward reward) {
        if (reward.getQuantityRemaining() == null) {
            reward.setQuantityRemaining(reward.getQuantity());
        }
        return rewardRepository.save(reward);
    }

    @Transactional
    public Reward updateReward(UUID rewardId, Reward updates) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reward not found"));

        if (updates.getName() != null) reward.setName(updates.getName());
        if (updates.getDiscountType() != null) reward.setDiscountType(updates.getDiscountType());
        if (updates.getDiscountValue() != null) reward.setDiscountValue(updates.getDiscountValue());
        if (updates.getPointsCost() != null) reward.setPointsCost(updates.getPointsCost());
        if (updates.getQuantity() != null) reward.setQuantity(updates.getQuantity());
        if (updates.getStatus() != null) reward.setStatus(updates.getStatus());

        return rewardRepository.save(reward);
    }

    @Transactional(readOnly = true)
    public List<Reward> getRewards() {
        return rewardRepository.findAll();
    }
}
