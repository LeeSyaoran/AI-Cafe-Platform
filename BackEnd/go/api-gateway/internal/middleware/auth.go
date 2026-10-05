package middleware

import (
	"strings"

	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"

	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
)

type AuthMiddleware struct {
	authService *service.AuthService
	jwtConfig   config.JWTConfig
}

func NewAuthMiddleware(authService *service.AuthService, jwtConfig config.JWTConfig) *AuthMiddleware {
	return &AuthMiddleware{authService: authService, jwtConfig: jwtConfig}
}

func (m *AuthMiddleware) JWTAuth() fiber.Handler {
	return func(c *fiber.Ctx) error {
		authHeader := c.Get("Authorization")
		if authHeader == "" {
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("MISSING_TOKEN", "Authorization header required"))
		}

		parts := strings.Split(authHeader, " ")
		if len(parts) != 2 || parts[0] != "Bearer" {
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("INVALID_TOKEN_FORMAT", "Bearer token required"))
		}

		tokenString := parts[1]

		claims, err := m.authService.ValidateToken(tokenString)
		if err != nil {
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("INVALID_TOKEN", "Invalid or expired token"))
		}

		// Parse token for user details
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			return []byte(m.jwtConfig.Secret), nil
		})
		if err != nil || !token.Valid {
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("INVALID_TOKEN", "Invalid token"))
		}

		// Store user info in locals
		c.Locals("user", token)
		c.Locals("userId", claims.UserID)
		c.Locals("email", claims.Email)
		c.Locals("role", claims.Role)

		return c.Next()
	}
}

func (m *AuthMiddleware) RequireRole(roles ...string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		userRole := c.Locals("role").(string)

		for _, role := range roles {
			if userRole == role {
				return c.Next()
			}
		}

		return c.Status(fiber.StatusForbidden).JSON(response.Error("FORBIDDEN", "Insufficient permissions"))
	}
}
