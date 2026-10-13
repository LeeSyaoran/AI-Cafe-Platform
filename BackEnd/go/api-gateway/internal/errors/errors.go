package errors

import "errors"

// Auth Errors
var (
	ErrOTPNotFound        = errors.New("otp not found")
	ErrOTPExpired        = errors.New("otp expired")
	ErrOTPInvalid        = errors.New("invalid otp code")
	ErrOTPAttemptsExceeded = errors.New("too many otp attempts")
	ErrUserExists        = errors.New("user already exists")
	ErrInvalidCredentials = errors.New("invalid credentials")
	ErrUserNotFound      = errors.New("user not found")
	ErrTokenExpired      = errors.New("token expired")
	ErrTokenInvalid      = errors.New("invalid token")
	ErrTokenBlacklisted  = errors.New("token has been revoked")
)

// Cart Errors
var (
	ErrCartEmpty       = errors.New("cart is empty")
	ErrCartNotFound   = errors.New("cart not found")
	ErrCartItemNotFound = errors.New("cart item not found")
	ErrProductNotFound = errors.New("product not found")
	ErrInvalidQuantity = errors.New("invalid quantity")
)

// Order Errors
var (
	ErrOrderNotFound       = errors.New("order not found")
	ErrOrderCannotCancel   = errors.New("order cannot be cancelled")
	ErrInsufficientCredit  = errors.New("insufficient credit")
	ErrInvalidOrderStatus  = errors.New("invalid order status")
)

// Credit Errors
var (
	ErrCreditNotFound = errors.New("credit account not found")
)

// Common Errors
var (
	ErrInvalidRequest = errors.New("invalid request")
	ErrInternal      = errors.New("internal server error")
)

// ServiceError represents a business logic error with code
type ServiceError struct {
	Code    string
	Message string
	Err     error
}

func (e *ServiceError) Error() string {
	if e.Err != nil {
		return e.Err.Error()
	}
	return e.Message
}

func (e *ServiceError) Unwrap() error {
	return e.Err
}

func New(code, message string) *ServiceError {
	return &ServiceError{Code: code, Message: message}
}

func Wrap(err error, code, message string) *ServiceError {
	return &ServiceError{Code: code, Message: message, Err: err}
}
