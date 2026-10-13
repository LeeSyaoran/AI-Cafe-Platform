package service

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/google/uuid"

	"aicafe-api-gateway/internal/dto"
	apperrors "aicafe-api-gateway/internal/errors"
	"aicafe-api-gateway/internal/model"
	"aicafe-api-gateway/internal/repository"
)

type OrderService struct {
	repo *repository.PostgresRepo
}

func NewOrderService(repo *repository.PostgresRepo) *OrderService {
	return &OrderService{repo: repo}
}

// CreateOrderFromCart creates order from user's cart
func (s *OrderService) CreateOrderFromCart(ctx context.Context, userID string, req *dto.CreateOrderRequest) (*dto.OrderResponse, error) {
	// Parse cafe ID
	cafeID, err := uuid.Parse(req.CafeID)
	if err != nil {
		return nil, apperrors.New("INVALID_CAFE_ID", "Invalid cafe ID")
	}

	// Get cart
	uid, _ := uuid.Parse(userID)
	cart, err := s.repo.GetCart(ctx, uid, cafeID)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get cart")
	}
	if cart == nil {
		return nil, apperrors.ErrCartEmpty
	}

	// Get cart items
	items, err := s.repo.GetCartItems(ctx, cart.ID)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get cart items")
	}
	if len(items) == 0 {
		return nil, apperrors.ErrCartEmpty
	}

	// Get cafe for tax calculation
	cafe, err := s.repo.GetCafeByID(ctx, cafeID)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get cafe")
	}

	// Calculate totals
	taxRate := 0.1 // default 10%
	if cafe != nil {
		taxRate = cafe.TaxRate
	}
	taxAmount := cart.Subtotal * taxRate
	totalAmount := cart.Subtotal + taxAmount

	// Create order
	orderID := uuid.New()
	orderNumber := generateOrderNumber()
	now := time.Now()

	order := &model.Order{
		ID:            orderID,
		OrderNumber:   orderNumber,
		UserID:        uid,
		CompanyID:     cart.CompanyID,
		CafeID:        cafeID,
		OrderType:     req.OrderType,
		Status:        "pending",
		Subtotal:      cart.Subtotal,
		TaxAmount:     taxAmount,
		TotalAmount:   totalAmount,
		PaymentStatus: "pending",
		CustomerNote:  req.CustomerNote,
		CreatedAt:     now,
		UpdatedAt:     now,
	}

	if err := s.repo.CreateOrder(ctx, order); err != nil {
		return nil, apperrors.Wrap(err, "ORDER_CREATE_FAILED", "Failed to create order")
	}

	// Create order items
	var orderItems []dto.OrderItemResponse
	for _, item := range items {
		oi := &model.OrderItem{
			ID:          uuid.New(),
			OrderID:     orderID,
			ProductID:   item.ProductID,
			VariantID:   item.VariantID,
			ProductName: "",
			Quantity:    item.Quantity,
			UnitPrice:   item.UnitPrice,
			LineTotal:   item.LineTotal,
			Notes:       item.Notes,
			ItemStatus:  "pending",
		}

		// Get product name
		product, _ := s.repo.GetProductByID(ctx, item.ProductID)
		if product != nil {
			oi.ProductName = product.Name
		}

		// TODO: Create order item in DB
		// s.repo.CreateOrderItem(ctx, oi)

		orderItems = append(orderItems, dto.OrderItemResponse{
			ID:          oi.ID.String(),
			ProductID:   oi.ProductID.String(),
			ProductName: oi.ProductName,
			Quantity:    oi.Quantity,
			UnitPrice:   oi.UnitPrice,
			LineTotal:   oi.LineTotal,
			Notes:       oi.Notes,
		})
	}

	// Delete cart
	// TODO: s.repo.DeleteCart(ctx, cart.ID)

	return &dto.OrderResponse{
		ID:            order.ID.String(),
		OrderNumber:   order.OrderNumber,
		Status:        order.Status,
		OrderType:     order.OrderType,
		Subtotal:      order.Subtotal,
		TaxAmount:     order.TaxAmount,
		TotalAmount:   order.TotalAmount,
		PaymentStatus: order.PaymentStatus,
		PaymentMethod: req.PaymentMethod,
		CafeID:        order.CafeID.String(),
		CafeName:      "",
		Items:         orderItems,
		CustomerNote:  order.CustomerNote,
		CreatedAt:     order.CreatedAt,
	}, nil
}

// GetOrders returns paginated orders for a user
func (s *OrderService) GetOrders(ctx context.Context, userID string, page, limit int) (*dto.OrderListResponse, error) {
	if page < 1 {
		page = 1
	}
	if limit < 1 || limit > 100 {
		limit = 20
	}

	offset := (page - 1) * limit
	uid := uuid.MustParse(userID)

	orders, err := s.repo.GetOrdersByUser(ctx, uid, limit, offset)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get orders")
	}

	// TODO: Get total count for pagination
	total := len(orders)
	if orders == nil {
		orders = []model.Order{}
	}

	// Convert to response
	var orderResponses []dto.OrderResponse
	for _, order := range orders {
		orderResponses = append(orderResponses, dto.OrderResponse{
			ID:            order.ID.String(),
			OrderNumber:   order.OrderNumber,
			Status:        order.Status,
			OrderType:     order.OrderType,
			Subtotal:      order.Subtotal,
			DiscountAmount: order.DiscountAmount,
			TaxAmount:     order.TaxAmount,
			TotalAmount:   order.TotalAmount,
			PaymentStatus: order.PaymentStatus,
			PaymentMethod: order.PaymentStatus,
			CafeID:        order.CafeID.String(),
			CreatedAt:     order.CreatedAt,
			UpdatedAt:     order.UpdatedAt,
		})
	}

	return &dto.OrderListResponse{
		Orders: orderResponses,
		Meta: &dto.PaginationMeta{
			Page:       page,
			Limit:      limit,
			Total:      total,
			TotalPages: (total + limit - 1) / limit,
		},
	}, nil
}

// GetOrderByID returns a single order
func (s *OrderService) GetOrderByID(ctx context.Context, userID, orderID string) (*dto.OrderResponse, error) {
	uid, err := uuid.Parse(orderID)
	if err != nil {
		return nil, apperrors.New("INVALID_ORDER_ID", "Invalid order ID")
	}

	order, err := s.repo.GetOrderByID(ctx, uid)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get order")
	}
	if order == nil {
		return nil, apperrors.ErrOrderNotFound
	}

	// Verify ownership
	if order.UserID.String() != userID {
		return nil, apperrors.New("FORBIDDEN", "Access denied")
	}

	// TODO: Get order items
	items := []dto.OrderItemResponse{}

	return &dto.OrderResponse{
		ID:            order.ID.String(),
		OrderNumber:   order.OrderNumber,
		Status:        order.Status,
		OrderType:     order.OrderType,
		Subtotal:      order.Subtotal,
		DiscountAmount: order.DiscountAmount,
		TaxAmount:     order.TaxAmount,
		TotalAmount:   order.TotalAmount,
		PaymentStatus: order.PaymentStatus,
		PaymentMethod: order.PaymentStatus,
		CafeID:        order.CafeID.String(),
		Items:         items,
		CustomerNote:  order.CustomerNote,
		CreatedAt:     order.CreatedAt,
		UpdatedAt:     order.UpdatedAt,
	}, nil
}

// CancelOrder cancels an order
func (s *OrderService) CancelOrder(ctx context.Context, userID, orderID string, req *dto.CancelOrderRequest) (*dto.OrderResponse, error) {
	uid, err := uuid.Parse(orderID)
	if err != nil {
		return nil, apperrors.New("INVALID_ORDER_ID", "Invalid order ID")
	}

	order, err := s.repo.GetOrderByID(ctx, uid)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get order")
	}
	if order == nil {
		return nil, apperrors.ErrOrderNotFound
	}

	// Verify ownership
	if order.UserID.String() != userID {
		return nil, apperrors.New("FORBIDDEN", "Access denied")
	}

	// Check if order can be cancelled
	if order.Status != "pending" && order.Status != "confirmed" {
		return nil, apperrors.ErrOrderCannotCancel
	}

	// Update status
	now := time.Now()
	order.Status = "cancelled"
	order.UpdatedAt = now

	if err := s.repo.UpdateOrderStatus(ctx, uid, "cancelled"); err != nil {
		return nil, apperrors.Wrap(err, "UPDATE_FAILED", "Failed to cancel order")
	}

	// TODO: Refund credit if paid
	// if order.PaymentStatus == "paid" {
	//     s.creditService.Refund(ctx, userID, order.TotalAmount, order.ID.String())
	// }

	return &dto.OrderResponse{
		ID:            order.ID.String(),
		OrderNumber:   order.OrderNumber,
		Status:        order.Status,
		OrderType:     order.OrderType,
		Subtotal:      order.Subtotal,
		DiscountAmount: order.DiscountAmount,
		TaxAmount:     order.TaxAmount,
		TotalAmount:   order.TotalAmount,
		PaymentStatus: order.PaymentStatus,
		CafeID:        order.CafeID.String(),
		CustomerNote:  order.CustomerNote,
		CreatedAt:     order.CreatedAt,
		UpdatedAt:     order.UpdatedAt,
		CancelledAt:   &now,
	}, nil
}

// Checkout performs checkout from cart
func (s *OrderService) Checkout(ctx context.Context, userID string, req *dto.CheckoutRequest) (*dto.OrderResponse, error) {
	// Convert to CreateOrderRequest
	createReq := &dto.CreateOrderRequest{
		CafeID:           req.CafeID,
		OrderType:        req.OrderType,
		DeliveryAddressID: req.DeliveryAddressID,
		CustomerNote:     req.CustomerNote,
		PromoCode:        req.PromoCode,
		PaymentMethod:    req.PaymentMethod,
	}

	return s.CreateOrderFromCart(ctx, userID, createReq)
}

// ============ Helper Functions ============

func generateOrderNumber() string {
	timestamp := time.Now().UnixNano() / 1000000 // milliseconds
	return fmt.Sprintf("ORD%d", timestamp)
}

func parseJSONOptions(jsonStr string) (map[string]interface{}, error) {
	if jsonStr == "" {
		return nil, nil
	}
	var result map[string]interface{}
	if err := json.Unmarshal([]byte(jsonStr), &result); err != nil {
		return nil, err
	}
	return result, nil
}
