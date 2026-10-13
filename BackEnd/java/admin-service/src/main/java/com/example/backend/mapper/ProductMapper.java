package com.example.backend.mapper;

import com.example.backend.dto.request.CreateCategoryRequest;
import com.example.backend.dto.request.CreateProductRequest;
import com.example.backend.dto.request.UpdateCategoryRequest;
import com.example.backend.dto.request.UpdateProductRequest;
import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request, UUID companyId) {
        if (request == null) return null;

        return Product.builder()
                .companyId(companyId)
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .imageUrl(request.getImageUrl())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .status("active")
                .isActive(true)
                .build();
    }

    public void updateEntity(Product product, UpdateProductRequest request) {
        if (product == null || request == null) return;

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if (request.getIsBestSeller() != null) product.setIsBestSeller(request.getIsBestSeller());
        if (request.getSortOrder() != null) product.setSortOrder(request.getSortOrder());
    }

    public Category toCategoryEntity(CreateCategoryRequest request, UUID companyId) {
        if (request == null) return null;

        return Category.builder()
                .companyId(companyId)
                .name(request.getName())
                .icon(request.getIcon())
                .color(request.getColor())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(true)
                .build();
    }

    public void updateCategoryEntity(Category category, UpdateCategoryRequest request) {
        if (category == null || request == null) return;

        if (request.getName() != null) category.setName(request.getName());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getColor() != null) category.setColor(request.getColor());
        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());
        if (request.getIsActive() != null) category.setIsActive(request.getIsActive());
    }
}
