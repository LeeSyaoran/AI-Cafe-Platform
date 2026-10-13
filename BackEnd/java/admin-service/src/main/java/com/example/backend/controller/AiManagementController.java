package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.dto.request.*;
import com.example.backend.dto.response.*;
import com.example.backend.exception.*;
import com.example.backend.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/ai")
@RequiredArgsConstructor
public class AiManagementController {

    private final AiManagementService aiManagementService;

    @GetMapping("/sessions")
    public ResponseEntity<List<AiSession>> listSessions(
            @RequestParam(required = false) String sessionType,
            @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(aiManagementService.getAllSessions(sessionType, activeOnly));
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<AiSession> getSession(@PathVariable UUID id) {
        return ResponseEntity.ok(aiManagementService.getSessionById(id));
    }

    @GetMapping("/users/{userId}/sessions")
    public ResponseEntity<List<AiSession>> getUserSessions(@PathVariable UUID userId) {
        return ResponseEntity.ok(aiManagementService.getSessionsByUserId(userId));
    }

    @PostMapping("/sessions/{id}/terminate")
    public ResponseEntity<Map<String, String>> terminateSession(@PathVariable UUID id) {
        aiManagementService.terminateSession(id);
        return ResponseEntity.ok(Map.of("message", "AI Session terminated", "sessionId", id.toString()));
    }

    @GetMapping("/usage")
    public ResponseEntity<List<AiUsage>> listUsage(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID sessionId,
            @RequestParam(required = false) String modelId) {
        return ResponseEntity.ok(aiManagementService.getUsageHistory(userId, sessionId, modelId));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AiAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(aiManagementService.getAiAnalytics());
    }
}
