package config

import "os"

type Config struct {
	Port                   string
	DatabaseURL            string
	RedisURL               string
	JWT                    JWTConfig
	CORSOrigins            string
	RateLimit              RateLimitConfig
	UserServiceURL         string
	PaymentServiceURL      string
	MemberServiceURL       string
	OrderServiceURL        string
	NotificationServiceURL string
	AdminServiceURL        string
	CafeServiceURL         string
}

type JWTConfig struct {
	Secret               string
	AccessTokenExpiration  int64 // seconds
	RefreshTokenExpiration int64 // seconds
	Issuer               string
}

func (c *JWTConfig) GetSigningKey() []byte {
	return []byte(c.Secret)
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
		UserServiceURL:         getEnv("USER_SERVICE_URL", "http://aicafe-user-service:3004"),
		PaymentServiceURL:      getEnv("PAYMENT_SERVICE_URL", "http://aicafe-payment-service:3005"),
		MemberServiceURL:       getEnv("MEMBER_SERVICE_URL", "http://aicafe-member-service:3006"),
		OrderServiceURL:        getEnv("ORDER_SERVICE_URL", "http://aicafe-order-service:3007"),
		NotificationServiceURL: getEnv("NOTIFICATION_SERVICE_URL", "http://aicafe-notification-service:3008"),
		AdminServiceURL:        getEnv("ADMIN_SERVICE_URL", "http://aicafe-admin-service:3009"),
		CafeServiceURL:         getEnv("CAFE_SERVICE_URL", "http://aicafe-cafe-service:3010"),
	}
}

func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
