package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product createProduct(Product product);

    Product updateProduct(UUID productId, Product updates);

    void deleteProduct(UUID productId);

    List<Product> getProducts(UUID companyId, UUID categoryId);

    Product getProduct(UUID productId);

    Category createCategory(Category category);

    Category updateCategory(UUID categoryId, Category updates);

    void deleteCategory(UUID categoryId);

    List<Category> getCategories(UUID companyId);
}
