package repository

import (
	"context"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

// RedisClient wraps Redis client with app-specific methods
type RedisClient struct {
	client *redis.Client
}

// NewRedisClient creates a new Redis client
func NewRedisClient(redisURL string) (*RedisClient, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     redisURL,
		Password: "", // no password
		DB:       0,  // default DB
	})

	// Test connection
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := client.Ping(ctx).Err(); err != nil {
		return nil, fmt.Errorf("failed to connect to Redis: %w", err)
	}

	return &RedisClient{client: client}, nil
}

// Close closes the Redis connection
func (r *RedisClient) Close() error {
	return r.client.Close()
}

// ============ OTP Operations ============

const (
	otpPrefix    = "otp:"
	otpAttempts  = "otp_attempts:"
	otpLock      = "otp_lock:"
)

// SetOTP stores OTP with expiry
func (r *RedisClient) SetOTP(ctx context.Context, phone, otp string, expiry time.Duration) error {
	key := otpPrefix + phone
	return r.client.Set(ctx, key, otp, expiry).Err()
}

// GetOTP retrieves stored OTP
func (r *RedisClient) GetOTP(ctx context.Context, phone string) (string, error) {
	key := otpPrefix + phone
	return r.client.Get(ctx, key).Result()
}

// DeleteOTP removes OTP
func (r *RedisClient) DeleteOTP(ctx context.Context, phone string) error {
	key := otpPrefix + phone
	return r.client.Del(ctx, key).Err()
}

// GetOTPAttempts returns current failed attempts count
func (r *RedisClient) GetOTPAttempts(ctx context.Context, phone string) (int, error) {
	key := otpAttempts + phone
	val, err := r.client.Get(ctx, key).Int()
	if err == redis.Nil {
		return 0, nil
	}
	return val, err
}

// IncrementOTPAttempts increments and returns new count
func (r *RedisClient) IncrementOTPAttempts(ctx context.Context, phone string) (int, error) {
	key := otpAttempts + phone
	pipe := r.client.Pipeline()
	incr := pipe.Incr(ctx, key)
	pipe.Expire(ctx, key, 15*time.Minute)
	_, err := pipe.Exec(ctx)
	if err != nil {
		return 0, err
	}
	return int(incr.Val()), nil
}

// ResetOTPAttempts resets failed attempts
func (r *RedisClient) ResetOTPAttempts(ctx context.Context, phone string) error {
	key := otpAttempts + phone
	return r.client.Del(ctx, key).Err()
}

// LockOTP locks phone after too many failed attempts
func (r *RedisClient) LockOTP(ctx context.Context, phone string, duration time.Duration) error {
	key := otpLock + phone
	return r.client.Set(ctx, key, "locked", duration).Err()
}

// IsOTPLocked checks if phone is locked
func (r *RedisClient) IsOTPLocked(ctx context.Context, phone string) (bool, error) {
	key := otpLock + phone
	exists, err := r.client.Exists(ctx, key).Result()
	return exists > 0, err
}

// ============ Token Blacklist Operations ============

const tokenBlacklistPrefix = "token_blacklist:"

// AddToBlacklist adds token to blacklist
func (r *RedisClient) AddToBlacklist(ctx context.Context, tokenID string, expiry time.Duration) error {
	key := tokenBlacklistPrefix + tokenID
	return r.client.Set(ctx, key, "blacklisted", expiry).Err()
}

// IsBlacklisted checks if token is blacklisted
func (r *RedisClient) IsBlacklisted(ctx context.Context, tokenID string) (bool, error) {
	key := tokenBlacklistPrefix + tokenID
	exists, err := r.client.Exists(ctx, key).Result()
	return exists > 0, err
}

// ============ Rate Limiting Operations ============

const rateLimitPrefix = "rate:"

// IncrementRateLimit increments counter and returns count
func (r *RedisClient) IncrementRateLimit(ctx context.Context, key string, window time.Duration) (int64, error) {
	fullKey := rateLimitPrefix + key
	pipe := r.client.Pipeline()
	incr := pipe.Incr(ctx, fullKey)
	pipe.Expire(ctx, fullKey, window)
	_, err := pipe.Exec(ctx)
	if err != nil {
		return 0, err
	}
	return incr.Val(), nil
}

// GetRateLimitCount returns current count
func (r *RedisClient) GetRateLimitCount(ctx context.Context, key string) (int64, error) {
	fullKey := rateLimitPrefix + key
	val, err := r.client.Get(ctx, fullKey).Int64()
	if err == redis.Nil {
		return 0, nil
	}
	return val, err
}

// ============ Session/Cache Operations ============

const sessionPrefix = "session:"

// SetSession stores session data
func (r *RedisClient) SetSession(ctx context.Context, sessionID string, data interface{}, expiry time.Duration) error {
	key := sessionPrefix + sessionID
	return r.client.Set(ctx, key, data, expiry).Err()
}

// GetSession retrieves session data
func (r *RedisClient) GetSession(ctx context.Context, sessionID string) (string, error) {
	key := sessionPrefix + sessionID
	return r.client.Get(ctx, key).Result()
}

// DeleteSession removes session
func (r *RedisClient) DeleteSession(ctx context.Context, sessionID string) error {
	key := sessionPrefix + sessionID
	return r.client.Del(ctx, key).Err()
}

// ============ Generic Cache Operations ============

const cachePrefix = "cache:"

// SetCache stores cache with key prefix
func (r *RedisClient) SetCache(ctx context.Context, key string, value interface{}, expiry time.Duration) error {
	fullKey := cachePrefix + key
	return r.client.Set(ctx, fullKey, value, expiry).Err()
}

// GetCache retrieves cache
func (r *RedisClient) GetCache(ctx context.Context, key string) (string, error) {
	fullKey := cachePrefix + key
	return r.client.Get(ctx, fullKey).Result()
}

// DeleteCache removes cache entry
func (r *RedisClient) DeleteCache(ctx context.Context, key string) error {
	fullKey := cachePrefix + key
	return r.client.Del(ctx, fullKey).Err()
}

// DeleteCachePattern removes all keys matching pattern
func (r *RedisClient) DeleteCachePattern(ctx context.Context, pattern string) error {
	fullPattern := cachePrefix + pattern
	iter := r.client.Scan(ctx, 0, fullPattern, 0).Iterator()
	var keys []string
	for iter.Next(ctx) {
		keys = append(keys, iter.Val())
	}
	if err := iter.Err(); err != nil {
		return err
	}
	if len(keys) > 0 {
		return r.client.Del(ctx, keys...).Err()
	}
	return nil
}
