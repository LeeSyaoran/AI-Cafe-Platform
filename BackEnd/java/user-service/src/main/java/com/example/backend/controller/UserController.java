package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.request.UpdateProfileRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.response.UserResponse;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /v1/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@RequestHeader("X-User-ID") UUID userId) {
        User user = userService.getUserByIdWithProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(toUserResponse(user)));
    }

    // PUT /v1/me
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody UpdateProfileRequest request) {
        User user = userService.updateUser(
                userId,
                request.getFullName(),
                request.getDisplayName(),
                request.getPhone(),
                request.getDateOfBirth(),
                request.getGender()
        );
        return ResponseEntity.ok(ApiResponse.ok(toUserResponse(user)));
    }

    // GET /v1/users/:id
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(toUserResponse(user)));
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
