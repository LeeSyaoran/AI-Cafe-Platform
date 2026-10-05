package config

import (
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type JWTClaims struct {
	UserID string `json:"userId"`
	Email  string `json:"email"`
	Role   string `json:"role"`
	Tier   string `json:"tier,omitempty"`
	jwt.RegisteredClaims
}

func NewJWTClaims(userID, email, role string) *JWTClaims {
	return &JWTClaims{
		UserID: userID,
		Email:  email,
		Role:   role,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(time.Hour)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}
}

type CustomClaims struct {
	jwt.RegisteredClaims
	UserID string `json:"userId"`
	Email  string `json:"email"`
	Role   string `json:"role"`
}
