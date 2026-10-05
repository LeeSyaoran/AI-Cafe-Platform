package handler

import (
	"log"

	"github.com/gofiber/fiber/v2"
)

// HealthCheck - GET /health
func HealthCheck(c *fiber.Ctx) error {
	return c.JSON(fiber.Map{
		"status":  "healthy",
		"service": "aicafe-api-gateway",
	})
}

// APIVersion - GET /v1
func APIVersion(c *fiber.Ctx) error {
	return c.JSON(fiber.Map{
		"version": "1.0.0",
		"name":    "AI Café API",
	})
}

// ErrorHandler - custom error handler
func ErrorHandler(c *fiber.Ctx, err error) error {
	code := fiber.StatusInternalServerError
	message := "Internal Server Error"

	if e, ok := err.(*fiber.Error); ok {
		code = e.Code
		message = e.Message
	}

	log.Printf("[ERROR] Fiber ErrorHandler: %v (code=%d)", err, code)

	return c.Status(code).JSON(fiber.Map{
		"error": fiber.Map{
			"code":    code,
			"message": message,
		},
	})
}
