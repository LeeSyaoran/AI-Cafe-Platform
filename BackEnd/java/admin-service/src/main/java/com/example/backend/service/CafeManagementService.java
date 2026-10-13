package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;

import com.example.backend.entity.Cafe;

import java.util.List;
import java.util.UUID;

public interface CafeManagementService {
    List<Cafe> getAllCafes(UUID companyId, String status, String search);
    Cafe getCafeById(UUID id);
    Cafe createCafe(CreateCafeRequest request);
    Cafe updateCafe(UUID id, UpdateCafeRequest request);
    void deleteCafe(UUID id);

    List<Seat> getSeatsByCafe(UUID cafeId, String zone, String status);
    Seat getSeatById(UUID seatId);
    Seat createSeat(CreateSeatRequest request);
    Seat updateSeat(UUID seatId, UpdateSeatRequest request);
    Seat updateSeatStatus(UUID seatId, String status);
    void deleteSeat(UUID seatId);
}
