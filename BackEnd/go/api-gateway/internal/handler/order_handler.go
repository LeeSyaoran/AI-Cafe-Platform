package handler

import (
	"strconv"

	"github.com/gofiber/fiber/v2"

	"aicafe-api-gateway/internal/dto"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"
)

// OrderHandler handles order endpoints
type OrderHandler struct {
	orderService *service.OrderService
}

func NewOrderHandler(orderService *service.OrderService) *OrderHandler {
	return &OrderHandler{orderService: orderService}
}

// CreateOrder creates a new order from cart
// POST /v1/orders
func (h *OrderHandler) CreateOrder(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)

	var req dto.CreateOrderRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.orderService.CreateOrderFromCart(c.Context(), userID, &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(result))
}

// ListOrders returns paginated orders for user
// GET /v1/orders
func (h *OrderHandler) ListOrders(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)

	page, _ := strconv.Atoi(c.Query("page", "1"))
	limit := c.QueryInt("limit", 20)

	result, err := h.orderService.GetOrders(c.Context(), userID, page, limit)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.SuccessWithMeta(result.Orders, page, limit, result.Meta.Total))
}

// GetOrder returns a single order
// GET /v1/orders/:id
func (h *OrderHandler) GetOrder(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)
	orderID := c.Params("id")

	result, err := h.orderService.GetOrderByID(c.Context(), userID, orderID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// CancelOrder cancels an order
// PUT /v1/orders/:id/cancel
func (h *OrderHandler) CancelOrder(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)
	orderID := c.Params("id")

	var req dto.CancelOrderRequest
	if err := c.BodyParser(&req); err != nil {
		// Ignore parse errors for optional body
		req = dto.CancelOrderRequest{}
	}

	result, err := h.orderService.CancelOrder(c.Context(), userID, orderID, &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// handleError converts service errors to HTTP responses
func (h *OrderHandler) handleError(c *fiber.Ctx, err error) error {
	// TODO: Map specific errors to proper status codes
	return c.Status(fiber.StatusBadRequest).JSON(response.Error("ERROR", err.Error()))
}
