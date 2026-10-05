package handler

import (
	"context"
	"fmt"
	"log"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/websocket/v2"
	"github.com/google/uuid"
	"github.com/jackc/pgx/v5/pgxpool"

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
	for {
		_, msg, err := c.ReadMessage()
		if err != nil {
			break
		}

		// Echo back for now
		c.WriteMessage(websocket.TextMessage, []byte(`{"type":"pong"}` + string(msg)))
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

// GET /v1/cafes - List all cafes from database
func ListCafes(c *fiber.Ctx) error {
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	rows, err := pool.Query(ctx, `
		SELECT id, cafe_code, name, slug, address, phone, opening_hours,
		       is_delivery_enabled, is_pickup_enabled, is_reservation_enabled, tax_rate
		FROM cafes
		WHERE is_active = true
		ORDER BY name
	`)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer rows.Close()

	var cafes []interface{}
	for rows.Next() {
		var id, cafeCode, name, slug *string
		var address, phone, openingHours *string
		var delivery, pickup, reservation bool
		var taxRate *float64

		err := rows.Scan(&id, &cafeCode, &name, &slug, &address, &phone,
			&openingHours, &delivery, &pickup, &reservation, &taxRate)
		if err != nil {
			continue
		}

		cafes = append(cafes, fiber.Map{
			"id":                    id,
			"cafe_code":             cafeCode,
			"name":                  name,
			"slug":                  slug,
			"address":               address,
			"phone":                 phone,
			"opening_hours":         openingHours,
			"is_delivery_enabled":   delivery,
			"is_pickup_enabled":     pickup,
			"is_reservation_enabled": reservation,
			"tax_rate":              taxRate,
		})
	}

	if cafes == nil {
		cafes = []interface{}{}
	}

	return c.JSON(response.Success(cafes))
}

// GET /v1/cafes/:id - Get cafe by ID
func GetCafe(c *fiber.Ctx) error {
	id := c.Params("id")
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	var cafeCode, name, slug, address, phone, description, openingHours *string
	var delivery, pickup, reservation bool
	var taxRate *float64

	err := pool.QueryRow(ctx, `
		SELECT cafe_code, name, slug, address, phone, description,
		       opening_hours,
		       is_delivery_enabled, is_pickup_enabled, is_reservation_enabled, tax_rate
		FROM cafes WHERE id = $1 AND is_active = true
	`, id).Scan(&cafeCode, &name, &slug, &address, &phone, &description,
		&openingHours, &delivery, &pickup, &reservation, &taxRate)

	if err != nil {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Cafe not found"))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":                      id,
		"cafe_code":               cafeCode,
		"name":                    name,
		"slug":                    slug,
		"address":                 address,
		"phone":                   phone,
		"description":             description,
		"opening_hours":           openingHours,
		"is_delivery_enabled":     delivery,
		"is_pickup_enabled":       pickup,
		"is_reservation_enabled":   reservation,
		"tax_rate":                taxRate,
	}))
}

// GET /v1/categories - List categories from database
func ListCategories(c *fiber.Ctx) error {
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	rows, err := pool.Query(ctx, `
		SELECT id, name, slug, icon, color, sort_order, is_featured
		FROM categories
		WHERE is_active = true
		ORDER BY sort_order, name
	`)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer rows.Close()

	var categories []interface{}
	for rows.Next() {
		var id, name, slug, icon, color *string
		var sortOrder *int
		var isFeatured bool

		err := rows.Scan(&id, &name, &slug, &icon, &color, &sortOrder, &isFeatured)
		if err != nil {
			continue
		}

		categories = append(categories, fiber.Map{
			"id":          id,
			"name":        name,
			"slug":        slug,
			"icon":        icon,
			"color":       color,
			"sort_order":  sortOrder,
			"is_featured": isFeatured,
		})
	}

	if categories == nil {
		categories = []interface{}{}
	}

	return c.JSON(response.Success(categories))
}

// GET /v1/categories/:id - Get category with products
func GetCategory(c *fiber.Ctx) error {
	id := c.Params("id")
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	// Get category info
	var name, slug, icon, color *string
	err := pool.QueryRow(ctx, `
		SELECT name, slug, icon, color FROM categories WHERE id = $1 AND is_active = true
	`, id).Scan(&name, &slug, &icon, &color)
	if err != nil {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Category not found"))
	}

	// Get products in category
	rows, err := pool.Query(ctx, `
		SELECT id, name, name_vi, slug, price, image_url, is_best_seller, is_featured, preparation_time
		FROM products
		WHERE category_id = $1 AND is_active = true
		ORDER BY is_best_seller DESC, name
	`, id)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer rows.Close()

	var products []interface{}
	for rows.Next() {
		var prodID, prodName, prodNameVi, slug, imageURL *string
		var price float64
		var bestSeller, featured bool
		var prepTime *int

		err := rows.Scan(&prodID, &prodName, &prodNameVi, &slug, &price, &imageURL, &bestSeller, &featured, &prepTime)
		if err != nil {
			continue
		}

		products = append(products, fiber.Map{
			"id":               prodID,
			"name":             prodName,
			"name_vi":          prodNameVi,
			"slug":             slug,
			"price":            price,
			"image_url":        imageURL,
			"is_best_seller":   bestSeller,
			"is_featured":      featured,
			"preparation_time": prepTime,
		})
	}

	if products == nil {
		products = []interface{}{}
	}

	return c.JSON(response.Success(fiber.Map{
		"id":       id,
		"name":     name,
		"slug":     slug,
		"icon":     icon,
		"color":    color,
		"products": products,
	}))
}

// GET /v1/menu - Get full menu with categories and products
func GetMenu(c *fiber.Ctx) error {
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	// Get categories
	catRows, err := pool.Query(ctx, `
		SELECT id, name, slug, icon, color, sort_order, is_featured
		FROM categories
		WHERE is_active = true
		ORDER BY sort_order, name
	`)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer catRows.Close()

	type Product struct {
		ID              *string  `json:"id"`
		Name            *string  `json:"name"`
		NameVi          *string  `json:"name_vi"`
		Slug            *string  `json:"slug"`
		Price           float64  `json:"price"`
		ImageURL        *string  `json:"image_url"`
		IsBestSeller    bool     `json:"is_best_seller"`
		IsFeatured      bool     `json:"is_featured"`
		PreparationTime *int     `json:"preparation_time"`
	}

	type Category struct {
		ID        *string  `json:"id"`
		Name      *string  `json:"name"`
		Slug      *string  `json:"slug"`
		Icon      *string  `json:"icon"`
		Color     *string  `json:"color"`
		SortOrder *int     `json:"sort_order"`
		IsFeatured bool    `json:"is_featured"`
		Products  []Product `json:"products"`
	}

	var categories []Category
	for catRows.Next() {
		var cat Category
		err := catRows.Scan(&cat.ID, &cat.Name, &cat.Slug, &cat.Icon, &cat.Color, &cat.SortOrder, &cat.IsFeatured)
		if err != nil {
			continue
		}
		cat.Products = []Product{}
		categories = append(categories, cat)
	}

	// Get all products
	prodRows, err := pool.Query(ctx, `
		SELECT id, category_id, name, name_vi, slug, price, image_url, is_best_seller, is_featured, preparation_time
		FROM products
		WHERE is_active = true
		ORDER BY category_id, is_best_seller DESC, name
	`)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}
	defer prodRows.Close()

	// Map products to categories
	for prodRows.Next() {
		var prod Product
		var categoryID *string
		err := prodRows.Scan(&prod.ID, &categoryID, &prod.Name, &prod.NameVi, &prod.Slug,
			&prod.Price, &prod.ImageURL, &prod.IsBestSeller, &prod.IsFeatured, &prod.PreparationTime)
		if err != nil {
			continue
		}

		// Find matching category and add product
		for i := range categories {
			if categories[i].ID != nil && categoryID != nil && *categories[i].ID == *categoryID {
				categories[i].Products = append(categories[i].Products, prod)
				break
			}
		}
	}

	return c.JSON(response.Success(categories))
}

// GET /v1/wallet - Get user wallet
func GetWallet(c *fiber.Ctx) error {
	log.Printf("[DEBUG] GetWallet called")

	// Safely get userId
	userIDVal := c.Locals("userId")
	if userIDVal == nil {
		log.Printf("[DEBUG] GetWallet: userId is nil")
		return c.Status(401).JSON(response.Error("UNAUTHORIZED", "User not authenticated"))
	}

	userID, ok := userIDVal.(string)
	if !ok {
		log.Printf("[DEBUG] GetWallet: userId is not a string")
		return c.Status(500).JSON(response.Error("INTERNAL_ERROR", "Invalid user ID type"))
	}

	log.Printf("[DEBUG] GetWallet: userId=%s", userID)
	return c.JSON(response.Success(fiber.Map{
		"user_id":  userID,
		"balance":  0.0,
		"currency": "VND",
	}))
}

// POST /v1/admin/categories (Admin)
func CreateCategory(c *fiber.Ctx) error {
	var req struct {
		Name      string `json:"name"`
		Icon      string `json:"icon"`
		Color     string `json:"color"`
		SortOrder int    `json:"sort_order"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	id := uuid.New().String()
	_, err := pool.Exec(ctx, `
		INSERT INTO categories (id, name, icon, color, sort_order, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7)
	`, id, req.Name, req.Icon, req.Color, req.SortOrder, time.Now(), time.Now())

	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Category created",
	}))
}

// PUT /v1/admin/categories/:id (Admin)
func UpdateCategory(c *fiber.Ctx) error {
	id := c.Params("id")

	var req struct {
		Name      *string `json:"name"`
		Icon      *string `json:"icon"`
		Color     *string `json:"color"`
		SortOrder *int    `json:"sort_order"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	// Build update query dynamically
	query := "UPDATE categories SET updated_at = $1"
	args := []interface{}{time.Now()}
	argIndex := 2

	if req.Name != nil {
		query += fmt.Sprintf(", name = $%d", argIndex)
		args = append(args, *req.Name)
		argIndex++
	}
	if req.Icon != nil {
		query += fmt.Sprintf(", icon = $%d", argIndex)
		args = append(args, *req.Icon)
		argIndex++
	}
	if req.Color != nil {
		query += fmt.Sprintf(", color = $%d", argIndex)
		args = append(args, *req.Color)
		argIndex++
	}
	if req.SortOrder != nil {
		query += fmt.Sprintf(", sort_order = $%d", argIndex)
		args = append(args, *req.SortOrder)
		argIndex++
	}

	query += fmt.Sprintf(" WHERE id = $%d", argIndex)
	args = append(args, id)

	result, err := pool.Exec(ctx, query, args...)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}

	if result.RowsAffected() == 0 {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Category not found"))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Category updated",
	}))
}

// DELETE /v1/admin/categories/:id (Admin)
func DeleteCategory(c *fiber.Ctx) error {
	id := c.Params("id")
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	result, err := pool.Exec(ctx, `UPDATE categories SET is_active = false WHERE id = $1`, id)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}

	if result.RowsAffected() == 0 {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Category not found"))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Category deleted",
	}))
}

// POST /v1/admin/cafes (Admin)
func CreateCafe(c *fiber.Ctx) error {
	var req struct {
		CafeCode         string  `json:"cafe_code"`
		Name             string  `json:"name"`
		Slug             string  `json:"slug"`
		Address          string  `json:"address"`
		Phone            string  `json:"phone"`
		OpeningTime      *string `json:"opening_time"`
		ClosingTime      *string `json:"closing_time"`
		DeliveryEnabled  bool    `json:"is_delivery_enabled"`
		PickupEnabled    bool    `json:"is_pickup_enabled"`
		ReservationEnabled bool  `json:"is_reservation_enabled"`
		TaxRate          float64 `json:"tax_rate"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	id := uuid.New().String()
	_, err := pool.Exec(ctx, `
		INSERT INTO cafes (id, cafe_code, name, slug, address, phone, opening_time, closing_time,
		                  is_delivery_enabled, is_pickup_enabled, is_reservation_enabled, tax_rate,
		                  is_active, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, true, $13, $14)
	`, id, req.CafeCode, req.Name, req.Slug, req.Address, req.Phone,
		req.OpeningTime, req.ClosingTime, req.DeliveryEnabled, req.PickupEnabled,
		req.ReservationEnabled, req.TaxRate, time.Now(), time.Now())

	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}

	return c.Status(fiber.StatusCreated).JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Cafe created",
	}))
}

// PUT /v1/admin/cafes/:id (Admin)
func UpdateCafe(c *fiber.Ctx) error {
	id := c.Params("id")
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	var req struct {
		Name             *string  `json:"name"`
		Address          *string  `json:"address"`
		Phone            *string  `json:"phone"`
		OpeningTime      *string  `json:"opening_time"`
		ClosingTime      *string  `json:"closing_time"`
		DeliveryEnabled  *bool    `json:"is_delivery_enabled"`
		PickupEnabled   *bool    `json:"is_pickup_enabled"`
		ReservationEnabled *bool  `json:"is_reservation_enabled"`
		TaxRate          *float64 `json:"tax_rate"`
	}
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(response.Error("INVALID_REQUEST", "Invalid request body"))
	}

	query := "UPDATE cafes SET updated_at = $1"
	args := []interface{}{time.Now()}
	argIndex := 2

	if req.Name != nil {
		query += fmt.Sprintf(", name = $%d", argIndex)
		args = append(args, *req.Name)
		argIndex++
	}
	if req.Address != nil {
		query += fmt.Sprintf(", address = $%d", argIndex)
		args = append(args, *req.Address)
		argIndex++
	}
	if req.Phone != nil {
		query += fmt.Sprintf(", phone = $%d", argIndex)
		args = append(args, *req.Phone)
		argIndex++
	}
	if req.OpeningTime != nil {
		query += fmt.Sprintf(", opening_time = $%d", argIndex)
		args = append(args, *req.OpeningTime)
		argIndex++
	}
	if req.ClosingTime != nil {
		query += fmt.Sprintf(", closing_time = $%d", argIndex)
		args = append(args, *req.ClosingTime)
		argIndex++
	}
	if req.DeliveryEnabled != nil {
		query += fmt.Sprintf(", is_delivery_enabled = $%d", argIndex)
		args = append(args, *req.DeliveryEnabled)
		argIndex++
	}
	if req.PickupEnabled != nil {
		query += fmt.Sprintf(", is_pickup_enabled = $%d", argIndex)
		args = append(args, *req.PickupEnabled)
		argIndex++
	}
	if req.ReservationEnabled != nil {
		query += fmt.Sprintf(", is_reservation_enabled = $%d", argIndex)
		args = append(args, *req.ReservationEnabled)
		argIndex++
	}
	if req.TaxRate != nil {
		query += fmt.Sprintf(", tax_rate = $%d", argIndex)
		args = append(args, *req.TaxRate)
		argIndex++
	}

	query += fmt.Sprintf(" WHERE id = $%d", argIndex)
	args = append(args, id)

	result, err := pool.Exec(ctx, query, args...)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}

	if result.RowsAffected() == 0 {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Cafe not found"))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Cafe updated",
	}))
}

// DELETE /v1/admin/cafes/:id (Admin)
func DeleteCafe(c *fiber.Ctx) error {
	id := c.Params("id")
	pool := c.Locals("db").(*pgxpool.Pool)
	ctx := context.Background()

	result, err := pool.Exec(ctx, `UPDATE cafes SET is_active = false WHERE id = $1`, id)
	if err != nil {
		return c.Status(500).JSON(response.Error("DB_ERROR", err.Error()))
	}

	if result.RowsAffected() == 0 {
		return c.Status(404).JSON(response.Error("NOT_FOUND", "Cafe not found"))
	}

	return c.JSON(response.Success(fiber.Map{
		"id":      id,
		"message": "Cafe deleted",
	}))
}
