package handler

import (
	"github.com/gofiber/fiber/v2"
	"aicafe-api-gateway/pkg/response"
)

type ProductHandler struct {
	productService interface {
		GetProducts(ctx interface{}, companyID interface{}, categoryID interface{}, search string, limit, offset int) (interface{}, error)
		GetProductByID(ctx interface{}, id interface{}) (interface{}, error)
		GetCategories(ctx interface{}, companyID interface{}) (interface{}, error)
	}
}

func NewProductHandler(productService interface{}) *ProductHandler {
	return &ProductHandler{productService: productService}
}

// GET /v1/products
func (h *ProductHandler) ListProducts(c *fiber.Ctx) error {
	companyID := c.Query("companyId")
	search := c.Query("search")
	categoryID := c.Query("categoryId")
	limit := c.QueryInt("limit", 20)
	offset := c.QueryInt("offset", 0)

	// TODO: Implement actual service call
	return c.JSON(response.Success(fiber.Map{
		"products": []interface{}{},
		"total":    0,
		"limit":    limit,
		"offset":   offset,
	}))
}

// GET /v1/products/featured
func (h *ProductHandler) GetFeaturedProducts(c *fiber.Ctx) error {
	return c.JSON(response.Success(fiber.Map{
		"products": []interface{}{},
	}))
}

// GET /v1/products/best-sellers
func (h *ProductHandler) GetBestSellers(c *fiber.Ctx) error {
	return c.JSON(response.Success(fiber.Map{
		"products": []interface{}{},
	}))
}

// GET /v1/products/:id
func (h *ProductHandler) GetProduct(c *fiber.Ctx) error {
	id := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"id":    id,
		"name":  "Sample Product",
		"price": 45000,
	}))
}

// POST /v1/admin/products (Admin)
func (h *ProductHandler) CreateProduct(c *fiber.Ctx) error {
	var req struct {
		Name        string  `json:"name"`
		Price       float64 `json:"price"`
		Description string  `json:"description"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}
	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"message": "Product created",
	}))
}

// PUT /v1/admin/products/:id (Admin)
func (h *ProductHandler) UpdateProduct(c *fiber.Ctx) error {
	id := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Product updated",
	}))
}

// DELETE /v1/admin/products/:id (Admin)
func (h *ProductHandler) DeleteProduct(c *fiber.Ctx) error {
	id := c.Params("id")
	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Product deleted",
	}))
}
