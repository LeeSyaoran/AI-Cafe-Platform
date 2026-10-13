package com.example.backend.service;

import com.example.backend.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    List<Product> getActiveProducts(UUID companyId);

    Optional<Product> getProduct(UUID productId);

    Optional<Product> getProductByIdAndCompany(UUID productId, UUID companyId);

    List<Product> getProductsByCategory(UUID categoryId);
}
