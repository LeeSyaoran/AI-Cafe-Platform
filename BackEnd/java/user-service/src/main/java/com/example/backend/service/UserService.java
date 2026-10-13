package com.example.backend.service;

import com.example.backend.dto.request.ChangePasswordRequest;
import com.example.backend.dto.request.UpdateProfileRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.User;

import java.util.UUID;

public interface UserService {

    User getUserEntityById(UUID id);

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    User createUser(String email, String password, String phone, String fullName, String role);

    UserResponse updateProfile(UUID id, UpdateProfileRequest request);

    void updatePassword(UUID id, ChangePasswordRequest request);

    void deactivateUser(UUID id);

    void verifyEmail(UUID id);
}
