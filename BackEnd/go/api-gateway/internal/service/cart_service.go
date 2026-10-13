package service

import (
	"context"
	"time"

	"github.com/google/uuid"

	"aicafe-api-gateway/internal/dto"
	apperrors "aicafe-api-gateway/internal/errors"
	"aicafe-api-gateway/internal/repository"
)

type CartService struct {
	repo *repository.PostgresRepo
}

func NewCartService(repo *repository.PostgresRepo) *CartService {
	return &CartService{repo: repo}
}

// GetCart returns user's cart
func (s *CartService) GetCart(ctx context.Context, userID, cafeID string) (*dto.CartResponse, error) {
	uid, err := uuid.Parse(userID)
	if err != nil {
		return nil, apperrors.New("INVALID_USER_ID", "Invalid user ID")
	}

	cid, err := uuid.Parse(cafeID)
	if err != nil {
		return nil, apperrors.New("INVALID_CAFE_ID", "Invalid cafe ID")
	}

	cart, err := s.repo.GetCart(ctx, uid, cid)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get cart")
	}
	if cart == nil {
		return &dto.CartResponse{
			ID:        "",
			UserID:    userID,
			CafeID:    cafeID,
			Items:     []dto.CartItemResponse{},
			Subtotal:  0,
			ItemCount: 0,
		}, nil
	}

	// Get cart items
	items, err := s.repo.GetCartItems(ctx, cart.ID)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get cart items")
	}

	var itemResponses []dto.CartItemResponse
	for _, item := range items {
		// Get product name
		productName := ""
		product, err := s.repo.GetProductByID(ctx, item.ProductID)
		if err == nil && product != nil {
			productName = product.Name
		}

		itemResponses = append(itemResponses, dto.CartItemResponse{
			ID:          item.ID.String(),
			ProductID:   item.ProductID.String(),
			ProductName: productName,
			Quantity:    item.Quantity,
			UnitPrice:   item.UnitPrice,
			LineTotal:   item.LineTotal,
			Notes:       item.Notes,
		})
	}

	if itemResponses == nil {
		itemResponses = []dto.CartItemResponse{}
	}

	return &dto.CartResponse{
		ID:        cart.ID.String(),
		UserID:    cart.UserID.String(),
		CafeID:    cart.CafeID.String(),
		Items:     itemResponses,
		Subtotal:  cart.Subtotal,
		ItemCount: cart.ItemCount,
		CreatedAt: cart.CreatedAt.Format(time.RFC3339),
		UpdatedAt: cart.UpdatedAt.Format(time.RFC3339),
	}, nil
}

// AddItem adds an item to cart
func (s *CartService) AddItem(ctx context.Context, userID string, req *dto.AddCartItemRequest) (*dto.CartResponse, error) {
	// Parse IDs
	uid, err := uuid.Parse(userID)
	if err != nil {
		return nil, apperrors.New("INVALID_USER_ID", "Invalid user ID")
	}

	productID, err := uuid.Parse(req.ProductID)
	if err != nil {
		return nil, apperrors.New("INVALID_PRODUCT_ID", "Invalid product ID")
	}

	// Validate quantity
	if req.Quantity < 1 {
		return nil, apperrors.ErrInvalidQuantity
	}

	// Get product
	product, err := s.repo.GetProductByID(ctx, productID)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get product")
	}
	if product == nil {
		return nil, apperrors.ErrProductNotFound
	}

	// Get or create cart
	_, err = s.repo.GetCart(ctx, uid, product.CategoryID) // Using category as placeholder
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Failed to get cart")
	}

	// TODO: Implement cart creation and item addition
	// This requires additional repository methods

	return s.GetCart(ctx, userID, product.CategoryID.String())
}

// UpdateItem updates cart item quantity
func (s *CartService) UpdateItem(ctx context.Context, userID, itemID string, req *dto.UpdateCartItemRequest) (*dto.CartResponse, error) {
	// TODO: Implement update logic
	return nil, apperrors.New("NOT_IMPLEMENTED", "Update cart item not yet implemented")
}

// RemoveItem removes item from cart
func (s *CartService) RemoveItem(ctx context.Context, userID, itemID string) error {
	// TODO: Implement remove logic
	return apperrors.New("NOT_IMPLEMENTED", "Remove cart item not yet implemented")
}

// ClearCart clears all items from cart
func (s *CartService) ClearCart(ctx context.Context, userID, cafeID string) error {
	// TODO: Implement clear logic
	return apperrors.New("NOT_IMPLEMENTED", "Clear cart not yet implemented")
}

// Checkout performs checkout
func (s *CartService) Checkout(ctx context.Context, userID string, req *dto.CheckoutRequest) (*dto.OrderResponse, error) {
	// TODO: Implement checkout - delegates to OrderService
	return nil, apperrors.New("NOT_IMPLEMENTED", "Checkout not yet implemented")
}
