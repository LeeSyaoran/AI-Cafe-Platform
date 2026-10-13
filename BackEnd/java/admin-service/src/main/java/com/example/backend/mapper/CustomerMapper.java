package com.example.backend.mapper;

import com.example.backend.dto.request.CreateLoyaltyTierRequest;
import com.example.backend.dto.request.UpdateCustomerRequest;
import com.example.backend.dto.request.UpdateLoyaltyTierRequest;
import com.example.backend.entity.Customer;
import com.example.backend.entity.LoyaltyTier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CustomerMapper {

    public LoyaltyTier toTierEntity(CreateLoyaltyTierRequest request) {
        if (request == null) return null;

        return LoyaltyTier.builder()
                .companyId(request.getCompanyId())
                .name(request.getName())
                .nameVi(request.getNameVi())
                .minPoints(request.getMinPoints() != null ? request.getMinPoints() : 0)
                .maxPoints(request.getMaxPoints())
                .discountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : BigDecimal.ZERO)
                .pointsMultiplier(request.getPointsMultiplier() != null ? request.getPointsMultiplier() : BigDecimal.ONE)
                .color(request.getColor())
                .icon(request.getIcon())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
    }

    public void updateTierEntity(LoyaltyTier tier, UpdateLoyaltyTierRequest request) {
        if (tier == null || request == null) return;

        if (request.getName() != null) tier.setName(request.getName());
        if (request.getNameVi() != null) tier.setNameVi(request.getNameVi());
        if (request.getMinPoints() != null) tier.setMinPoints(request.getMinPoints());
        if (request.getMaxPoints() != null) tier.setMaxPoints(request.getMaxPoints());
        if (request.getDiscountPercent() != null) tier.setDiscountPercent(request.getDiscountPercent());
        if (request.getPointsMultiplier() != null) tier.setPointsMultiplier(request.getPointsMultiplier());
        if (request.getColor() != null) tier.setColor(request.getColor());
        if (request.getIcon() != null) tier.setIcon(request.getIcon());
        if (request.getSortOrder() != null) tier.setSortOrder(request.getSortOrder());
    }

    public void updateCustomerEntity(Customer customer, UpdateCustomerRequest request) {
        if (customer == null || request == null) return;

        if (request.getLoyaltyTierId() != null) customer.setLoyaltyTierId(request.getLoyaltyTierId());
        if (request.getPreferredPayment() != null) customer.setPreferredPayment(request.getPreferredPayment());
        if (request.getPreferredCafeId() != null) customer.setPreferredCafeId(request.getPreferredCafeId());
        if (request.getNotes() != null) customer.setNotes(request.getNotes());
        if (request.getStatus() != null) customer.setStatus(request.getStatus());
    }
}
