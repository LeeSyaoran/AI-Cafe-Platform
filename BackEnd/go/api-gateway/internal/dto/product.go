package dto

// ============ PRODUCT REQUEST DTOs ============

type ProductQueryParams struct {
	CategoryID string `query:"categoryId"`
	Search     string `query:"search"`
	MinPrice   string `query:"minPrice"`
	MaxPrice   string `query:"maxPrice"`
	SortBy     string `query:"sortBy"`
	SortOrder  string `query:"sortOrder"`
	Page       int    `query:"page"`
	Limit      int    `query:"limit"`
}

// ============ PRODUCT RESPONSE DTOs ============

type ProductResponse struct {
	ID           string                  `json:"id"`
	Name         string                  `json:"name"`
	NameVi       string                  `json:"nameVi,omitempty"`
	Description  string                  `json:"description,omitempty"`
	ShortDesc    string                  `json:"shortDesc,omitempty"`
	Price        float64                 `json:"price"`
	Images       []string                `json:"images"`
	Calories     int                     `json:"calories,omitempty"`
	CategoryID   string                  `json:"categoryId"`
	CategoryName string                  `json:"categoryName,omitempty"`
	SKU          string                  `json:"sku,omitempty"`
	IsFeatured   bool                    `json:"isFeatured"`
	IsBestSeller bool                    `json:"isBestSeller"`
	PrepTime     int                     `json:"prepTime,omitempty"`
	Options      []ProductOptionResponse `json:"options,omitempty"`
	CreatedAt    string                  `json:"createdAt,omitempty"`
}

type ProductOptionResponse struct {
	ID     string               `json:"id"`
	Name   string              `json:"name"`
	Type   string              `json:"type"` // single, multiple
	Min    int                 `json:"min,omitempty"`
	Max    int                 `json:"max,omitempty"`
	Values []ProductOptionValue `json:"values"`
}

type ProductOptionValue struct {
	ID    string  `json:"id"`
	Name  string  `json:"name"`
	Price float64 `json:"price,omitempty"`
}

type ProductListResponse struct {
	Products []ProductResponse `json:"products"`
	Meta     *PaginationMeta  `json:"meta"`
}

type CategoryResponse struct {
	ID        string   `json:"id"`
	Name      string   `json:"name"`
	Slug      string   `json:"slug,omitempty"`
	Icon      string   `json:"icon,omitempty"`
	Color     string   `json:"color,omitempty"`
	SortOrder int      `json:"sortOrder"`
	IsFeatured bool    `json:"isFeatured"`
}

type CategoryListResponse struct {
	Categories []CategoryResponse `json:"categories"`
}
