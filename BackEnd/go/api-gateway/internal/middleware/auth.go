package middleware

import (
	"log"
	"strings"

	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"

	"github.com/gofiber/fiber/v2"
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
		log.Printf("[DEBUG] JWTAuth middleware called for path: %s", c.Path())

		authHeader := c.Get("Authorization")
		if authHeader == "" {
			log.Printf("[DEBUG] No Authorization header")
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("MISSING_TOKEN", "Authorization header required"))
		}

		parts := strings.Split(authHeader, " ")
		if len(parts) != 2 || parts[0] != "Bearer" {
			log.Printf("[DEBUG] Invalid token format")
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("INVALID_TOKEN_FORMAT", "Bearer token required"))
		}

		tokenString := parts[1]
		log.Printf("[DEBUG] Validating token...")

		claims, err := m.authService.ValidateToken(tokenString)
		if err != nil {
			log.Printf("[DEBUG] ValidateToken failed: %v", err)
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("INVALID_TOKEN", "Invalid or expired token"))
		}

		log.Printf("[DEBUG] Token valid, userId: %s", claims.UserID)

		// Store user info in locals
		c.Locals("user", claims)
		c.Locals("userId", claims.UserID)
		c.Locals("email", claims.Email)
		c.Locals("role", claims.Role)

		return c.Next()
	}
}

func (m *AuthMiddleware) RequireRole(roles ...string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		userRole, ok := c.Locals("role").(string)
		if !ok || userRole == "" {
			return c.Status(fiber.StatusUnauthorized).JSON(response.Error("UNAUTHORIZED", "User role not found"))
		}

		for _, role := range roles {
			if userRole == role {
				return c.Next()
			}
		}

		return c.Status(fiber.StatusForbidden).JSON(response.Error("FORBIDDEN", "Insufficient permissions"))
	}
}
