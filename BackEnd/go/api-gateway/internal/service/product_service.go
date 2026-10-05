package service

import (
	"context"

	"github.com/google/uuid"
	"aicafe-api-gateway/internal/model"
	"aicafe-api-gateway/internal/repository"
)

type ProductService struct {
	repo *repository.PostgresRepo
}

func NewProductService(repo *repository.PostgresRepo) *ProductService {
	return &ProductService{repo: repo}
}

func (s *ProductService) GetProducts(ctx context.Context, companyID uuid.UUID, categoryID *uuid.UUID, search string, limit, offset int) ([]model.Product, error) {
	if limit <= 0 {
		limit = 20
	}
	if limit > 100 {
		limit = 100
	}
	return s.repo.GetProducts(ctx, companyID, categoryID, search, limit, offset)
}

func (s *ProductService) GetProductByID(ctx context.Context, id uuid.UUID) (*model.Product, error) {
	return s.repo.GetProductByID(ctx, id)
}

func (s *ProductService) GetCategories(ctx context.Context, companyID uuid.UUID) ([]model.Category, error) {
	return s.repo.GetCategories(ctx, companyID)
}

type CreditService struct {
	repo *repository.PostgresRepo
}

func NewCreditService(repo *repository.PostgresRepo) *CreditService {
	return &CreditService{repo: repo}
}

func (s *CreditService) GetBalance(ctx context.Context, userID uuid.UUID) (float64, error) {
	// TODO: implement
	return 1000.0, nil
}

func (s *CreditService) Deduct(ctx context.Context, userID uuid.UUID, amount float64) error {
	// TODO: implement
	return nil
}
