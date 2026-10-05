package main

import (
	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/pkg/response"
)

type AuthHandler struct {
	authService interface {
		Register(email, password string) error
		Login(email, password string) (string, string, error)
		RefreshToken(refreshToken string) (string, error)
		Logout(token string) error
	}
}

func NewAuthHandler(authService interface{}) *AuthHandler {
	return &AuthHandler{authService: authService}
}

// POST /v1/auth/register
func (h *AuthHandler) Register(c *fiber.Ctx) error {
	var req RegisterRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	if err := h.authService.Register(req.Email, req.Password); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("REGISTRATION_FAILED", err.Error()))
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"message": "Registration successful",
	}))
}

// POST /v1/auth/login
func (h *AuthHandler) Login(c *fiber.Ctx) error {
	var req LoginRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	accessToken, refreshToken, err := h.authService.Login(req.Email, req.Password)
	if err != nil {
		return c.Status(fiber.StatusUnauthorized).JSON(response.Error("LOGIN_FAILED", "Invalid email or password"))
	}

	return c.JSON(response.Success(fiber.Map{
		"accessToken":  accessToken,
		"refreshToken": refreshToken,
		"tokenType":   "Bearer",
	}))
}

// POST /v1/auth/refresh
func (h *AuthHandler) RefreshToken(c *fiber.Ctx) error {
	var req struct {
		RefreshToken string `json:"refreshToken"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	accessToken, err := h.authService.RefreshToken(req.RefreshToken)
	if err != nil {
		return c.Status(fiber.StatusUnauthorized).JSON(response.Error("TOKEN_EXPIRED", "Refresh token expired or invalid"))
	}

	return c.JSON(response.Success(fiber.Map{
		"accessToken": accessToken,
	}))
}

// POST /v1/auth/logout
func (h *AuthHandler) Logout(c *fiber.Ctx) error {
	// Get token from Authorization header
	authHeader := c.Get("Authorization")
	if len(authHeader) > 7 {
		token := authHeader[7:]
		h.authService.Logout(token)
	}
	return c.JSON(response.Success(fiber.Map{
		"message": "Logged out successfully",
	}))
}

// GET /v1/me
func (h *AuthHandler) GetProfile(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(*config.JWTClaims)

	return c.JSON(response.Success(fiber.Map{
		"userId": claims.UserID,
		"email":  claims.Email,
		"role":   claims.Role,
	}))
}

type RegisterRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

type LoginRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}
