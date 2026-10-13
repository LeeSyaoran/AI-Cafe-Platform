package service

import (
	"context"

	"github.com/google/uuid"

	"aicafe-api-gateway/internal/dto"
	appapperrors "aicafe-api-gateway/internal/errors"
	"aicafe-api-gateway/internal/repository"
)

type CreditService struct {
	repo *repository.PostgresRepo
}

func NewCreditService(repo *repository.PostgresRepo) *CreditService {
	return &CreditService{repo: repo}
}

// GetBalance returns user's credit balance
func (s *CreditService) GetBalance(ctx context.Context, userID string) (*dto.CreditBalanceResponse, error) {
	uid, err := uuid.Parse(userID)
	if err != nil {
		return nil, appapperrors.New("INVALID_USER_ID", "Invalid user ID")
	}

	balance, err := s.repo.GetCreditBalance(ctx, uid)
	if err != nil {
		return nil, appapperrors.Wrap(err, "DB_ERROR", "Failed to get balance")
	}

	return &dto.CreditBalanceResponse{
		UserID:        userID,
		Balance:       balance,
		PendingBalance: 0, // TODO: Add pending balance
	}, nil
}

// TopUp adds credit to user account
func (s *CreditService) TopUp(ctx context.Context, userID string, req *dto.TopUpRequest) (*dto.CreditTransactionResponse, error) {
	// TODO: Implement with payment gateway
	return nil, appapperrors.New("NOT_IMPLEMENTED", "Top-up not yet implemented")
}

// Deduct deducts credit from user account
func (s *CreditService) Deduct(ctx context.Context, userID string, amount float64, referenceID, description string) error {
	// TODO: Implement with transaction
	return appapperrors.New("NOT_IMPLEMENTED", "Deduct not yet implemented")
}

// GetTransactions returns paginated transactions
func (s *CreditService) GetTransactions(ctx context.Context, userID string, page, limit int) (*dto.CreditTransactionListResponse, error) {
	if page < 1 {
		page = 1
	}
	if limit < 1 || limit > 100 {
		limit = 20
	}

	uid, err := uuid.Parse(userID)
	if err != nil {
		return nil, appapperrors.New("INVALID_USER_ID", "Invalid user ID")
	}

	transactions, err := s.repo.GetCreditTransactions(ctx, uid, limit, (page-1)*limit)
	if err != nil {
		return nil, appapperrors.Wrap(err, "DB_ERROR", "Failed to get transactions")
	}

	var responses []dto.CreditTransactionResponse
	for _, tx := range transactions {
		responses = append(responses, dto.CreditTransactionResponse{
			ID:           tx.ID.String(),
			UserID:       tx.UserID.String(),
			Amount:       tx.Amount,
			BalanceAfter: tx.BalanceAfter,
			Type:         tx.Type,
			ReferenceID:  tx.ReferenceID,
			Description:  tx.Description,
			CreatedAt:    tx.CreatedAt.Format("2006-01-02T15:04:05Z"),
		})
	}

	if responses == nil {
		responses = []dto.CreditTransactionResponse{}
	}

	return &dto.CreditTransactionListResponse{
		Transactions: responses,
		Meta: &dto.PaginationMeta{
			Page:       page,
			Limit:      limit,
			Total:      len(responses),
			TotalPages: (len(responses) + limit - 1) / limit,
		},
	}, nil
}
