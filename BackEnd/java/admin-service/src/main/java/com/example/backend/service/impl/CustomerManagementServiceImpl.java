package com.example.backend.service.impl;

import com.example.backend.dto.request.AdjustPointsRequest;
import com.example.backend.dto.request.CreateLoyaltyTierRequest;
import com.example.backend.dto.request.UpdateCustomerRequest;
import com.example.backend.dto.request.UpdateLoyaltyTierRequest;
import com.example.backend.entity.Customer;
import com.example.backend.entity.LoyaltyTier;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.CustomerMapper;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.LoyaltyTierRepository;
import com.example.backend.service.CustomerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerManagementServiceImpl implements CustomerManagementService {

    private final CustomerRepository customerRepository;
    private final LoyaltyTierRepository loyaltyTierRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers(String status, UUID loyaltyTierId, UUID cafeId) {
        if (status != null && !status.isBlank()) {
            return customerRepository.findByStatus(status);
        }
        if (loyaltyTierId != null) {
            return customerRepository.findByLoyaltyTierId(loyaltyTierId);
        }
        if (cafeId != null) {
            return customerRepository.findByPreferredCafeId(cafeId);
        }
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByUserId(UUID userId) {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user ID: " + userId));
    }

    @Override
    public Customer updateCustomer(UUID userId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(userId)
                .orElseGet(() -> Customer.builder()
                        .userId(userId)
                        .loyaltyPoints(0L)
                        .lifetimePoints(0L)
                        .totalOrders(0)
                        .totalSpent(BigDecimal.ZERO)
                        .avgOrderValue(BigDecimal.ZERO)
                        .visitCount(0)
                        .status("active")
                        .build());

        customerMapper.updateCustomerEntity(customer, request);
        return customerRepository.save(customer);
    }

    @Override
    public Customer adjustPoints(UUID userId, AdjustPointsRequest request) {
        Customer customer = customerRepository.findById(userId)
                .orElseGet(() -> Customer.builder()
                        .userId(userId)
                        .loyaltyPoints(0L)
                        .lifetimePoints(0L)
                        .totalOrders(0)
                        .totalSpent(BigDecimal.ZERO)
                        .avgOrderValue(BigDecimal.ZERO)
                        .visitCount(0)
                        .status("active")
                        .build());

        long currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0L;
        long newPoints = Math.max(0L, currentPoints + request.getPointsDelta());
        customer.setLoyaltyPoints(newPoints);

        if (request.getPointsDelta() > 0) {
            long currentLifetime = customer.getLifetimePoints() != null ? customer.getLifetimePoints() : 0L;
            customer.setLifetimePoints(currentLifetime + request.getPointsDelta());
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomerStatus(UUID userId, String status) {
        Customer customer = getCustomerByUserId(userId);
        customer.setStatus(status);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyTier> getAllTiers(UUID companyId) {
        if (companyId != null) {
            return loyaltyTierRepository.findByCompanyIdOrderBySortOrderAsc(companyId);
        }
        return loyaltyTierRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public LoyaltyTier getTierById(UUID id) {
        return loyaltyTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty tier not found with ID: " + id));
    }

    @Override
    public LoyaltyTier createTier(CreateLoyaltyTierRequest request) {
        LoyaltyTier tier = customerMapper.toTierEntity(request);
        return loyaltyTierRepository.save(tier);
    }

    @Override
    public LoyaltyTier updateTier(UUID id, UpdateLoyaltyTierRequest request) {
        LoyaltyTier tier = getTierById(id);
        customerMapper.updateTierEntity(tier, request);
        return loyaltyTierRepository.save(tier);
    }

    @Override
    public void deleteTier(UUID id) {
        if (!loyaltyTierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Loyalty tier not found with ID: " + id);
        }
        loyaltyTierRepository.deleteById(id);
    }
}
