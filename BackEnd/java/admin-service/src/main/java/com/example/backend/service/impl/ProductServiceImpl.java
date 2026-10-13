package com.example.backend.service.impl;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService  {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ============ Product CRUD ============

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID productId, Product updates) {
        Product product = findProductOrThrow(productId);

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
        Product product = findProductOrThrow(productId);
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts(UUID companyId, UUID categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        }
        return productRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Transactional(readOnly = true)
    public Product getProduct(UUID productId) {
        return findProductOrThrow(productId);
    }

    private Product findProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    // ============ Category CRUD ============

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(UUID categoryId, Category updates) {
        Category category = findCategoryOrThrow(categoryId);

        if (updates.getName() != null) category.setName(updates.getName());
        if (updates.getIcon() != null) category.setIcon(updates.getIcon());
        if (updates.getColor() != null) category.setColor(updates.getColor());
        if (updates.getSortOrder() != null) category.setSortOrder(updates.getSortOrder());
        if (updates.getIsActive() != null) category.setIsActive(updates.getIsActive());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = findCategoryOrThrow(categoryId);
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories(UUID companyId) {
        return categoryRepository.findByCompanyIdOrderBySortOrder(companyId);
    }

    private Category findCategoryOrThrow(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }
}
