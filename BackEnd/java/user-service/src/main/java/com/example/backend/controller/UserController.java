package com.example.backend.controller;

import com.example.backend.dto.request.ChangePasswordRequest;
import com.example.backend.dto.request.RegisterDeviceRequest;
import com.example.backend.dto.request.UpdateProfileRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.UserDevice;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.response.ApiResponse;
import com.example.backend.security.UserPrincipal;
import com.example.backend.service.DeviceService;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DeviceService deviceService;

    @GetMapping({"/me", "/users/me"})
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-User-ID", required = false) UUID headerUserId) {
        UUID userId = resolveUserId(principal, headerUserId);
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(userId)));
    }

    @PutMapping({"/me", "/users/me"})
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-User-ID", required = false) UUID headerUserId,
            @RequestBody UpdateProfileRequest request) {
        UUID userId = resolveUserId(principal, headerUserId);
        return ResponseEntity.ok(ApiResponse.ok(userService.updateProfile(userId, request)));
    }

    @PutMapping({"/me/password", "/users/me/password"})
    public ResponseEntity<ApiResponse<Map<String, String>>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-User-ID", required = false) UUID headerUserId,
            @Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = resolveUserId(principal, headerUserId);
        userService.updatePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("message", "Password updated successfully")));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    @PostMapping({"/me/devices", "/users/me/devices"})
    public ResponseEntity<ApiResponse<UserDevice>> registerDevice(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-User-ID", required = false) UUID headerUserId,
            @Valid @RequestBody RegisterDeviceRequest request) {
        UUID userId = resolveUserId(principal, headerUserId);
        return ResponseEntity.ok(ApiResponse.ok(deviceService.registerDevice(userId, request)));
    }

    @GetMapping({"/me/devices", "/users/me/devices"})
    public ResponseEntity<ApiResponse<List<UserDevice>>> getDevices(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "X-User-ID", required = false) UUID headerUserId) {
        UUID userId = resolveUserId(principal, headerUserId);
        return ResponseEntity.ok(ApiResponse.ok(deviceService.getUserDevices(userId)));
    }

    private UUID resolveUserId(UserPrincipal principal, UUID headerUserId) {
        if (principal != null && principal.getUserId() != null) {
            return principal.getUserId();
        }
        if (headerUserId != null) {
            return headerUserId;
        }
        throw new UnauthorizedException("UNAUTHORIZED", "Missing authentication credentials");
    }
}
