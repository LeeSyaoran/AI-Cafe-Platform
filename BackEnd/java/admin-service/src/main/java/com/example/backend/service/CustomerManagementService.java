package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;


import java.util.List;
import java.util.UUID;

public interface CustomerManagementService {
    List<Customer> getAllCustomers(String status, UUID loyaltyTierId, UUID cafeId);
    Customer getCustomerByUserId(UUID userId);
    Customer updateCustomer(UUID userId, UpdateCustomerRequest request);
    Customer adjustPoints(UUID userId, AdjustPointsRequest request);
    Customer updateCustomerStatus(UUID userId, String status);

    List<LoyaltyTier> getAllTiers(UUID companyId);
    LoyaltyTier getTierById(UUID id);
    LoyaltyTier createTier(CreateLoyaltyTierRequest request);
    LoyaltyTier updateTier(UUID id, UpdateLoyaltyTierRequest request);
    void deleteTier(UUID id);
}
