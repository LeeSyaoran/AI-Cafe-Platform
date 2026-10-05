package repository

import (
	"context"
	"fmt"
	"time"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"aicafe-api-gateway/internal/model"
)

type PostgresRepo struct {
	pool *pgxpool.Pool
}

func NewPostgresRepo(databaseURL string) *PostgresRepo {
	config, err := pgxpool.ParseConfig(databaseURL)
	if err != nil {
		panic(fmt.Sprintf("Failed to parse database URL: %v", err))
	}

	config.MaxConns = 20
	config.MinConns = 5

	pool, err := pgxpool.NewWithConfig(context.Background(), config)
	if err != nil {
		panic(fmt.Sprintf("Failed to connect to database: %v", err))
	}

	return &PostgresRepo{pool: pool}
}

func (r *PostgresRepo) Close() {
	r.pool.Close()
}

// User operations
func (r *PostgresRepo) CreateUser(ctx context.Context, user *model.User) error {
	query := `
		INSERT INTO users (id, email, password_hash, phone, role, status, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
	`
	_, err := r.pool.Exec(ctx, query, user.ID, user.Email, user.PasswordHash, user.Phone, user.Role, user.Status, user.CreatedAt, user.UpdatedAt)
	return err
}

func (r *PostgresRepo) GetUserByEmail(ctx context.Context, email string) (*model.User, error) {
	query := `
		SELECT id, email, password_hash, phone, role, status, created_at, updated_at
		FROM users WHERE email = $1
	`
	var user model.User
	err := r.pool.QueryRow(ctx, query, email).Scan(
		&user.ID, &user.Email, &user.PasswordHash, &user.Phone,
		&user.Role, &user.Status, &user.CreatedAt, &user.UpdatedAt,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	return &user, err
}

func (r *PostgresRepo) GetUserByID(ctx context.Context, id uuid.UUID) (*model.User, error) {
	query := `
		SELECT id, email, password_hash, phone, role, status, created_at, updated_at
		FROM users WHERE id = $1
	`
	var user model.User
	err := r.pool.QueryRow(ctx, query, id).Scan(
		&user.ID, &user.Email, &user.PasswordHash, &user.Phone,
		&user.Role, &user.Status, &user.CreatedAt, &user.UpdatedAt,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	return &user, err
}

// Company operations
func (r *PostgresRepo) GetCompanyByID(ctx context.Context, id uuid.UUID) (*model.Company, error) {
	query := `
		SELECT id, company_code, name, legal_name, tax_id, address, phone, email, status, created_at, updated_at
		FROM companies WHERE id = $1
	`
	var c model.Company
	err := r.pool.QueryRow(ctx, query, id).Scan(
		&c.ID, &c.CompanyCode, &c.Name, &c.LegalName, &c.TaxID,
		&c.Address, &c.Phone, &c.Email, &c.Status, &c.CreatedAt, &c.UpdatedAt,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	return &c, err
}

// Cafe operations
func (r *PostgresRepo) GetCafes(ctx context.Context, companyID uuid.UUID) ([]model.Cafe, error) {
	query := `
		SELECT id, company_id, region_id, cafe_code, name, slug, description, address, phone,
		       is_active, is_delivery_enabled, is_pickup_enabled, tax_rate, created_at
		FROM cafes WHERE company_id = $1 AND is_active = true
		ORDER BY name
	`
	rows, err := r.pool.Query(ctx, query, companyID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var cafes []model.Cafe
	for rows.Next() {
		var c model.Cafe
		err := rows.Scan(
			&c.ID, &c.CompanyID, &c.RegionID, &c.CafeCode, &c.Name, &c.Slug,
			&c.Description, &c.Address, &c.Phone, &c.IsActive, &c.IsDeliveryEnabled,
			&c.IsPickupEnabled, &c.TaxRate, &c.CreatedAt,
		)
		if err != nil {
			return nil, err
		}
		cafes = append(cafes, c)
	}
	return cafes, nil
}

func (r *PostgresRepo) GetCafeByID(ctx context.Context, id uuid.UUID) (*model.Cafe, error) {
	query := `
		SELECT id, company_id, region_id, cafe_code, name, slug, description, address, phone,
		       is_active, is_delivery_enabled, is_pickup_enabled, tax_rate, created_at
		FROM cafes WHERE id = $1
	`
	var c model.Cafe
	err := r.pool.QueryRow(ctx, query, id).Scan(
		&c.ID, &c.CompanyID, &c.RegionID, &c.CafeCode, &c.Name, &c.Slug,
		&c.Description, &c.Address, &c.Phone, &c.IsActive, &c.IsDeliveryEnabled,
		&c.IsPickupEnabled, &c.TaxRate, &c.CreatedAt,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	return &c, err
}

// Category operations
func (r *PostgresRepo) GetCategories(ctx context.Context, companyID uuid.UUID) ([]model.Category, error) {
	query := `
		SELECT id, company_id, name, slug, icon, color, sort_order, is_featured, is_active
		FROM categories WHERE company_id = $1 AND is_active = true
		ORDER BY sort_order, name
	`
	rows, err := r.pool.Query(ctx, query, companyID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var categories []model.Category
	for rows.Next() {
		var c model.Category
		err := rows.Scan(&c.ID, &c.CompanyID, &c.Name, &c.Slug, &c.Icon, &c.Color, &c.SortOrder, &c.IsFeatured, &c.IsActive)
		if err != nil {
			return nil, err
		}
		categories = append(categories, c)
	}
	return categories, nil
}

// Product operations
func (r *PostgresRepo) GetProducts(ctx context.Context, companyID uuid.UUID, categoryID *uuid.UUID, search string, limit, offset int) ([]model.Product, error) {
	query := `
		SELECT p.id, p.company_id, p.category_id, p.name, p.name_vi, p.slug, p.sku,
		       p.description, p.short_description, p.price, p.images, p.calories, p.preparation_time,
		       p.is_active, p.is_featured, p.is_best_seller, p.created_at, p.updated_at,
		       c.id, c.name, c.slug, c.icon, c.color
		FROM products p
		LEFT JOIN categories c ON p.category_id = c.id
		WHERE p.company_id = $1 AND p.is_active = true
	`
	args := []interface{}{companyID}
	argIndex := 2

	if categoryID != nil {
		query += fmt.Sprintf(" AND p.category_id = $%d", argIndex)
		args = append(args, *categoryID)
		argIndex++
	}
	if search != "" {
		query += fmt.Sprintf(" AND (p.name ILIKE $%d OR p.name_vi ILIKE $%d)", argIndex, argIndex)
		args = append(args, "%"+search+"%")
		argIndex++
	}

	query += fmt.Sprintf(" ORDER BY p.is_best_seller DESC, p.name LIMIT $%d OFFSET $%d", argIndex, argIndex+1)
	args = append(args, limit, offset)

	rows, err := r.pool.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var products []model.Product
	for rows.Next() {
		var p model.Product
		var shortDesc, images *string
		var cat model.Category
		err := rows.Scan(
			&p.ID, &p.CompanyID, &p.CategoryID, &p.Name, &p.NameVi, &p.Slug, &p.SKU,
			&p.Description, &shortDesc, &p.Price, &images, &p.Calories, &p.PreparationTime,
			&p.IsActive, &p.IsFeatured, &p.IsBestSeller, &p.CreatedAt, &p.UpdatedAt,
			&cat.ID, &cat.Name, &cat.Slug, &cat.Icon, &cat.Color,
		)
		if err != nil {
			return nil, err
		}
		if shortDesc != nil {
			p.ShortDesc = *shortDesc
		}
		p.Category = &cat
		products = append(products, p)
	}
	return products, nil
}

func (r *PostgresRepo) GetProductByID(ctx context.Context, id uuid.UUID) (*model.Product, error) {
	query := `
		SELECT p.id, p.company_id, p.category_id, p.name, p.name_vi, p.slug, p.sku,
		       p.description, p.short_description, p.price, p.images, p.calories, p.preparation_time,
		       p.is_active, p.is_featured, p.is_best_seller, p.created_at, p.updated_at,
		       c.id, c.name, c.slug, c.icon, c.color
		FROM products p
		LEFT JOIN categories c ON p.category_id = c.id
		WHERE p.id = $1
	`
	var p model.Product
	var shortDesc, images *string
	var cat model.Category
	err := r.pool.QueryRow(ctx, query, id).Scan(
		&p.ID, &p.CompanyID, &p.CategoryID, &p.Name, &p.NameVi, &p.Slug, &p.SKU,
		&p.Description, &shortDesc, &p.Price, &images, &p.Calories, &p.PreparationTime,
		&p.IsActive, &p.IsFeatured, &p.IsBestSeller, &p.CreatedAt, &p.UpdatedAt,
		&cat.ID, &cat.Name, &cat.Slug, &cat.Icon, &cat.Color,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	if shortDesc != nil {
		p.ShortDesc = *shortDesc
	}
	p.Category = &cat
	return &p, nil
}

// Order operations
func (r *PostgresRepo) CreateOrder(ctx context.Context, order *model.Order) error {
	query := `
		INSERT INTO orders (id, order_number, user_id, company_id, cafe_id, seat_id, order_type,
		                   status, subtotal, discount_amount, tax_amount, total_amount, total_paid,
		                   payment_status, customer_note, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17)
	`
	_, err := r.pool.Exec(ctx, query,
		order.ID, order.OrderNumber, order.UserID, order.CompanyID, order.CafeID, order.SeatID,
		order.OrderType, order.Status, order.Subtotal, order.DiscountAmount, order.TaxAmount,
		order.TotalAmount, order.TotalPaid, order.PaymentStatus, order.CustomerNote,
		order.CreatedAt, order.UpdatedAt,
	)
	return err
}

func (r *PostgresRepo) GetOrderByID(ctx context.Context, id uuid.UUID) (*model.Order, error) {
	query := `
		SELECT id, order_number, user_id, company_id, cafe_id, seat_id, order_type,
		       status, subtotal, discount_amount, tax_amount, total_amount, total_paid,
		       payment_status, customer_note, created_at, updated_at
		FROM orders WHERE id = $1
	`
	var o model.Order
	err := r.pool.QueryRow(ctx, query, id).Scan(
		&o.ID, &o.OrderNumber, &o.UserID, &o.CompanyID, &o.CafeID, &o.SeatID,
		&o.OrderType, &o.Status, &o.Subtotal, &o.DiscountAmount, &o.TaxAmount,
		&o.TotalAmount, &o.TotalPaid, &o.PaymentStatus, &o.CustomerNote,
		&o.CreatedAt, &o.UpdatedAt,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	return &o, err
}

func (r *PostgresRepo) UpdateOrderStatus(ctx context.Context, id uuid.UUID, status string) error {
	query := `UPDATE orders SET status = $1, updated_at = $2 WHERE id = $3`
	_, err := r.pool.Exec(ctx, query, status, time.Now(), id)
	return err
}

func (r *PostgresRepo) GetOrdersByUser(ctx context.Context, userID uuid.UUID, limit, offset int) ([]model.Order, error) {
	query := `
		SELECT id, order_number, user_id, company_id, cafe_id, seat_id, order_type,
		       status, subtotal, discount_amount, tax_amount, total_amount, total_paid,
		       payment_status, customer_note, created_at, updated_at
		FROM orders WHERE user_id = $1
		ORDER BY created_at DESC
		LIMIT $2 OFFSET $3
	`
	rows, err := r.pool.Query(ctx, query, userID, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var orders []model.Order
	for rows.Next() {
		var o model.Order
		err := rows.Scan(
			&o.ID, &o.OrderNumber, &o.UserID, &o.CompanyID, &o.CafeID, &o.SeatID,
			&o.OrderType, &o.Status, &o.Subtotal, &o.DiscountAmount, &o.TaxAmount,
			&o.TotalAmount, &o.TotalPaid, &o.PaymentStatus, &o.CustomerNote,
			&o.CreatedAt, &o.UpdatedAt,
		)
		if err != nil {
			return nil, err
		}
		orders = append(orders, o)
	}
	return orders, nil
}

// Cart operations
func (r *PostgresRepo) GetCart(ctx context.Context, userID, cafeID uuid.UUID) (*model.Cart, error) {
	query := `
		SELECT id, user_id, company_id, cafe_id, subtotal, item_count, expires_at, created_at, updated_at
		FROM carts WHERE user_id = $1 AND cafe_id = $2
	`
	var c model.Cart
	err := r.pool.QueryRow(ctx, query, userID, cafeID).Scan(
		&c.ID, &c.UserID, &c.CompanyID, &c.CafeID, &c.Subtotal, &c.ItemCount, &c.ExpiresAt, &c.CreatedAt, &c.UpdatedAt,
	)
	if err == pgx.ErrNoRows {
		return nil, nil
	}
	return &c, err
}

func (r *PostgresRepo) CreateCart(ctx context.Context, cart *model.Cart) error {
	query := `
		INSERT INTO carts (id, user_id, company_id, cafe_id, subtotal, item_count, expires_at, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
	`
	_, err := r.pool.Exec(ctx, query,
		cart.ID, cart.UserID, cart.CompanyID, cart.CafeID, cart.Subtotal, cart.ItemCount, cart.ExpiresAt, cart.CreatedAt, cart.UpdatedAt,
	)
	return err
}

func (r *PostgresRepo) GetCartItems(ctx context.Context, cartID uuid.UUID) ([]model.CartItem, error) {
	query := `
		SELECT id, cart_id, product_id, variant_id, quantity, options_json, modifiers_json,
		       unit_price, line_total, notes, created_at
		FROM cart_items WHERE cart_id = $1
	`
	rows, err := r.pool.Query(ctx, query, cartID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var items []model.CartItem
	for rows.Next() {
		var item model.CartItem
		err := rows.Scan(
			&item.ID, &item.CartID, &item.ProductID, &item.VariantID, &item.Quantity,
			&item.OptionsJSON, &item.ModifiersJSON, &item.UnitPrice, &item.LineTotal, &item.Notes, &item.CreatedAt,
		)
		if err != nil {
			return nil, err
		}
		items = append(items, item)
	}
	return items, nil
}
