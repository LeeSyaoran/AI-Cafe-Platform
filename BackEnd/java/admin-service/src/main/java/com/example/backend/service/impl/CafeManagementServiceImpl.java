package com.example.backend.service.impl;

import com.example.backend.dto.request.CreateCafeRequest;
import com.example.backend.dto.request.CreateSeatRequest;
import com.example.backend.dto.request.UpdateCafeRequest;
import com.example.backend.dto.request.UpdateSeatRequest;
import com.example.backend.entity.Cafe;
import com.example.backend.entity.Seat;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.CafeMapper;
import com.example.backend.repository.CafeRepository;
import com.example.backend.repository.SeatRepository;
import com.example.backend.service.CafeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeManagementServiceImpl implements CafeManagementService {

    private final CafeRepository cafeRepository;
    private final SeatRepository seatRepository;
    private final CafeMapper cafeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Cafe> getAllCafes(UUID companyId, String status, String search) {
        if (search != null && !search.isBlank()) {
            return cafeRepository.findByNameContainingIgnoreCase(search.trim());
        }
        if (companyId != null) {
            return cafeRepository.findByCompanyId(companyId);
        }
        if (status != null && !status.isBlank()) {
            return cafeRepository.findByStatus(status);
        }
        return cafeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cafe getCafeById(UUID id) {
        return cafeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cafe not found with ID: " + id));
    }

    @Override
    public Cafe createCafe(CreateCafeRequest request) {
        Cafe cafe = cafeMapper.toEntity(request);
        if (cafeRepository.existsBySlug(cafe.getSlug())) {
            cafe.setSlug(cafe.getSlug() + "-" + UUID.randomUUID().toString().substring(0, 6));
        }
        return cafeRepository.save(cafe);
    }

    @Override
    public Cafe updateCafe(UUID id, UpdateCafeRequest request) {
        Cafe cafe = getCafeById(id);
        cafeMapper.updateEntity(cafe, request);
        return cafeRepository.save(cafe);
    }

    @Override
    public void deleteCafe(UUID id) {
        if (!cafeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cafe not found with ID: " + id);
        }
        cafeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getSeatsByCafe(UUID cafeId, String zone, String status) {
        if (zone != null && !zone.isBlank()) {
            return seatRepository.findByCafeIdAndZone(cafeId, zone);
        }
        if (status != null && !status.isBlank()) {
            return seatRepository.findByCafeIdAndStatus(cafeId, status);
        }
        return seatRepository.findByCafeId(cafeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Seat getSeatById(UUID seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));
    }

    @Override
    public Seat createSeat(CreateSeatRequest request) {
        Seat seat = cafeMapper.toSeatEntity(request);
        return seatRepository.save(seat);
    }

    @Override
    public Seat updateSeat(UUID seatId, UpdateSeatRequest request) {
        Seat seat = getSeatById(seatId);
        cafeMapper.updateSeatEntity(seat, request);
        return seatRepository.save(seat);
    }

    @Override
    public Seat updateSeatStatus(UUID seatId, String status) {
        Seat seat = getSeatById(seatId);
        seat.setStatus(status);
        seat.setIsAvailable("available".equalsIgnoreCase(status));
        return seatRepository.save(seat);
    }

    @Override
    public void deleteSeat(UUID seatId) {
        if (!seatRepository.existsById(seatId)) {
            throw new ResourceNotFoundException("Seat not found with ID: " + seatId);
        }
        seatRepository.deleteById(seatId);
    }
}
