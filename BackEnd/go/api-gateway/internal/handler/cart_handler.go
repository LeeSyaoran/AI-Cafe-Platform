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

type CartHandler struct {
	repo *repository.PostgresRepo
}

func NewCartHandler(repo *repository.PostgresRepo) *CartHandler {
	return &CartHandler{repo: repo}
}

// GET /v1/cart
func (h *CartHandler) GetCart(c *fiber.Ctx) error {
	userID := c.Locals("userId").(string)
	cafeID := c.Query("cafeId", "00000000-0000-0000-0000-000000000001")

	ctx := context.Background()
	cart, err := h.repo.GetCart(ctx, uuid.MustParse(userID), uuid.MustParse(cafeID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("FETCH_CART_FAILED", err.Error()))
	}
	if cart == nil {
		return c.JSON(response.Success(fiber.Map{
			"id":        nil,
			"items":     []interface{}{},
			"subtotal":  0,
			"itemCount": 0,
		}))
	}

	// Get cart items
	items, err := h.repo.GetCartItems(ctx, cart.ID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("FETCH_CART_ITEMS_FAILED", err.Error()))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":        cart.ID,
		"items":     items,
		"subtotal":  cart.Subtotal,
		"itemCount": cart.ItemCount,
	}))
}

// POST /v1/cart/items
func (h *CartHandler) AddItem(c *fiber.Ctx) error {
	var req struct {
		ProductID string `json:"productId"`
		CafeID   string `json:"cafeId"`
		Quantity int    `json:"quantity"`
		Notes    string `json:"notes"`
	}

	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	userID := c.Locals("userId").(string)
	companyID := "00000000-0000-0000-0000-000000000001"

	ctx := context.Background()

	// Get or create cart
	cart, err := h.repo.GetCart(ctx, uuid.MustParse(userID), uuid.MustParse(req.CafeID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(response.Error("FETCH_CART_FAILED", err.Error()))
	}

	if cart == nil {
		cart = &model.Cart{
			ID:        uuid.New(),
			UserID:    uuid.MustParse(userID),
			CompanyID: uuid.MustParse(companyID),
			CafeID:    uuid.MustParse(req.CafeID),
			Subtotal:  0,
			ItemCount: 0,
			CreatedAt: time.Now(),
			UpdatedAt: time.Now(),
		}
		if err := h.repo.CreateCart(ctx, cart); err != nil {
			return c.Status(fiber.StatusInternalServerError).JSON(response.Error("CREATE_CART_FAILED", err.Error()))
		}
	}

	// TODO: Add item to cart, calculate totals
	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"cartId": cart.ID,
		"message": "Item added to cart",
	}))
}

// PUT /v1/cart/items/:id
func (h *CartHandler) UpdateItem(c *fiber.Ctx) error {
	itemID := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"itemId": itemID,
		"message": "Cart item updated",
	}))
}

// DELETE /v1/cart/items/:id
func (h *CartHandler) RemoveItem(c *fiber.Ctx) error {
	itemID := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"itemId": itemID,
		"message": "Cart item removed",
	}))
}

// POST /v1/cart/clear
func (h *CartHandler) ClearCart(c *fiber.Ctx) error {
	return c.JSON(response.Success(fiber.Map{
		"message": "Cart cleared",
	}))
}

// POST /v1/cart/checkout
func (h *CartHandler) Checkout(c *fiber.Ctx) error {
	var req struct {
		PaymentMethod string `json:"paymentMethod"`
	}

	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	// TODO: Implement checkout logic
	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"orderId": uuid.New(),
		"message": "Checkout initiated",
		"paymentMethod": req.PaymentMethod,
	}))
}
