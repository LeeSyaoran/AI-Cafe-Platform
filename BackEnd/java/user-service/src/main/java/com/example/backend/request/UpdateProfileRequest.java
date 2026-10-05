package com.example.backend.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String displayName;
    private String dateOfBirth;
    private String gender;
    private String language;
    private String timezone;
    private String avatarUrl;
    private String bio;
}
