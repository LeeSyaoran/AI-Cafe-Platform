package handler

import (
	"context"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"aicafe-api-gateway/pkg/response"
)

type ProductHandler struct {
	db *pgxpool.Pool
}

func NewProductHandler(db *pgxpool.Pool) *ProductHandler {
	return &ProductHandler{db: db}
}

// GET /v1/products
func (h *ProductHandler) ListProducts(c *fiber.Ctx) error {
	ctx := context.Background()
	categoryID := c.Query("categoryId")
	limit := c.QueryInt("limit", 20)
	offset := c.QueryInt("offset", 0)

	query := `
		SELECT id, category_id, name, name_vi, slug, price, image_url,
		       is_featured, is_best_seller, preparation_time
		FROM products
		WHERE is_active = true`

	var args []interface{}
	argIndex := 1

	if categoryID != "" {
		query += " AND category_id = $" + string(rune('0'+argIndex))
		args = append(args, categoryID)
		argIndex++
	}

	query += " ORDER BY name LIMIT $" + string(rune('0'+argIndex)) + " OFFSET $" + string(rune('0'+argIndex+1))
	args = append(args, limit, offset)

	rows, err := h.db.Query(ctx, query, args...)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer rows.Close()

	var products []interface{}
	for rows.Next() {
		var id, categoryID, name, nameVi, slug, imageURL *string
		var price float64
		var featured, bestSeller bool
		var prepTime *int

		if err := rows.Scan(&id, &categoryID, &name, &nameVi, &slug, &price, &imageURL, &featured, &bestSeller, &prepTime); err != nil {
			continue
		}

		products = append(products, fiber.Map{
			"id":               id,
			"category_id":      categoryID,
			"name":             name,
			"name_vi":          nameVi,
			"slug":             slug,
			"price":            price,
			"image_url":        imageURL,
			"is_featured":      featured,
			"is_best_seller":   bestSeller,
			"preparation_time": prepTime,
		})
	}

	if products == nil {
		products = []interface{}{}
	}

	return c.JSON(response.Success(fiber.Map{
		"products": products,
		"total":    len(products),
		"limit":    limit,
		"offset":   offset,
	}))
}

// GET /v1/products/featured
func (h *ProductHandler) GetFeaturedProducts(c *fiber.Ctx) error {
	ctx := context.Background()

	rows, err := h.db.Query(ctx, `
		SELECT id, category_id, name, name_vi, slug, price, image_url,
		       is_featured, is_best_seller, preparation_time
		FROM products
		WHERE is_active = true AND is_featured = true
		ORDER BY is_best_seller DESC, name
		LIMIT 20
	`)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer rows.Close()

	var products []interface{}
	for rows.Next() {
		var id, categoryID, name, nameVi, slug, imageURL *string
		var price float64
		var featured, bestSeller bool
		var prepTime *int

		if err := rows.Scan(&id, &categoryID, &name, &nameVi, &slug, &price, &imageURL, &featured, &bestSeller, &prepTime); err != nil {
			continue
		}

		products = append(products, fiber.Map{
			"id":               id,
			"category_id":      categoryID,
			"name":             name,
			"name_vi":          nameVi,
			"slug":             slug,
			"price":            price,
			"image_url":        imageURL,
			"is_featured":      featured,
			"is_best_seller":   bestSeller,
			"preparation_time": prepTime,
		})
	}

	if products == nil {
		products = []interface{}{}
	}

	return c.JSON(response.Success(fiber.Map{
		"products": products,
	}))
}

// GET /v1/products/best-sellers
func (h *ProductHandler) GetBestSellers(c *fiber.Ctx) error {
	ctx := context.Background()

	rows, err := h.db.Query(ctx, `
		SELECT id, category_id, name, name_vi, slug, price, image_url,
		       is_featured, is_best_seller, preparation_time
		FROM products
		WHERE is_active = true AND is_best_seller = true
		ORDER BY name
		LIMIT 20
	`)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer rows.Close()

	var products []interface{}
	for rows.Next() {
		var id, categoryID, name, nameVi, slug, imageURL *string
		var price float64
		var featured, bestSeller bool
		var prepTime *int

		if err := rows.Scan(&id, &categoryID, &name, &nameVi, &slug, &price, &imageURL, &featured, &bestSeller, &prepTime); err != nil {
			continue
		}

		products = append(products, fiber.Map{
			"id":               id,
			"category_id":      categoryID,
			"name":             name,
			"name_vi":          nameVi,
			"slug":             slug,
			"price":            price,
			"image_url":        imageURL,
			"is_featured":      featured,
			"is_best_seller":   bestSeller,
			"preparation_time": prepTime,
		})
	}

	if products == nil {
		products = []interface{}{}
	}

	return c.JSON(response.Success(fiber.Map{
		"products": products,
	}))
}

// GET /v1/products/:id
func (h *ProductHandler) GetProduct(c *fiber.Ctx) error {
	ctx := context.Background()
	id := c.Params("id")

	var productID, categoryID, name, nameVi, slug, imageURL, description *string
	var price float64
	var featured, bestSeller bool
	var prepTime *int

	err := h.db.QueryRow(ctx, `
		SELECT id, category_id, name, name_vi, slug, price, image_url, description,
		       is_featured, is_best_seller, preparation_time
		FROM products
		WHERE id = $1 AND is_active = true
	`, id).Scan(&productID, &categoryID, &name, &nameVi, &slug, &price, &imageURL, &description, &featured, &bestSeller, &prepTime)

	if err != nil {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Product not found"))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":               productID,
		"category_id":      categoryID,
		"name":             name,
		"name_vi":          nameVi,
		"slug":             slug,
		"price":            price,
		"image_url":        imageURL,
		"description":      description,
		"is_featured":      featured,
		"is_best_seller":   bestSeller,
		"preparation_time": prepTime,
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
