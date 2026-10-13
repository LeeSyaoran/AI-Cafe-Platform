package dto

// ============ CREDIT REQUEST DTOs ============

type TopUpRequest struct {
	Amount        float64 `json:"amount" validate:"required,gt=0"`
	PaymentMethod string  `json:"paymentMethod" validate:"required,oneof=card momo vnpay"`
	PaymentID     string  `json:"paymentId,omitempty"`
}

// ============ CREDIT RESPONSE DTOs ============

type CreditBalanceResponse struct {
	UserID        string  `json:"userId"`
	Balance       float64 `json:"balance"`
	PendingBalance float64 `json:"pendingBalance,omitempty"`
}

type CreditTransactionResponse struct {
	ID            string    `json:"id"`
	UserID        string    `json:"userId"`
	Amount        float64   `json:"amount"`
	BalanceAfter  float64   `json:"balanceAfter"`
	Type          string    `json:"type"`
	ReferenceID   string    `json:"referenceId,omitempty"`
	Description   string    `json:"description,omitempty"`
	CreatedAt     string    `json:"createdAt"`
}

type CreditTransactionListResponse struct {
	Transactions []CreditTransactionResponse `json:"transactions"`
	Meta        *PaginationMeta            `json:"meta"`
}
