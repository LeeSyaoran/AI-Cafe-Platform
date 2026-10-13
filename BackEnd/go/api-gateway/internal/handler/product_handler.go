package handler

import (
	"github.com/gofiber/fiber/v2"

	"aicafe-api-gateway/internal/dto"
	"aicafe-api-gateway/internal/service"
	"aicafe-api-gateway/pkg/response"
)

// ProductHandler handles product endpoints
type ProductHandler struct {
	productService *service.ProductService
}

func NewProductHandler(productService *service.ProductService) *ProductHandler {
	return &ProductHandler{productService: productService}
}

// GetProducts returns paginated products
// GET /v1/products
func (h *ProductHandler) GetProducts(c *fiber.Ctx) error {
	// Default company ID - should come from JWT in production
	companyID := "00000000-0000-0000-0000-000000000001"

	params := &dto.ProductQueryParams{
		CategoryID: c.Query("categoryId"),
		Search:     c.Query("search"),
		Page:       c.QueryInt("page", 1),
		Limit:      c.QueryInt("limit", 20),
	}

	result, err := h.productService.GetProducts(c.Context(), companyID, params)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.SuccessWithMeta(result.Products, params.Page, params.Limit, result.Meta.Total))
}

// GetFeaturedProducts returns featured products
// GET /v1/products/featured
func (h *ProductHandler) GetFeaturedProducts(c *fiber.Ctx) error {
	companyID := "00000000-0000-0000-0000-000000000001"
	limit := c.QueryInt("limit", 10)

	result, err := h.productService.GetFeaturedProducts(c.Context(), companyID, limit)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result.Products))
}

// GetBestSellers returns best selling products
// GET /v1/products/best-sellers
func (h *ProductHandler) GetBestSellers(c *fiber.Ctx) error {
	// For now, reuse featured products (best sellers are included)
	return h.GetFeaturedProducts(c)
}

// GetProduct returns a single product
// GET /v1/products/:id
func (h *ProductHandler) GetProduct(c *fiber.Ctx) error {
	productID := c.Params("id")

	result, err := h.productService.GetProductByID(c.Context(), productID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result))
}

// GetCategories returns all product categories
// GET /v1/categories
func (h *ProductHandler) GetCategories(c *fiber.Ctx) error {
	companyID := "00000000-0000-0000-0000-000000000001"

	result, err := h.productService.GetCategories(c.Context(), companyID)
	if err != nil {
		return h.handleError(c, err)
	}

	return c.JSON(response.Success(result.Categories))
}

// CreateProduct creates a new product (Admin)
// POST /v1/admin/products
func (h *ProductHandler) CreateProduct(c *fiber.Ctx) error {
	// TODO: Implement with service
	return c.Status(fiber.StatusNotImplemented).JSON(response.Error("NOT_IMPLEMENTED", "Admin product creation not implemented"))
}

// UpdateProduct updates a product (Admin)
// PUT /v1/admin/products/:id
func (h *ProductHandler) UpdateProduct(c *fiber.Ctx) error {
	// TODO: Implement with service
	return c.Status(fiber.StatusNotImplemented).JSON(response.Error("NOT_IMPLEMENTED", "Admin product update not implemented"))
}

// DeleteProduct deletes a product (Admin)
// DELETE /v1/admin/products/:id
func (h *ProductHandler) DeleteProduct(c *fiber.Ctx) error {
	// TODO: Implement with service
	return c.Status(fiber.StatusNotImplemented).JSON(response.Error("NOT_IMPLEMENTED", "Admin product deletion not implemented"))
}

// handleError converts service errors to HTTP responses
func (h *ProductHandler) handleError(c *fiber.Ctx, err error) error {
	// TODO: Map specific errors to proper status codes
	return c.Status(fiber.StatusBadRequest).JSON(response.Error("ERROR", err.Error()))
}
