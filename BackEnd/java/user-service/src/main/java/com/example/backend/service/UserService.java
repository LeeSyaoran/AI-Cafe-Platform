package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.entity.UserProfile;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserByIdWithProfile(UUID id) {
        return userRepository.findByIdWithProfile(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    }

    @Transactional
    public User createUser(String email, String password, String phone, String role) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_EXISTS", "Email already registered");
        }

        // Check if phone already exists
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new ApiException(HttpStatus.CONFLICT, "PHONE_EXISTS", "Phone already registered");
        }

        // Create user
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .phone(phone)
                .role(role != null ? role : "customer")
                .status("active")
                .build();

        user = userRepository.save(user);

        // Create profile
        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName("")
                .displayName(extractNameFromEmail(email))
                .build();

        user.setProfile(profile);

        log.info("Created new user: {} with role: {}", email, user.getRole());
        return user;
    }

    @Transactional
    public User updateUser(UUID id, String fullName, String displayName, String phone, String dateOfBirth, String gender) {
        User user = getUserByIdWithProfile(id);

        if (fullName != null) {
            user.getProfile().setFullName(fullName);
        }
        if (displayName != null) {
            user.getProfile().setDisplayName(displayName);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (dateOfBirth != null) {
            user.getProfile().setDateOfBirth(dateOfBirth);
        }
        if (gender != null) {
            user.getProfile().setGender(gender);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(UUID id, String currentPassword, String newPassword) {
        User user = getUserById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password updated for user: {}", user.getEmail());
    }

    @Transactional
    public void deactivateUser(UUID id) {
        User user = getUserById(id);
        user.setStatus("inactive");
        userRepository.save(user);
        log.info("User deactivated: {}", user.getEmail());
    }

    @Transactional
    public void verifyEmail(UUID id) {
        User user = getUserById(id);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);
    }

    @Transactional
    public void verifyPhone(UUID id) {
        User user = getUserById(id);
        user.setPhoneVerifiedAt(Instant.now());
        userRepository.save(user);
    }

    private String extractNameFromEmail(String email) {
        if (email == null) return "User";
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        return "User";
    }
}
