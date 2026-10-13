package com.example.backend.service.impl;

import com.example.backend.dto.response.CafeBranchResponse;
import com.example.backend.entity.Cafe;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.CafeMapper;
import com.example.backend.repository.CafeRepository;
import com.example.backend.repository.SeatRepository;
import com.example.backend.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final SeatRepository seatRepository;
    private final CafeMapper cafeMapper;

    @Override
    public List<Cafe> getCafesByCompany(UUID companyId) {
        return cafeRepository.findByCompanyId(companyId);
    }

    @Override
    public Cafe getCafeById(UUID id) {
        return cafeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cafe not found with ID: " + id));
    }

    @Override
    public Cafe getCafeBySlug(String slug) {
        return cafeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Cafe not found with slug: " + slug));
    }

    @Override
    public List<CafeBranchResponse> searchCafes(UUID companyId, String province, String search) {
        List<Cafe> cafes;
        if (search != null && !search.isBlank()) {
            cafes = cafeRepository.findByNameContainingIgnoreCase(search.trim());
        } else if (province != null && !province.isBlank()) {
            cafes = cafeRepository.findByProvince(province);
        } else if (companyId != null) {
            cafes = cafeRepository.findByCompanyId(companyId);
        } else {
            cafes = cafeRepository.findAll();
        }

        return cafes.stream().map(cafe -> {
            long totalSeats = seatRepository.findByCafeId(cafe.getId()).size();
            long availableSeats = seatRepository.countAvailableSeats(cafe.getId());
            return cafeMapper.toBranchResponse(cafe, totalSeats, availableSeats);
        }).collect(Collectors.toList());
    }
}
