package config

import "github.com/golang-jwt/jwt/v5"

type JWTClaims struct {
	UserID string `json:"userId"`
	Email  string `json:"email"`
	Role   string `json:"role"`
}

type JWTConfig struct {
	Secret                  string
	AccessTokenExpiration  int64
	RefreshTokenExpiration int64
	Issuer                 string
}

func (c *JWTConfig) GetSigningKey() []byte {
	return []byte(c.Secret)
}

type CustomClaims struct {
	jwt.RegisteredClaims
	UserID string `json:"userId"`
	Email  string `json:"email"`
	Role   string `json:"role"`
}
