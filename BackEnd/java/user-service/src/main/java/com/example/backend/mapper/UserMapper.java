package com.example.backend.mapper;

import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserProfile profile = user.getProfile();
        UserResponse.ProfileResponse profileResponse = null;

        if (profile != null) {
            profileResponse = UserResponse.ProfileResponse.builder()
                    .fullName(profile.getFullName())
                    .displayName(profile.getDisplayName())
                    .avatarUrl(profile.getAvatarUrl())
                    .dateOfBirth(profile.getDateOfBirth())
                    .gender(profile.getGender())
                    .language(profile.getLanguage())
                    .timezone(profile.getTimezone())
                    .bio(profile.getBio())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .profile(profileResponse)
                .build();
    }
}
