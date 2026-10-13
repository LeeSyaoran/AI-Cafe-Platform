package com.example.backend.service.impl;

import com.example.backend.entity.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts(UUID companyId) {
        return productRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProduct(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductByIdAndCompany(UUID productId, UUID companyId) {
        return productRepository.findByIdAndCompanyId(productId, companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }
}
