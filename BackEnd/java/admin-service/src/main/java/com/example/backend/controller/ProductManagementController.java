package com.example.backend.controller;

import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.entity.Promotion;
import com.example.backend.entity.Reward;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProductManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
public class ProductManagementController {

    private final ProductManagementService productService;

    // ============ Products ============

    // GET /v1/admin/products
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Product> products = productService.getProducts(companyId, categoryId, status, page, size);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    // GET /v1/admin/products/:id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable UUID id) {
        Product product = productService.getProducts(null, null, null, 0, 1).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    // POST /v1/admin/products
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            @RequestHeader("X-Company-ID") UUID companyId) {

        Product product = Product.builder()
                .companyId(companyId)
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .imageUrl(request.getImageUrl())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .isActive(true)
                .isFeatured(false)
                .isBestSeller(false)
                .status("active")
                .build();

        product = productService.createProduct(product);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    // PUT /v1/admin/products/:id
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest request) {

        Product updates = new Product();
        updates.setName(request.getName());
        updates.setDescription(request.getDescription());
        updates.setPrice(request.getPrice());
        updates.setStock(request.getStock());
        updates.setImageUrl(request.getImageUrl());
        updates.setStatus(request.getStatus());
        updates.setIsFeatured(request.getIsFeatured());
        updates.setIsBestSeller(request.getIsBestSeller());
        updates.setSortOrder(request.getSortOrder());

        Product product = productService.updateProduct(id, updates);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    // DELETE /v1/admin/products/:id
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted"));
    }

    // ============ Categories ============

    // GET /v1/admin/categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories(
            @RequestHeader("X-Company-ID") UUID companyId) {
        List<Category> categories = productService.getCategories(companyId);
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    // POST /v1/admin/categories
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @RequestHeader("X-Company-ID") UUID companyId) {

        Category category = Category.builder()
                .companyId(companyId)
                .name(request.getName())
                .icon(request.getIcon())
                .color(request.getColor())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(true)
                .build();

        category = productService.createCategory(category);
        return ResponseEntity.ok(ApiResponse.ok(category));
    }

    // PUT /v1/admin/categories/:id
    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request) {

        Category updates = new Category();
        updates.setName(request.getName());
        updates.setIcon(request.getIcon());
        updates.setColor(request.getColor());
        updates.setSortOrder(request.getSortOrder());
        updates.setIsActive(request.getIsActive());

        Category category = productService.updateCategory(id, updates);
        return ResponseEntity.ok(ApiResponse.ok(category));
    }

    // DELETE /v1/admin/categories/:id
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable UUID id) {
        productService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted"));
    }

    // ============ Promotions ============

    // GET /v1/admin/promotions
    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<List<Promotion>>> getPromotions() {
        List<Promotion> promotions = productService.getPromotions();
        return ResponseEntity.ok(ApiResponse.ok(promotions));
    }

    // POST /v1/admin/promotions
    @PostMapping("/promotions")
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(
            @Valid @RequestBody CreatePromotionRequest request) {

        Promotion promotion = Promotion.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .status("active")
                .build();

        promotion = productService.createPromotion(promotion);
        return ResponseEntity.ok(ApiResponse.ok(promotion));
    }

    // ============ Rewards ============

    // GET /v1/admin/rewards
    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<List<Reward>>> getRewards() {
        List<Reward> rewards = productService.getRewards();
        return ResponseEntity.ok(ApiResponse.ok(rewards));
    }

    // POST /v1/admin/rewards
    @PostMapping("/rewards")
    public ResponseEntity<ApiResponse<Reward>> createReward(
            @Valid @RequestBody CreateRewardRequest request) {

        Reward reward = Reward.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .rewardType(request.getRewardType())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .pointsCost(request.getPointsCost())
                .validityDays(request.getValidityDays() != null ? request.getValidityDays() : 30)
                .applicableTiers(request.getApplicableTiers())
                .status("active")
                .build();

        reward = productService.createReward(reward);
        return ResponseEntity.ok(ApiResponse.ok(reward));
    }

    // Request DTOs
    @lombok.Data
    public static class CreateProductRequest {
        @jakarta.validation.constraints.NotNull
        private UUID categoryId;
        @jakarta.validation.constraints.NotBlank
        private String name;
        private String description;
        private String shortDescription;
        @jakarta.validation.constraints.NotNull
        private Double price;
        private Double originalPrice;
        private String imageUrl;
        private Integer stock;
    }

    @lombok.Data
    public static class UpdateProductRequest {
        private String name;
        private String description;
        private Double price;
        private Integer stock;
        private String imageUrl;
        private String status;
        private Boolean isFeatured;
        private Boolean isBestSeller;
        private Integer sortOrder;
    }

    @lombok.Data
    public static class CreateCategoryRequest {
        @jakarta.validation.constraints.NotBlank
        private String name;
        private String icon;
        private String color;
        private Integer sortOrder;
    }

    @lombok.Data
    public static class UpdateCategoryRequest {
        private String name;
        private String icon;
        private String color;
        private Integer sortOrder;
        private Boolean isActive;
    }

    @lombok.Data
    public static class CreatePromotionRequest {
        @jakarta.validation.constraints.NotBlank
        private String code;
        @jakarta.validation.constraints.NotBlank
        private String name;
        private String description;
        @jakarta.validation.constraints.NotBlank
        private String discountType;
        @jakarta.validation.constraints.NotNull
        private Double discountValue;
        private Double minOrderAmount;
        private Double maxDiscountAmount;
        private java.time.Instant startDate;
        private java.time.Instant endDate;
        private Integer usageLimit;
    }

    @lombok.Data
    public static class CreateRewardRequest {
        @jakarta.validation.constraints.NotBlank
        private String code;
        @jakarta.validation.constraints.NotBlank
        private String name;
        private String description;
        @jakarta.validation.constraints.NotBlank
        private String rewardType;
        private String discountType;
        private Double discountValue;
        @jakarta.validation.constraints.NotNull
        private Integer pointsCost;
        private Integer validityDays;
        private String applicableTiers;
    }
}
