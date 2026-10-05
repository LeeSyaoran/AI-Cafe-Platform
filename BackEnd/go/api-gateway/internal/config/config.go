package main

import "os"

type Config struct {
	Port         string
	DatabaseURL  string
	RedisURL     string
	JWT          JWTConfig
	CORSOrigins  string
	RateLimit    RateLimitConfig
}

type JWTConfig struct {
	Secret               string
	AccessTokenExpiration  int64 // seconds
	RefreshTokenExpiration int64 // seconds
	Issuer               string
}

type RateLimitConfig struct {
	Capacity int
	Duration int // seconds
}

func Load() *Config {
	return &Config{
		Port:        getEnv("PORT", "3000"),
		DatabaseURL: getEnv("DATABASE_URL", "postgres://aicafe:aicafe@localhost:5432/aicafe?sslmode=disable"),
		RedisURL:    getEnv("REDIS_URL", "redis://localhost:6379"),
		JWT: JWTConfig{
			Secret:                getEnv("JWT_SECRET", "change-me-in-production"),
			AccessTokenExpiration:  3600,   // 1 hour
			RefreshTokenExpiration: 604800, // 7 days
			Issuer:                "aicafe-api-gateway",
		},
		CORSOrigins: getEnv("CORS_ORIGINS", "http://localhost:3001,http://localhost:5173"),
		RateLimit: RateLimitConfig{
			Capacity: 100,
			Duration: 60,
		},
	}
}

func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
