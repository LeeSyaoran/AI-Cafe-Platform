package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.request.AuthRequests.LoginRequest;
import com.example.backend.request.AuthRequests.RegisterRequest;
import com.example.backend.request.AuthRequests.RefreshTokenRequest;
import com.example.backend.response.AuthResponses.TokenResponse;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        TokenResponse tokens = authService.register(
                request.getEmail(), request.getPassword(), request.getFullName(),
                request.getPhone(), "CUSTOMER");
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokens = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestHeader("X-User-ID") String userId) {
        User user = authService.getCurrentUser(java.util.UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("X-User-ID") String userId) {
        authService.logout(java.util.UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

package com.example.backend.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank @Email private String email;
    @NotBlank private String password;
}

@Data
class RegisterRequest {
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    private String fullName;
    private String phone;
}

@Data
class RefreshTokenRequest {
    @NotBlank private String refreshToken;
}

@Data
class UpdateProfileRequest {
    private String fullName;
    private String displayName;
    private String avatarUrl;
    private String dateOfBirth;
    private String gender;
    private String language;
    private String timezone;
    private String bio;
}

@Data
class ChangePasswordRequest {
    @NotBlank private String currentPassword;
    @NotBlank private String newPassword;
}

@Data
class ForgotPasswordRequest {
    @NotBlank @Email private String email;
}

@Data
class ResetPasswordRequest {
    @NotBlank private String token;
    @NotBlank @Size(min = 6) private String newPassword;
}

package com.example.backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserInfo {
    private String id;
    private String email;
    private String fullName;
    private String role;
}