package com.example.backend.controller;

import com.example.backend.entity.Product;
import com.example.backend.entity.Category;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Product>> getProductBySlug(@PathVariable String slug) {
        Product product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable UUID categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeaturedProducts(
            @RequestParam UUID companyId,
            @RequestParam(defaultValue = "20") int limit) {
        List<Product> products = productService.getFeaturedProducts(companyId, limit);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCompany(@RequestParam UUID companyId) {
        List<Product> products = productService.getProductsByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }
}

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
class CategoryController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getCategories(@RequestParam UUID companyId) {
        List<Category> categories = productService.getCategories(companyId);
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategory(@PathVariable UUID id) {
        Category category = productService.getCategory(id);
        return ResponseEntity.ok(ApiResponse.ok(category));
    }
}