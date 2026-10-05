package handler

import (
	"github.com/gofiber/websocket/v2"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"
)

type AIHandler struct {
	creditService *service.CreditService
}

func NewAIHandler(creditService *service.CreditService) *AIHandler {
	return &AIHandler{creditService: creditService}
}

// ChatWebSocket handles real-time AI chat via WebSocket
func (h *AIHandler) ChatWebSocket(c *websocket.Conn) {
	// TODO: Implement WebSocket chat
	// 1. Authenticate user from token in query param
	// 2. Store connection in session manager
	// 3. Handle incoming messages
	// 4. Stream AI responses

	for {
		messageType, msg, err := c.ReadMessage()
		if err != nil {
			break
		}

		if messageType == websocket.TextMessage {
			// Echo back for now
			c.WriteMessage(messageType, []byte(`{"type":"pong"}`))
		}
	}
}

// GET /v1/credits/balance
func GetCreditBalance(c *fiber.Ctx) error {
	userID := c.Locals("userId")
	return c.JSON(response.Success(fiber.Map{
		"userId":   userID,
		"balance":  1000.0,
		"currency": "credits",
	}))
}

// GET /v1/cafes
func ListCafes(c *fiber.Ctx) error {
	return c.JSON(response.Success([]interface{}{
		fiber.Map{
			"id":                  "00000000-0000-0000-0000-000000000001",
			"name":                "AI Café Nguyễn Huệ",
			"slug":                "aica-nguyen-hue",
			"address":             "123 Nguyễn Huệ, Quận 1, TP.HCM",
			"phone":               "02812345678",
			"isDeliveryEnabled":   true,
			"isPickupEnabled":     true,
			"isReservationEnabled": true,
		},
	}))
}

// GET /v1/cafes/:id
func GetCafe(c *fiber.Ctx) error {
	id := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"id": id,
		"name": "AI Café Nguyễn Huệ",
		"address": "123 Nguyễn Huệ, Quận 1, TP.HCM",
		"hours": "7:00 - 22:00",
		"phone": "02812345678",
	}))
}

// GET /v1/categories
func ListCategories(c *fiber.Ctx) error {
	return c.JSON(response.Success([]interface{}{
		fiber.Map{"id": "1", "name": "Coffee", "slug": "coffee", "icon": "☕", "color": "#6F4E37"},
		fiber.Map{"id": "2", "name": "Tea", "slug": "tea", "icon": "🍵", "color": "#98D8C8"},
		fiber.Map{"id": "3", "name": "Fresh Juice", "slug": "fresh-juice", "icon": "🧃", "color": "#F7DC6F"},
		fiber.Map{"id": "4", "name": "Bakery", "slug": "bakery", "icon": "🥐", "color": "#F8B500"},
		fiber.Map{"id": "5", "name": "Desserts", "slug": "desserts", "icon": "🍰", "color": "#FF69B4"},
		fiber.Map{"id": "6", "name": "AI Specials", "slug": "ai-specials", "icon": "🤖", "color": "#9B59B6"},
	}))
}

// POST /v1/admin/categories (Admin)
func CreateCategory(c *fiber.Ctx) error {
	var req struct {
		Name      string `json:"name"`
		Icon      string `json:"icon"`
		Color     string `json:"color"`
		SortOrder int    `json:"sortOrder"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}
	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"message": "Category created",
	}))
}

// PUT /v1/admin/categories/:id (Admin)
func UpdateCategory(c *fiber.Ctx) error {
	id := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Category updated",
	}))
}

// DELETE /v1/admin/categories/:id (Admin)
func DeleteCategory(c *fiber.Ctx) error {
	id := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Category deleted",
	}))
}
