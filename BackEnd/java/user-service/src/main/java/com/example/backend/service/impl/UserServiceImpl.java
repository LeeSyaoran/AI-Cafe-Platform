package com.example.backend.service.impl;

import com.example.backend.dto.request.ChangePasswordRequest;
import com.example.backend.dto.request.UpdateProfileRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.entity.UserProfile;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(UUID id) {
        return userRepository.findByIdWithProfile(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = getUserEntityById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailWithProfile(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public User createUser(String email, String password, String phone, String fullName, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("EMAIL_EXISTS", "Email already registered: " + email);
        }

        if (phone != null && !phone.isBlank() && userRepository.existsByPhone(phone)) {
            throw new ConflictException("PHONE_EXISTS", "Phone number already registered: " + phone);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .phone(phone)
                .role(role != null ? role : "customer")
                .status("active")
                .build();

        String displayName = (fullName != null && !fullName.isBlank()) ? fullName : extractNameFromEmail(email);
        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(fullName != null ? fullName : "")
                .displayName(displayName)
                .build();

        user.setProfile(profile);
        user = userRepository.save(user);

        log.info("Created new user: {} with role: {}", email, user.getRole());
        return user;
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID id, UpdateProfileRequest request) {
        User user = getUserEntityById(id);
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = UserProfile.builder().user(user).build();
            user.setProfile(profile);
        }

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getLanguage() != null) profile.setLanguage(request.getLanguage());
        if (request.getTimezone() != null) profile.setTimezone(request.getTimezone());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new ConflictException("PHONE_EXISTS", "Phone number already in use: " + request.getPhone());
            }
            user.setPhone(request.getPhone());
        }

        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void updatePassword(UUID id, ChangePasswordRequest request) {
        User user = getUserEntityById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("INVALID_PASSWORD", "Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password updated for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        User user = getUserEntityById(id);
        user.setStatus("inactive");
        userRepository.save(user);
        log.info("User deactivated: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void verifyEmail(UUID id) {
        User user = getUserEntityById(id);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);
    }

    private String extractNameFromEmail(String email) {
        if (email == null) return "User";
        int atIndex = email.indexOf('@');
        return (atIndex > 0) ? email.substring(0, atIndex) : email;
    }
}
