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