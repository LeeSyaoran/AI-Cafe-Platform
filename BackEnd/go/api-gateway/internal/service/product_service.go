package service

import (
	"context"

	"github.com/google/uuid"

	"aicafe-api-gateway/internal/dto"
	appapperrors "aicafe-api-gateway/internal/errors"
	"aicafe-api-gateway/internal/model"
	"aicafe-api-gateway/internal/repository"
)

type ProductService struct {
	repo *repository.PostgresRepo
}

func NewProductService(repo *repository.PostgresRepo) *ProductService {
	return &ProductService{repo: repo}
}

// GetProducts returns paginated products
func (s *ProductService) GetProducts(ctx context.Context, companyID string, params *dto.ProductQueryParams) (*dto.ProductListResponse, error) {
	// Default values
	if params.Page < 1 {
		params.Page = 1
	}
	if params.Limit < 1 || params.Limit > 100 {
		params.Limit = 20
	}

	offset := (params.Page - 1) * params.Limit

	// Parse category ID if provided
	var categoryID *uuid.UUID
	if params.CategoryID != "" {
		cid, err := uuid.Parse(params.CategoryID)
		if err != nil {
			return nil, appapperrors.New("INVALID_CATEGORY_ID", "Invalid category ID")
		}
		categoryID = &cid
	}

	// Get company UUID
	companyUUID, err := uuid.Parse(companyID)
	if err != nil {
		return nil, appapperrors.New("INVALID_COMPANY_ID", "Invalid company ID")
	}

	// Get products
	products, err := s.repo.GetProducts(ctx, companyUUID, categoryID, params.Search, params.Limit, offset)
	if err != nil {
		return nil, appapperrors.Wrap(err, "DB_ERROR", "Failed to get products")
	}

	// Convert to response
	var productResponses []dto.ProductResponse
	for _, p := range products {
		productResponses = append(productResponses, convertProductToResponse(p))
	}

	if productResponses == nil {
		productResponses = []dto.ProductResponse{}
	}

	// TODO: Get total count for proper pagination
	total := len(productResponses)

	return &dto.ProductListResponse{
		Products: productResponses,
		Meta: &dto.PaginationMeta{
			Page:       params.Page,
			Limit:      params.Limit,
			Total:      total,
			TotalPages: (total + params.Limit - 1) / params.Limit,
		},
	}, nil
}

// GetProductByID returns a single product
func (s *ProductService) GetProductByID(ctx context.Context, productID string) (*dto.ProductResponse, error) {
	id, err := uuid.Parse(productID)
	if err != nil {
		return nil, appapperrors.New("INVALID_PRODUCT_ID", "Invalid product ID")
	}

	product, err := s.repo.GetProductByID(ctx, id)
	if err != nil {
		return nil, appapperrors.Wrap(err, "DB_ERROR", "Failed to get product")
	}
	if product == nil {
		return nil, appapperrors.New("PRODUCT_NOT_FOUND", "Product not found")
	}

	resp := convertProductToResponse(*product)
	return &resp, nil
}

// GetCategories returns all categories
func (s *ProductService) GetCategories(ctx context.Context, companyID string) (*dto.CategoryListResponse, error) {
	companyUUID, err := uuid.Parse(companyID)
	if err != nil {
		return nil, appapperrors.New("INVALID_COMPANY_ID", "Invalid company ID")
	}

	categories, err := s.repo.GetCategories(ctx, companyUUID)
	if err != nil {
		return nil, appapperrors.Wrap(err, "DB_ERROR", "Failed to get categories")
	}

	var categoryResponses []dto.CategoryResponse
	for _, c := range categories {
		categoryResponses = append(categoryResponses, dto.CategoryResponse{
			ID:         c.ID.String(),
			Name:       c.Name,
			Slug:       c.Slug,
			Icon:       c.Icon,
			Color:      c.Color,
			SortOrder:  c.SortOrder,
			IsFeatured: c.IsFeatured,
		})
	}

	if categoryResponses == nil {
		categoryResponses = []dto.CategoryResponse{}
	}

	return &dto.CategoryListResponse{
		Categories: categoryResponses,
	}, nil
}

// GetFeaturedProducts returns featured products
func (s *ProductService) GetFeaturedProducts(ctx context.Context, companyID string, limit int) (*dto.ProductListResponse, error) {
	if limit < 1 || limit > 50 {
		limit = 10
	}

	companyUUID, err := uuid.Parse(companyID)
	if err != nil {
		return nil, appapperrors.New("INVALID_COMPANY_ID", "Invalid company ID")
	}

	// Get featured products
	products, err := s.repo.GetProducts(ctx, companyUUID, nil, "", limit, 0)
	if err != nil {
		return nil, appapperrors.Wrap(err, "DB_ERROR", "Failed to get featured products")
	}

	var productResponses []dto.ProductResponse
	for _, p := range products {
		if p.IsFeatured || p.IsBestSeller {
			productResponses = append(productResponses, convertProductToResponse(p))
		}
	}

	if productResponses == nil {
		productResponses = []dto.ProductResponse{}
	}

	return &dto.ProductListResponse{
		Products: productResponses,
		Meta: &dto.PaginationMeta{
			Page:       1,
			Limit:      limit,
			Total:      len(productResponses),
			TotalPages: 1,
		},
	}, nil
}

// ============ Helper Functions ============

func convertProductToResponse(p model.Product) dto.ProductResponse {
	return dto.ProductResponse{
		ID:           p.ID.String(),
		Name:         p.Name,
		NameVi:       p.NameVi,
		Description:  p.Description,
		ShortDesc:    p.ShortDesc,
		Price:        p.Price,
		Images:       p.Images,
		Calories:     p.Calories,
		CategoryID:   p.CategoryID.String(),
		CategoryName: "",
		SKU:          p.SKU,
		IsFeatured:   p.IsFeatured,
		IsBestSeller: p.IsBestSeller,
		PrepTime:     p.PreparationTime,
		CreatedAt:    p.CreatedAt.Format("2006-01-02T15:04:05Z"),
	}
}
