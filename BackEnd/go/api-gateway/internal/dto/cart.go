package dto

// ============ CART REQUEST DTOs ============

type AddCartItemRequest struct {
	ProductID string                 `json:"productId" validate:"required,uuid"`
	Quantity  int                    `json:"quantity" validate:"required,min=1,max=99"`
	Options   map[string]interface{} `json:"options,omitempty"`
	Notes     string                 `json:"notes,omitempty"`
}

type UpdateCartItemRequest struct {
	Quantity int                    `json:"quantity" validate:"required,min=0,max=99"`
	Options  map[string]interface{} `json:"options,omitempty"`
	Notes    string                 `json:"notes,omitempty"`
}

type CheckoutRequest struct {
	CafeID           string `json:"cafeId" validate:"required,uuid"`
	OrderType        string `json:"orderType" validate:"required,oneof=pickup delivery dine_in"`
	DeliveryAddressID string `json:"deliveryAddressId,omitempty" validate:"omitempty,uuid"`
	CustomerNote     string `json:"customerNote,omitempty"`
	PromoCode        string `json:"promoCode,omitempty"`
	PaymentMethod    string `json:"paymentMethod" validate:"required,oneof=credit card momo vnpay"`
}

// ============ CART RESPONSE DTOs ============

type CartResponse struct {
	ID        string           `json:"id"`
	UserID    string           `json:"userId"`
	CafeID    string           `json:"cafeId"`
	Items     []CartItemResponse `json:"items"`
	Subtotal  float64         `json:"subtotal"`
	ItemCount int             `json:"itemCount"`
	CreatedAt string           `json:"createdAt"`
	UpdatedAt string           `json:"updatedAt"`
}

type CartItemResponse struct {
	ID          string                 `json:"id"`
	ProductID   string                 `json:"productId"`
	ProductName string                 `json:"productName"`
	Quantity    int                    `json:"quantity"`
	Options     map[string]interface{} `json:"options,omitempty"`
	UnitPrice   float64                `json:"unitPrice"`
	LineTotal   float64                `json:"lineTotal"`
	Notes       string                 `json:"notes,omitempty"`
}
