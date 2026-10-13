package service

import (
	"context"
	"crypto/rand"
	"math/big"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"

	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/internal/dto"
	apperrors "aicafe-api-gateway/internal/errors"
	"aicafe-api-gateway/internal/model"
	"aicafe-api-gateway/internal/repository"
)

const (
	OTPExpiry      = 5 * time.Minute
	MaxOTPAttempts = 3
	OTPWindow      = 15 * time.Minute
	ResendCooldown = 60 * time.Second
)

type AuthService struct {
	repo  *repository.PostgresRepo
	redis *repository.RedisClient
	jwt   config.JWTConfig
}

func NewAuthService(repo *repository.PostgresRepo, redis *repository.RedisClient, jwt config.JWTConfig) *AuthService {
	return &AuthService{
		repo:  repo,
		redis: redis,
		jwt:   jwt,
	}
}

// ============ OTP Operations ============

func (s *AuthService) SendOTP(ctx context.Context, phone string) (*dto.OTPSentResponse, error) {
	// Check if phone is locked
	locked, err := s.redis.IsOTPLocked(ctx, phone)
	if err == nil && locked {
		return nil, apperrors.ErrOTPAttemptsExceeded
	}

	// Generate secure 6-digit OTP
	otp, err := generateSecureOTP()
	if err != nil {
		return nil, apperrors.Wrap(err, "OTP_ERROR", "Failed to generate OTP")
	}

	// Store OTP in Redis with expiry
	if err := s.redis.SetOTP(ctx, phone, otp, OTPExpiry); err != nil {
		return nil, apperrors.Wrap(err, "OTP_SEND_FAILED", "Failed to store OTP")
	}

	// TODO: Send SMS via external service (Twilio, Vonage, etc.)
	// Example: sendSMS(phone, otp)

	return &dto.OTPSentResponse{
		Message:           "OTP sent successfully",
		ExpiresIn:        int(OTPExpiry.Seconds()),
		ResendAvailableIn: int(ResendCooldown.Seconds()),
	}, nil
}

func (s *AuthService) VerifyOTP(ctx context.Context, phone, code string) (*dto.AuthResponse, error) {
	// Check if phone is locked
	locked, err := s.redis.IsOTPLocked(ctx, phone)
	if err == nil && locked {
		return nil, apperrors.ErrOTPAttemptsExceeded
	}

	// Get stored OTP
	storedOTP, err := s.redis.GetOTP(ctx, phone)
	if err != nil {
		return nil, apperrors.ErrOTPNotFound
	}

	// Increment attempts
	attempts, _ := s.redis.IncrementOTPAttempts(ctx, phone)

	// Check if too many attempts
	if attempts > MaxOTPAttempts {
		s.redis.LockOTP(ctx, phone, OTPWindow)
		s.redis.DeleteOTP(ctx, phone)
		s.redis.ResetOTPAttempts(ctx, phone)
		return nil, apperrors.ErrOTPAttemptsExceeded
	}

	// Verify code
	if storedOTP != code {
		return nil, apperrors.ErrOTPInvalid
	}

	// Success! Clean up OTP and attempts
	s.redis.DeleteOTP(ctx, phone)
	s.redis.ResetOTPAttempts(ctx, phone)

	// Find or create user
	user, err := s.repo.FindOrCreateUserByPhone(ctx, phone)
	if err != nil {
		return nil, apperrors.Wrap(err, "USER_ERROR", "Failed to create user")
	}

	// Generate tokens
	return s.generateAuthResponse(ctx, user)
}

// ============ Registration & Login ============

func (s *AuthService) Register(ctx context.Context, req *dto.RegisterRequest) (*dto.AuthResponse, error) {
	// Check if user exists
	existing, err := s.repo.GetUserByEmail(ctx, req.Email)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Database error")
	}
	if existing != nil {
		return nil, apperrors.ErrUserExists
	}

	// Hash password with bcrypt
	passwordHash, err := hashPassword(req.Password)
	if err != nil {
		return nil, apperrors.Wrap(err, "HASH_ERROR", "Failed to hash password")
	}

	// Create user
	user := &model.User{
		ID:           uuid.New(),
		Email:        req.Email,
		PasswordHash: passwordHash,
		Name:         req.Name,
		Phone:        req.Phone,
		Role:         "customer",
		Status:       "active",
		Tier:         "bronze",
		CreatedAt:    time.Now(),
		UpdatedAt:    time.Now(),
	}

	if err := s.repo.CreateUser(ctx, user); err != nil {
		return nil, apperrors.Wrap(err, "USER_CREATE_FAILED", "Failed to create user")
	}

	return s.generateAuthResponse(ctx, user)
}

func (s *AuthService) Login(ctx context.Context, req *dto.LoginRequest) (*dto.AuthResponse, error) {
	user, err := s.repo.GetUserByEmail(ctx, req.Email)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Database error")
	}
	if user == nil {
		return nil, apperrors.ErrInvalidCredentials
	}

	// Verify password with bcrypt
	if !verifyPassword(req.Password, user.PasswordHash) {
		return nil, apperrors.ErrInvalidCredentials
	}

	return s.generateAuthResponse(ctx, user)
}

// ============ Token Operations ============

func (s *AuthService) RefreshToken(ctx context.Context, refreshToken string) (*dto.TokenRefreshResponse, error) {
	claims, err := s.ValidateToken(refreshToken)
	if err != nil {
		return nil, apperrors.ErrTokenInvalid
	}

	// Check token type
	if claims.Type != "refresh" {
		return nil, apperrors.ErrTokenInvalid
	}

	// Check if token is blacklisted
	blacklisted, _ := s.redis.IsBlacklisted(ctx, claims.TokenID)
	if blacklisted {
		return nil, apperrors.ErrTokenInvalid
	}

	// Get user
	user, err := s.repo.GetUserByIDString(ctx, claims.UserID)
	if err != nil || user == nil {
		return nil, apperrors.ErrUserNotFound
	}

	// Generate new access token
	accessToken, err := s.generateAccessToken(user)
	if err != nil {
		return nil, apperrors.Wrap(err, "TOKEN_ERROR", "Failed to generate token")
	}

	return &dto.TokenRefreshResponse{
		AccessToken: accessToken,
		ExpiresIn:  s.jwt.AccessTokenExpiration,
		TokenType:  "Bearer",
	}, nil
}

func (s *AuthService) Logout(ctx context.Context, tokenID string, expiry time.Duration) error {
	// Add token to blacklist
	return s.redis.AddToBlacklist(ctx, tokenID, expiry)
}

func (s *AuthService) ValidateToken(tokenString string) (*TokenClaims, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, apperrors.ErrTokenInvalid
		}
		return []byte(s.jwt.Secret), nil
	})

	if err != nil || !token.Valid {
		return nil, apperrors.ErrTokenInvalid
	}

	mapClaims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return nil, apperrors.ErrTokenInvalid
	}

	userID := getStringClaim(mapClaims, "user_id")
	if userID == "" {
		userID = getStringClaim(mapClaims, "sub")
	}

	return &TokenClaims{
		UserID:   userID,
		Email:    getStringClaim(mapClaims, "email"),
		Phone:    getStringClaim(mapClaims, "phone"),
		Role:     getStringClaim(mapClaims, "role"),
		Tier:     getStringClaim(mapClaims, "tier"),
		Type:     getStringClaim(mapClaims, "type"),
		TokenID:  getStringClaim(mapClaims, "jti"),
		Issuer:   getStringClaim(mapClaims, "iss"),
		IssuedAt: time.Unix(int64(getFloatClaim(mapClaims, "iat")), 0),
		Expiry:   time.Unix(int64(getFloatClaim(mapClaims, "exp")), 0),
	}, nil
}

// ============ Profile Operations ============

func (s *AuthService) GetProfile(ctx context.Context, userID string) (*dto.UserResponse, error) {
	user, err := s.repo.GetUserByIDString(ctx, userID)
	if err != nil {
		return nil, apperrors.Wrap(err, "DB_ERROR", "Database error")
	}
	if user == nil {
		return nil, apperrors.ErrUserNotFound
	}

	return &dto.UserResponse{
		ID:            user.ID.String(),
		Email:         user.Email,
		Phone:         user.Phone,
		Name:          user.Name,
		Role:          user.Role,
		Tier:          user.Tier,
		EmailVerified: user.EmailVerified,
		PhoneVerified: user.PhoneVerified,
		CreatedAt:     user.CreatedAt,
		UpdatedAt:     user.UpdatedAt,
	}, nil
}

func (s *AuthService) UpdateProfile(ctx context.Context, userID string, req *dto.UpdateProfileRequest) (*dto.UserResponse, error) {
	// Update via repository
	if err := s.repo.UpdateUser(ctx, userID, req.Name, ""); err != nil {
		return nil, apperrors.Wrap(err, "UPDATE_FAILED", "Failed to update profile")
	}

	// Fetch updated user
	user, err := s.repo.GetUserByIDString(ctx, userID)
	if err != nil || user == nil {
		return nil, apperrors.ErrUserNotFound
	}

	return &dto.UserResponse{
		ID:            user.ID.String(),
		Email:         user.Email,
		Phone:         user.Phone,
		Name:          user.Name,
		Role:          user.Role,
		Tier:          user.Tier,
		EmailVerified: user.EmailVerified,
		PhoneVerified: user.PhoneVerified,
		CreatedAt:     user.CreatedAt,
		UpdatedAt:     user.UpdatedAt,
	}, nil
}

// ============ Token Generation ============

type TokenClaims struct {
	UserID   string
	Email    string
	Phone    string
	Role     string
	Tier     string
	Type     string
	TokenID  string
	Issuer   string
	IssuedAt time.Time
	Expiry   time.Time
}

func (s *AuthService) generateAuthResponse(ctx context.Context, user *model.User) (*dto.AuthResponse, error) {
	// Generate access token
	accessToken, err := s.generateAccessToken(user)
	if err != nil {
		return nil, apperrors.Wrap(err, "TOKEN_ERROR", "Failed to generate access token")
	}

	// Generate refresh token
	refreshToken, err := s.generateRefreshToken(user)
	if err != nil {
		return nil, apperrors.Wrap(err, "TOKEN_ERROR", "Failed to generate refresh token")
	}

	return &dto.AuthResponse{
		Tokens: &dto.TokensResponse{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
			ExpiresIn:   int64(s.jwt.AccessTokenExpiration),
			TokenType:   "Bearer",
		},
		User: &dto.UserResponse{
			ID:            user.ID.String(),
			Email:         user.Email,
			Phone:         user.Phone,
			Name:          user.Name,
			Role:          user.Role,
			Tier:          user.Tier,
			EmailVerified: user.EmailVerified,
			PhoneVerified: user.PhoneVerified,
		},
	}, nil
}

func (s *AuthService) generateAccessToken(user *model.User) (string, error) {
	tokenID := uuid.New().String()
	claims := jwt.MapClaims{
		"jti":     tokenID,
		"user_id": user.ID.String(),
		"email":   user.Email,
		"phone":   user.Phone,
		"role":    user.Role,
		"tier":    user.Tier,
		"type":    "access",
		"iss":     s.jwt.Issuer,
		"iat":     time.Now().Unix(),
		"exp":     time.Now().Add(time.Duration(s.jwt.AccessTokenExpiration) * time.Second).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(s.jwt.Secret))
}

func (s *AuthService) generateRefreshToken(user *model.User) (string, error) {
	tokenID := uuid.New().String()
	claims := jwt.MapClaims{
		"jti":     tokenID,
		"user_id": user.ID.String(),
		"type":    "refresh",
		"iss":     s.jwt.Issuer,
		"iat":     time.Now().Unix(),
		"exp":     time.Now().Add(time.Duration(s.jwt.RefreshTokenExpiration) * time.Second).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(s.jwt.Secret))
}

// ============ Helper Functions ============

// generateSecureOTP generates a cryptographically secure 6-digit OTP
func generateSecureOTP() (string, error) {
	const digits = "0123456789"
	result := make([]byte, 6)
	for i := range result {
		num, err := rand.Int(rand.Reader, big.NewInt(int64(len(digits))))
		if err != nil {
			return "", err
		}
		result[i] = digits[num.Int64()]
	}
	return string(result), nil
}

// hashPassword hashes password with bcrypt
func hashPassword(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	return string(bytes), err
}

// verifyPassword verifies password against hash
func verifyPassword(password, hash string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
	return err == nil
}

// getStringClaim safely extracts string claim
func getStringClaim(claims jwt.MapClaims, key string) string {
	if val, ok := claims[key].(string); ok {
		return val
	}
	return ""
}

// getFloatClaim safely extracts float64 claim
func getFloatClaim(claims jwt.MapClaims, key string) float64 {
	if val, ok := claims[key].(float64); ok {
		return val
	}
	return 0
}
