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
    private String avatarUrl;
}