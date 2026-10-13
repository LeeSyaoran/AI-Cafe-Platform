package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts(
            @RequestHeader("X-Company-ID") UUID companyId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String status) {

        List<Product> products = productService.getProducts(companyId, categoryId);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

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
                .status("active")
                .build();

        product = productService.createProduct(product);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted"));
    }

    // ============ Category Endpoints ============

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories(
            @RequestHeader("X-Company-ID") UUID companyId) {
        List<Category> categories = productService.getCategories(companyId);
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

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

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable UUID id) {
        productService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted"));
    }
}
