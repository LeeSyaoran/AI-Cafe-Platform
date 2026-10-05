package model

import (
	"time"

	"github.com/google/uuid"
)

type User struct {
	ID           uuid.UUID  `json:"id"`
	Email        string    `json:"email"`
	PasswordHash string    `json:"-"`
	Phone        string    `json:"phone,omitempty"`
	Role         string    `json:"role"`
	Status       string    `json:"status"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

type UserProfile struct {
	UserID      uuid.UUID `json:"userId"`
	FullName    string    `json:"fullName"`
	DisplayName string    `json:"displayName"`
	AvatarURL   string    `json:"avatarUrl"`
	DateOfBirth string    `json:"dateOfBirth"`
	Gender      string    `json:"gender"`
	Language    string    `json:"language"`
	Timezone    string    `json:"timezone"`
}

type Company struct {
	ID           uuid.UUID `json:"id"`
	CompanyCode  string    `json:"companyCode"`
	Name         string    `json:"name"`
	LegalName    string    `json:"legalName"`
	TaxID        string    `json:"taxId"`
	Address      string    `json:"address"`
	Phone        string    `json:"phone"`
	Email        string    `json:"email"`
	Status       string    `json:"status"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

type Cafe struct {
	ID                uuid.UUID `json:"id"`
	CompanyID         uuid.UUID `json:"companyId"`
	RegionID          uuid.UUID `json:"regionId"`
	CafeCode          string    `json:"cafeCode"`
	Name              string    `json:"name"`
	Slug              string    `json:"slug"`
	Description       string    `json:"description"`
	Address           string    `json:"address"`
	Phone             string    `json:"phone"`
	IsActive          bool      `json:"isActive"`
	IsDeliveryEnabled bool      `json:"isDeliveryEnabled"`
	IsPickupEnabled   bool      `json:"isPickupEnabled"`
	TaxRate           float64   `json:"taxRate"`
	CreatedAt         time.Time `json:"createdAt"`
}

type Category struct {
	ID        uuid.UUID `json:"id"`
	CompanyID uuid.UUID `json:"companyId"`
	Name      string    `json:"name"`
	Slug      string    `json:"slug"`
	Icon      string    `json:"icon"`
	Color     string    `json:"color"`
	SortOrder int       `json:"sortOrder"`
	IsFeatured bool     `json:"isFeatured"`
	IsActive   bool     `json:"isActive"`
}

type Product struct {
	ID              uuid.UUID  `json:"id"`
	CompanyID       uuid.UUID  `json:"companyId"`
	CategoryID      uuid.UUID  `json:"categoryId"`
	Name            string     `json:"name"`
	NameVi          string     `json:"nameVi"`
	Slug            string     `json:"slug"`
	SKU             string     `json:"sku"`
	Description     string     `json:"description"`
	ShortDesc      string     `json:"shortDesc"`
	Price           float64    `json:"price"`
	Images          []string   `json:"images"`
	Calories        int        `json:"calories"`
	PreparationTime int        `json:"preparationTime"`
	IsActive        bool       `json:"isActive"`
	IsFeatured      bool       `json:"isFeatured"`
	IsBestSeller    bool       `json:"isBestSeller"`
	CreatedAt       time.Time  `json:"createdAt"`
	UpdatedAt       time.Time  `json:"updatedAt"`
	Category        *Category  `json:"category,omitempty"`
}

type Order struct {
	ID            uuid.UUID  `json:"id"`
	OrderNumber   string     `json:"orderNumber"`
	UserID        uuid.UUID  `json:"userId"`
	CompanyID     uuid.UUID  `json:"companyId"`
	CafeID        uuid.UUID  `json:"cafeId"`
	SeatID        *uuid.UUID `json:"seatId,omitempty"`
	OrderType     string     `json:"orderType"`
	Status        string     `json:"status"`
	Subtotal      float64    `json:"subtotal"`
	DiscountAmount float64    `json:"discountAmount"`
	TaxAmount     float64    `json:"taxAmount"`
	TotalAmount   float64    `json:"totalAmount"`
	TotalPaid     float64    `json:"totalPaid"`
	PaymentStatus string     `json:"paymentStatus"`
	CustomerNote  string     `json:"customerNote"`
	CreatedAt     time.Time  `json:"createdAt"`
	UpdatedAt     time.Time  `json:"updatedAt"`
}

type OrderItem struct {
	ID           uuid.UUID `json:"id"`
	OrderID      uuid.UUID `json:"orderId"`
	ProductID    uuid.UUID `json:"productId"`
	VariantID    uuid.UUID `json:"variantId,omitempty"`
	ProductName  string    `json:"productName"`
	Quantity     int       `json:"quantity"`
	UnitPrice    float64   `json:"unitPrice"`
	OptionsJSON  string    `json:"optionsJson"`
	ModifiersJSON string   `json:"modifiersJson"`
	Notes        string    `json:"notes"`
	LineTotal   float64   `json:"lineTotal"`
	ItemStatus   string    `json:"itemStatus"`
}

type Cart struct {
	ID         uuid.UUID  `json:"id"`
	UserID     uuid.UUID  `json:"userId"`
	CompanyID  uuid.UUID  `json:"companyId"`
	CafeID     uuid.UUID  `json:"cafeId"`
	Subtotal   float64    `json:"subtotal"`
	ItemCount  int        `json:"itemCount"`
	ExpiresAt  *time.Time `json:"expiresAt,omitempty"`
	CreatedAt  time.Time  `json:"createdAt"`
	UpdatedAt  time.Time  `json:"updatedAt"`
}

type CartItem struct {
	ID           uuid.UUID `json:"id"`
	CartID      uuid.UUID `json:"cartId"`
	ProductID    uuid.UUID `json:"productId"`
	VariantID    uuid.UUID `json:"variantId,omitempty"`
	Quantity     int       `json:"quantity"`
	OptionsJSON  string    `json:"optionsJson"`
	ModifiersJSON string   `json:"modifiersJson"`
	UnitPrice    float64   `json:"unitPrice"`
	LineTotal   float64   `json:"lineTotal"`
	Notes        string    `json:"notes"`
	CreatedAt   time.Time  `json:"createdAt"`
}
