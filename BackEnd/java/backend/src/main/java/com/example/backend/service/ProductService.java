package com.example.backend.service;

import com.example.backend.entity.Product;
import com.example.backend.entity.Category;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Product getProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));
    }

    @Transactional(readOnly = true)
    public Product getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrueOrderBySortOrderAsc(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts(UUID companyId, int limit) {
        return productRepository.findFeaturedProducts(companyId, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCompany(UUID companyId) {
        return productRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories(UUID companyId) {
        return categoryRepository.findByCompanyIdAndIsActiveTrueOrderBySortOrderAsc(companyId);
    }

    @Transactional(readOnly = true)
    public Category getCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "Category not found"));
    }
}