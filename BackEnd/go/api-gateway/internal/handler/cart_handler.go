package handler

import (
	"github.com/gofiber/fiber/v2"

	"aicafe-api-gateway/internal/dto"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"
)

// CartHandler handles cart endpoints
type CartHandler struct {
	cartService  *service.CartService
	orderService *service.OrderService
}

func NewCartHandler(cartService *service.CartService, orderService *service.OrderService) *CartHandler {
	return &CartHandler{
		cartService:  cartService,
		orderService: orderService,
	}
}

// GetCart returns user's cart
// GET /v1/cart
func (h *CartHandler) GetCart(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)
	cafeID := c.Query("cafeId", "00000000-0000-0000-0000-000000000001")

	result, err := h.cartService.GetCart(c.Context(), userID, cafeID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// AddItem adds an item to cart
// POST /v1/cart/items
func (h *CartHandler) AddItem(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)

	var req dto.AddCartItemRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.cartService.AddItem(c.Context(), userID, &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(result))
}

// UpdateItem updates a cart item
// PUT /v1/cart/items/:id
func (h *CartHandler) UpdateItem(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)
	itemID := c.Params("id")

	var req dto.UpdateCartItemRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	result, err := h.cartService.UpdateItem(c.Context(), userID, itemID, &req)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// RemoveItem removes an item from cart
// DELETE /v1/cart/items/:id
func (h *CartHandler) RemoveItem(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)
	itemID := c.Params("id")

	err := h.cartService.RemoveItem(c.Context(), userID, itemID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(fiber.Map{
		"message": "Item removed from cart",
	}))
}

// ClearCart clears all items from cart
// DELETE /v1/cart
func (h *CartHandler) ClearCart(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)
	cafeID := c.Query("cafeId", "00000000-0000-0000-0000-000000000001")

	err := h.cartService.ClearCart(c.Context(), userID, cafeID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(fiber.Map{
		"message": "Cart cleared",
	}))
}

// Checkout creates order from cart
// POST /v1/cart/checkout
func (h *CartHandler) Checkout(c *fiber.Ctx) error {
	userID := c.Locals("userID").(string)

	var req dto.CheckoutRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	// Use OrderService for checkout
	createReq := &dto.CreateOrderRequest{
		CafeID:           req.CafeID,
		OrderType:        req.OrderType,
		DeliveryAddressID: req.DeliveryAddressID,
		CustomerNote:     req.CustomerNote,
		PromoCode:        req.PromoCode,
		PaymentMethod:    req.PaymentMethod,
	}

	result, err := h.orderService.CreateOrderFromCart(c.Context(), userID, createReq)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(result))
}

// handleError converts service errors to HTTP responses
func (h *CartHandler) handleError(c *fiber.Ctx, err error) error {
	// TODO: Map specific errors to proper status codes
	return c.Status(fiber.StatusBadRequest).JSON(response.Error("ERROR", err.Error()))
}
