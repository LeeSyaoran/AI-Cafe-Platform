package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.request.*;
import com.example.backend.response.ApiResponse;
import com.example.backend.response.AuthResponse;
import com.example.backend.response.UserResponse;
import com.example.backend.service.AuthService;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // POST /v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var user = userService.createUser(request.getEmail(), request.getPassword(), request.getPhone(), "customer");
        var tokens = authService.login(request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType("Bearer")
                .expiresIn(tokens.expiresIn())
                .user(toUserResponse(user))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    // POST /v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        var tokens = authService.login(request.getEmail(), request.getPassword());
        var user = userService.getUserByEmail(request.getEmail());

        AuthResponse response = AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType("Bearer")
                .expiresIn(tokens.expiresIn())
                .user(toUserResponse(user))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // POST /v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var tokens = authService.refresh(request.getRefreshToken());

        AuthResponse response = AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType("Bearer")
                .expiresIn(tokens.expiresIn())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // POST /v1/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("message", "Logged out successfully")));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .profile(user.getProfile() != null ? UserResponse.ProfileResponse.builder()
                        .fullName(user.getProfile().getFullName())
                        .displayName(user.getProfile().getDisplayName())
                        .avatarUrl(user.getProfile().getAvatarUrl())
                        .dateOfBirth(user.getProfile().getDateOfBirth())
                        .gender(user.getProfile().getGender())
                        .language(user.getProfile().getLanguage())
                        .timezone(user.getProfile().getTimezone())
                        .bio(user.getProfile().getBio())
                        .build() : null)
                .build();
    }
}
