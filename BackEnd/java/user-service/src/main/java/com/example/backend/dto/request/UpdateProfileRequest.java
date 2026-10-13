package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String displayName;
    private String phone;
    private String dateOfBirth;
    private String gender;
    private String language;
    private String timezone;
    private String avatarUrl;
    private String bio;
}
