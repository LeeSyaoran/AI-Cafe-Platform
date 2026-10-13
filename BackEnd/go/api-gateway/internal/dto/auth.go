package dto

import "time"

// ============ AUTH REQUEST DTOs ============

type SendOTPRequest struct {
	Phone string `json:"phone" validate:"required,e164"`
}

type VerifyOTPRequest struct {
	Phone string `json:"phone" validate:"required,e164"`
	Code  string `json:"code" validate:"required,len=6"`
}

type RegisterRequest struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required,min=8"`
	Name     string `json:"name" validate:"required,min=2"`
	Phone    string `json:"phone,omitempty" validate:"omitempty,e164"`
}

type LoginRequest struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required"`
}

type RefreshTokenRequest struct {
	RefreshToken string `json:"refresh_token" validate:"required"`
}

type UpdateProfileRequest struct {
	Name  string `json:"name,omitempty" validate:"omitempty,min=2"`
	Phone string `json:"phone,omitempty" validate:"omitempty,e164"`
}

// ============ AUTH RESPONSE DTOs ============

type AuthResponse struct {
	Tokens *TokensResponse `json:"tokens"`
	User   *UserResponse   `json:"user"`
}

type TokensResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	ExpiresIn   int64  `json:"expires_in"`
	TokenType   string `json:"token_type"`
}

type UserResponse struct {
	ID            string    `json:"id"`
	Email         string    `json:"email,omitempty"`
	Phone         string    `json:"phone,omitempty"`
	Name          string    `json:"name"`
	Role          string    `json:"role"`
	Tier          string    `json:"tier,omitempty"`
	EmailVerified bool      `json:"emailVerified,omitempty"`
	PhoneVerified bool      `json:"phoneVerified,omitempty"`
	CreatedAt     time.Time `json:"createdAt,omitempty"`
	UpdatedAt     time.Time `json:"updatedAt,omitempty"`
}

type OTPSentResponse struct {
	Message           string `json:"message"`
	ExpiresIn        int    `json:"expires_in"`
	ResendAvailableIn int    `json:"resend_available_in"`
}

type TokenRefreshResponse struct {
	AccessToken string `json:"access_token"`
	ExpiresIn  int64  `json:"expires_in"`
	TokenType  string `json:"token_type"`
}
