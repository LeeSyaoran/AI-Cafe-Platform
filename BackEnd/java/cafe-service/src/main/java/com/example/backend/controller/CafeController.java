package com.example.backend.controller;

import com.example.backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/cafes")
@RequiredArgsConstructor
public class CafeController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cafe>>> getCafes(@RequestParam UUID companyId) {
        return ResponseEntity.ok(ApiResponse.ok(List.of()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cafe>> getCafe(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(new Cafe()));
    }
}

class Cafe {
    public UUID id;
    public String name;
    public String slug;
    public String address;
}