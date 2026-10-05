package handler

import (
	"context"
	"fmt"
	"log"
	"math/rand"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"

	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/internal/repository"
	"aicafe-api-gateway/pkg/response"
)

// OTP Store (in-memory for demo, use Redis in production)
var otpStore = make(map[string]*OTPEntry)

type OTPEntry struct {
	Code      string
	ExpiresAt time.Time
	Attempts  int
	Verified  bool
}

type AuthHandler struct {
	repo *repository.PostgresRepo
	jwt  config.JWTConfig
}

func NewAuthHandler(repo *repository.PostgresRepo, jwt config.JWTConfig) *AuthHandler {
	return &AuthHandler{repo: repo, jwt: jwt}
}

// POST /v1/auth/send-otp
func (h *AuthHandler) SendOTP(c *fiber.Ctx) error {
	var req struct {
		Phone string `json:"phone"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	if req.Phone == "" {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("PHONE_REQUIRED", "Phone number is required"))
	}

	// Generate 6-digit OTP
	otp := generateOTP()
	otpStore[req.Phone] = &OTPEntry{
		Code:      otp,
		ExpiresAt: time.Now().Add(5 * time.Minute),
		Attempts:  0,
		Verified:  false,
	}

	// Debug: Print OTP to console (REMOVE IN PRODUCTION!)
	fmt.Printf("[DEBUG] OTP for %s: %s\n", req.Phone, otp)

	return c.JSON(response.Success(fiber.Map{
		"message":            "OTP sent successfully",
		"expires_in":         300,
		"resend_available_in": 60,
		"_debug_otp":         otp, // DEBUG: Remove in production!
	}))
}

// POST /v1/auth/verify-otp
func (h *AuthHandler) VerifyOTP(c *fiber.Ctx) error {
	var req struct {
		Phone string `json:"phone"`
		Code  string `json:"code"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	entry, exists := otpStore[req.Phone]
	if !exists {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("OTP_NOT_FOUND", "No OTP found for this phone"))
	}

	if time.Now().After(entry.ExpiresAt) {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("OTP_EXPIRED", "OTP has expired"))
	}

	if entry.Code != req.Code {
		entry.Attempts++
		if entry.Attempts >= 3 {
			delete(otpStore, req.Phone)
			return c.Status(fiber.StatusBadRequest).JSON(response.Error("OTP_LOCKED", "Too many attempts. Please request a new OTP"))
		}
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("OTP_INVALID", "Invalid OTP code"))
	}

	entry.Verified = true

	// Find or create user
	user, err := h.repo.FindOrCreateUserByPhone(c.Context(), req.Phone)
	if err != nil {
		log.Printf("[ERROR] FindOrCreateUserByPhone failed for %s: %v", req.Phone, err)
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("USER_ERROR", "Failed to process user: "+err.Error()))
	}

	// Generate JWT tokens
	accessToken, err := h.generateAccessToken(user)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("TOKEN_ERROR", "Failed to generate token"))
	}

	refreshToken, err := h.generateRefreshToken(user)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("TOKEN_ERROR", "Failed to generate refresh token"))
	}

	// Clean up OTP
	delete(otpStore, req.Phone)

	return c.JSON(response.Success(fiber.Map{
		"tokens": fiber.Map{
			"access_token":  accessToken,
			"refresh_token": refreshToken,
			"expires_in":    h.jwt.AccessTokenExpiration,
			"token_type":    "Bearer",
		},
		"user": fiber.Map{
			"id":    user.ID,
			"phone": user.Phone,
			"name":  user.Name,
			"email": user.Email,
			"role":  user.Role,
			"tier":  user.Tier,
		},
	}))
}

// POST /v1/auth/refresh
func (h *AuthHandler) RefreshToken(c *fiber.Ctx) error {
	var req struct {
		RefreshToken string `json:"refresh_token"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	token, err := jwt.Parse(req.RefreshToken, func(token *jwt.Token) (interface{}, error) {
		return []byte(h.jwt.Secret), nil
	})

	if err != nil || !token.Valid {
		return c.Status(fiber.StatusUnauthorized).JSON(response.Error("TOKEN_EXPIRED", "Refresh token expired or invalid"))
	}

	claims := token.Claims.(jwt.MapClaims)
	userID := claims["user_id"].(string)

	// Generate new access token
	accessToken, err := h.generateAccessTokenFromID(c.Context(), userID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("TOKEN_ERROR", "Failed to generate token"))
	}

	return c.JSON(response.Success(fiber.Map{
		"access_token": accessToken,
		"token_type":  "Bearer",
	}))
}

// POST /v1/auth/logout
func (h *AuthHandler) Logout(c *fiber.Ctx) error {
	return c.JSON(response.Success(fiber.Map{
		"message": "Logged out successfully",
	}))
}

// GET /v1/me
func (h *AuthHandler) GetProfile(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	userID := claims["user_id"].(string)

	userData, err := h.repo.GetUserByIDString(c.Context(), userID)
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(response.Error("USER_NOT_FOUND", "User not found"))
	}

	return c.JSON(response.Success(userData))
}

// PUT /v1/me
func (h *AuthHandler) UpdateProfile(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	userID := claims["user_id"].(string)

	var req struct {
		Name  string `json:"name"`
		Email string `json:"email"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	err := h.repo.UpdateUser(c.Context(), userID, req.Name, req.Email)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("UPDATE_FAILED", "Failed to update profile"))
	}

	return c.JSON(response.Success(fiber.Map{
		"message": "Profile updated successfully",
	}))
}

// Helper functions
func generateOTP() string {
	rand.Seed(time.Now().UnixNano())
	return fmt.Sprintf("%06d", rand.Intn(1000000))
}

func (h *AuthHandler) generateAccessToken(user *repository.User) (string, error) {
	claims := config.JWTClaims{
		UserID: user.ID,
		Email:  user.Email,
		Role:   user.Role,
		Tier:   user.Tier,
		RegisteredClaims: jwt.RegisteredClaims{
			Issuer:    h.jwt.Issuer,
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(time.Duration(h.jwt.AccessTokenExpiration) * time.Second)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, &claims)
	return token.SignedString(h.jwt.GetSigningKey())
}

func (h *AuthHandler) generateRefreshToken(user *repository.User) (string, error) {
	claims := jwt.MapClaims{
		"user_id": user.ID,
		"type":    "refresh",
		"exp":     time.Now().Add(time.Duration(h.jwt.RefreshTokenExpiration) * time.Second).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(h.jwt.Secret))
}

func (h *AuthHandler) generateAccessTokenFromID(ctx context.Context, userID string) (string, error) {
	user, err := h.repo.GetUserByIDString(ctx, userID)
	if err != nil {
		return "", err
	}
	return h.generateAccessToken(user)
}
