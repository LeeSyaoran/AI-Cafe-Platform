package dto

import "time"

// ============ ORDER REQUEST DTOs ============

type CreateOrderRequest struct {
	CafeID           string `json:"cafeId" validate:"required,uuid"`
	OrderType        string `json:"orderType" validate:"required,oneof=pickup delivery dine_in"`
	DeliveryAddressID string `json:"deliveryAddressId,omitempty" validate:"omitempty,uuid"`
	CustomerNote     string `json:"customerNote,omitempty"`
	PromoCode        string `json:"promoCode,omitempty"`
	PaymentMethod    string `json:"paymentMethod" validate:"required,oneof=credit card momo vnpay"`
}

type CancelOrderRequest struct {
	Reason string `json:"reason,omitempty"`
}

// ============ ORDER RESPONSE DTOs ============

type OrderResponse struct {
	ID             string            `json:"id"`
	OrderNumber    string            `json:"orderNumber"`
	Status         string            `json:"status"`
	OrderType      string            `json:"orderType"`
	Subtotal       float64           `json:"subtotal"`
	DiscountAmount float64           `json:"discountAmount"`
	TaxAmount      float64           `json:"taxAmount"`
	TotalAmount    float64           `json:"totalAmount"`
	PaymentStatus  string            `json:"paymentStatus"`
	PaymentMethod  string            `json:"paymentMethod,omitempty"`
	CafeID         string            `json:"cafeId"`
	CafeName       string            `json:"cafeName,omitempty"`
	Items          []OrderItemResponse `json:"items"`
	CustomerNote   string            `json:"customerNote,omitempty"`
	CreatedAt      time.Time         `json:"createdAt"`
	UpdatedAt      time.Time         `json:"updatedAt,omitempty"`
	CancelledAt    *time.Time        `json:"cancelledAt,omitempty"`
}

type OrderItemResponse struct {
	ID          string                 `json:"id"`
	ProductID   string                 `json:"productId"`
	ProductName string                 `json:"productName"`
	Quantity    int                    `json:"quantity"`
	UnitPrice   float64                `json:"unitPrice"`
	LineTotal   float64                `json:"lineTotal"`
	Options     map[string]interface{} `json:"options,omitempty"`
	Notes       string                 `json:"notes,omitempty"`
}

type OrderListResponse struct {
	Orders []OrderResponse `json:"orders"`
	Meta   *PaginationMeta `json:"meta"`
}

// ============ PAGINATION ============

type PaginationMeta struct {
	Page       int `json:"page"`
	Limit      int `json:"limit"`
	Total      int `json:"total"`
	TotalPages int `json:"totalPages"`
}
