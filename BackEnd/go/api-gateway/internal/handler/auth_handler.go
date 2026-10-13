package handler

import (
	"errors"

	"github.com/gofiber/fiber/v2"

	"aicafe-api-gateway/internal/dto"
	apperrors "aicafe-api-gateway/internal/errors"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"
)

// AuthHandler handles authentication endpoints
type AuthHandler struct {
	authService *service.AuthService
}

func NewAuthHandler(authService *service.AuthService) *AuthHandler {
	return &AuthHandler{authService: authService}
}

// SendOTP sends OTP to phone number
// POST /v1/auth/send-otp
func (h *AuthHandler) SendOTP(c *fiber.Ctx) error {
	var req dto.SendOTPRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	if req.Phone == "" {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("PHONE_REQUIRED", "Phone number is required"))
	}

	result, err := h.authService.SendOTP(c.Context(), req.Phone)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// VerifyOTP verifies OTP and returns tokens
// POST /v1/auth/verify-otp
func (h *AuthHandler) VerifyOTP(c *fiber.Ctx) error {
	var req dto.VerifyOTPRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.authService.VerifyOTP(c.Context(), req.Phone, req.Code)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// Register creates a new user account
// POST /v1/auth/register
func (h *AuthHandler) Register(c *fiber.Ctx) error {
	var req dto.RegisterRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.authService.Register(c.Context(), &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(result))
}

// Login authenticates user with email/password
// POST /v1/auth/login
func (h *AuthHandler) Login(c *fiber.Ctx) error {
	var req dto.LoginRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.authService.Login(c.Context(), &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// RefreshToken refreshes access token
// POST /v1/auth/refresh
func (h *AuthHandler) RefreshToken(c *fiber.Ctx) error {
	var req dto.RefreshTokenRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.authService.RefreshToken(c.Context(), req.RefreshToken)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// Logout invalidates the session
// POST /v1/auth/logout
func (h *AuthHandler) Logout(c *fiber.Ctx) error {
	// Get token ID from context if available
	tokenID := ""
	if claims := c.Locals("claims"); claims != nil {
		if tc, ok := claims.(*service.TokenClaims); ok {
			tokenID = tc.TokenID
		}
	}

	// Add token to blacklist
	_ = h.authService.Logout(c.Context(), tokenID, 0) // Token will be blacklisted until expiry

	return c.JSON(response.Success(fiber.Map{
		"message": "Logged out successfully",
	}))
}

// GetProfile returns current user profile
// GET /v1/me
func (h *AuthHandler) GetProfile(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)

	result, err := h.authService.GetProfile(c.Context(), userID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// UpdateProfile updates current user profile
// PUT /v1/me
func (h *AuthHandler) UpdateProfile(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)

	var req dto.UpdateProfileRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.authService.UpdateProfile(c.Context(), userID, &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// handleError converts service errors to HTTP responses
func (h *AuthHandler) handleError(c *fiber.Ctx, err error) error {
	// Check for known error types
	switch {
	case errors.Is(err, apperrors.ErrOTPNotFound):
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("OTP_NOT_FOUND", "No OTP found. Please request a new one"))

	case errors.Is(err, apperrors.ErrOTPInvalid):
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("OTP_INVALID", "Invalid OTP code"))

	case errors.Is(err, apperrors.ErrOTPAttemptsExceeded):
		return c.Status(fiber.StatusTooManyRequests).JSON(response.Error("OTP_LOCKED", "Too many failed attempts. Please try again later"))

	case errors.Is(err, apperrors.ErrUserExists):
		return c.Status(fiber.StatusConflict).JSON(response.Error("USER_EXISTS", "User with this email already exists"))

	case errors.Is(err, apperrors.ErrInvalidCredentials):
		return c.Status(fiber.StatusUnauthorized).JSON(response.Error("INVALID_CREDENTIALS", "Invalid email or password"))

	case errors.Is(err, apperrors.ErrUserNotFound):
		return c.Status(fiber.StatusNotFound).JSON(response.Error("USER_NOT_FOUND", "User not found"))

	case errors.Is(err, apperrors.ErrTokenInvalid), errors.Is(err, apperrors.ErrTokenExpired):
		return c.Status(fiber.StatusUnauthorized).JSON(response.Error("TOKEN_INVALID", "Invalid or expired token"))

	case errors.Is(err, apperrors.ErrTokenBlacklisted):
		return c.Status(fiber.StatusUnauthorized).JSON(response.Error("TOKEN_REVOKED", "Token has been revoked"))

	default:
		// Check if it's a ServiceError
		if se, ok := err.(*apperrors.ServiceError); ok {
			return c.Status(fiber.StatusBadRequest).JSON(response.Error(se.Code, se.Message))
		}
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("INTERNAL_ERROR", "An unexpected error occurred"))
	}
}
