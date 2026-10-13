package response

import "net/http"

// ============ API Response Structures ============

type APIResponse struct {
	Success bool        `json:"success"`
	Data    interface{} `json:"data,omitempty"`
	Error   *APIError  `json:"error,omitempty"`
	Meta    *Meta      `json:"meta,omitempty"`
}

type APIError struct {
	Code    string      `json:"code"`
	Message string      `json:"message"`
	Details interface{} `json:"details,omitempty"`
	TraceID string     `json:"traceId,omitempty"`
}

type Meta struct {
	Page       int `json:"page,omitempty"`
	Limit      int `json:"limit,omitempty"`
	Total      int `json:"total,omitempty"`
	TotalPages int `json:"totalPages,omitempty"`
}

// ============ Factory Methods ============

func Success(data interface{}) APIResponse {
	return APIResponse{
		Success: true,
		Data:    data,
	}
}

func SuccessWithMeta(data interface{}, page, limit, total int) APIResponse {
	totalPages := 0
	if total > 0 && limit > 0 {
		totalPages = (total + limit - 1) / limit
	}
	return APIResponse{
		Success: true,
		Data:    data,
		Meta: &Meta{
			Page:       page,
			Limit:      limit,
			Total:      total,
			TotalPages: totalPages,
		},
	}
}

func Error(code, message string) *APIError {
	return &APIError{
		Code:    code,
		Message: message,
	}
}

func ErrorWithDetails(code, message string, details interface{}) *APIError {
	return &APIError{
		Code:    code,
		Message: message,
		Details: details,
	}
}

func ErrorWithTrace(code, message, traceID string) *APIError {
	return &APIError{
		Code:    code,
		Message: message,
		TraceID: traceID,
	}
}

// ============ HTTP Status Mapping ============

func GetStatusCode(errCode string) int {
	switch errCode {
	case "INVALID_REQUEST", "VALIDATION_ERROR":
		return http.StatusBadRequest
	case "UNAUTHORIZED", "TOKEN_EXPIRED", "TOKEN_INVALID", "TOKEN_BLACKLISTED":
		return http.StatusUnauthorized
	case "FORBIDDEN":
		return http.StatusForbidden
	case "NOT_FOUND", "USER_NOT_FOUND", "ORDER_NOT_FOUND", "PRODUCT_NOT_FOUND", "CART_NOT_FOUND":
		return http.StatusNotFound
	case "CONFLICT", "USER_EXISTS":
		return http.StatusConflict
	case "TOO_MANY_REQUESTS", "OTP_ATTEMPTS_EXCEEDED":
		return http.StatusTooManyRequests
	default:
		return http.StatusInternalServerError
	}
}
