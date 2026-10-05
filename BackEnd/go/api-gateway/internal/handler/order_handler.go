package handler

import (
	"context"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/google/uuid"
	"aicafe-api-gateway/internal/model"
	"aicafe-api-gateway/internal/repository"
	"aicafe-api-gateway/pkg/response"
)

type OrderHandler struct {
	repo *repository.PostgresRepo
}

func NewOrderHandler(repo *repository.PostgresRepo) *OrderHandler {
	return &OrderHandler{repo: repo}
}

// POST /v1/orders
func (h *OrderHandler) CreateOrder(c *fiber.Ctx) error {
	var req struct {
		CafeID      string `json:"cafeId"`
		OrderType   string `json:"orderType"`
		CustomerNote string `json:"customerNote"`
		Items       []struct {
			ProductID string `json:"productId"`
			Quantity  int    `json:"quantity"`
			Notes     string `json:"notes"`
		} `json:"items"`
	}

	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	userID := c.Locals("userId").(string)
	companyID := "00000000-0000-0000-0000-000000000001" // TODO: from JWT

	// Generate order number
	orderNumber := generateOrderNumber()

	// TODO: Calculate totals, apply discounts, tax
	subtotal := 0.0
	taxRate := 0.1
	taxAmount := subtotal * taxRate

	order := &model.Order{
		ID:            uuid.New(),
		OrderNumber:   orderNumber,
		UserID:        uuid.MustParse(userID),
		CompanyID:     uuid.MustParse(companyID),
		CafeID:        uuid.MustParse(req.CafeID),
		OrderType:     req.OrderType,
		Status:        "pending",
		Subtotal:      subtotal,
		TaxAmount:     taxAmount,
		TotalAmount:   subtotal + taxAmount,
		TotalPaid:     0,
		PaymentStatus: "pending",
		CustomerNote:  req.CustomerNote,
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	ctx := context.Background()
	if err := h.repo.CreateOrder(ctx, order); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("ORDER_CREATE_FAILED", err.Error()))
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"orderId":     order.ID,
		"orderNumber": order.OrderNumber,
		"status":      order.Status,
	}))
}

// GET /v1/orders
func (h *OrderHandler) ListOrders(c *fiber.Ctx) error {
	userID := c.Locals("userId").(string)
	limit := c.QueryInt("limit", 20)
	offset := c.QueryInt("offset", 0)

	ctx := context.Background()
	orders, err := h.repo.GetOrdersByUser(ctx, uuid.MustParse(userID), limit, offset)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("FETCH_ORDERS_FAILED", err.Error()))
	}

	return c.JSON(response.Success(fiber.Map{
		"orders": orders,
		"total":  len(orders),
		"limit":  limit,
		"offset": offset,
	}))
}

// GET /v1/orders/:id
func (h *OrderHandler) GetOrder(c *fiber.Ctx) error {
	orderID := c.Params("id")

	ctx := context.Background()
	order, err := h.repo.GetOrderByID(ctx, uuid.MustParse(orderID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("FETCH_ORDER_FAILED", err.Error()))
	}
	if order == nil {
		return c.Status(fiber.StatusNotFound).JSON(response.Error("ORDER_NOT_FOUND", "Order not found"))
	}

	return c.JSON(response.Success(order))
}

// PUT /v1/orders/:id/cancel
func (h *OrderHandler) CancelOrder(c *fiber.Ctx) error {
	orderID := c.Params("id")

	ctx := context.Background()
	order, err := h.repo.GetOrderByID(ctx, uuid.MustParse(orderID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("FETCH_ORDER_FAILED", err.Error()))
	}
	if order == nil {
		return c.Status(fiber.StatusNotFound).JSON(response.Error("ORDER_NOT_FOUND", "Order not found"))
	}

	// Check if order can be cancelled
	if order.Status != "pending" && order.Status != "confirmed" {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("CANNOT_CANCEL", "Order cannot be cancelled"))
	}

	if err := h.repo.UpdateOrderStatus(ctx, uuid.MustParse(orderID), "cancelled"); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("CANCEL_ORDER_FAILED", err.Error()))
	}

	return c.JSON(response.Success(fiber.Map{
		"orderId": orderID,
		"status":  "cancelled",
	}))
}

func generateOrderNumber() string {
	return time.Now().Format("20060102150405")
}
