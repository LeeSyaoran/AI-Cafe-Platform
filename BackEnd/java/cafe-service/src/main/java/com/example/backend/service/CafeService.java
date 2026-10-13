package com.example.backend.service;

import com.example.backend.dto.response.CafeBranchResponse;
import com.example.backend.entity.Cafe;

import java.util.List;
import java.util.UUID;

public interface CafeService {
    List<Cafe> getCafesByCompany(UUID companyId);
    Cafe getCafeById(UUID id);
    Cafe getCafeBySlug(String slug);
    List<CafeBranchResponse> searchCafes(UUID companyId, String province, String search);
}