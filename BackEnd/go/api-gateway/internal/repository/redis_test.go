package repository

import (
	"context"
	"testing"
	"time"
)

// TestRedisClient tests the Redis client operations
// Note: These tests require a running Redis instance
// Skip if Redis is not available

func TestRedisOTPOperations(t *testing.T) {
	// Skip if REDIS_URL is not set
	redisURL := "redis://localhost:6379"

	client, err := NewRedisClient(redisURL)
	if err != nil {
		t.Skipf("Redis not available: %v", err)
	}
	defer client.Close()

	ctx := context.Background()
	phone := "test-phone-123"
	otp := "123456"

	// Test SetOTP
	err = client.SetOTP(ctx, phone, otp, 5*time.Minute)
	if err != nil {
		t.Fatalf("SetOTP failed: %v", err)
	}

	// Test GetOTP
	storedOTP, err := client.GetOTP(ctx, phone)
	if err != nil {
		t.Fatalf("GetOTP failed: %v", err)
	}
	if storedOTP != otp {
		t.Errorf("Expected OTP '%s', got '%s'", otp, storedOTP)
	}

	// Test DeleteOTP
	err = client.DeleteOTP(ctx, phone)
	if err != nil {
		t.Fatalf("DeleteOTP failed: %v", err)
	}

	// Verify deletion
	deletedOTP, _ := client.GetOTP(ctx, phone)
	if deletedOTP != "" {
		t.Error("OTP should be deleted")
	}
}

func TestRedisOTPAttempts(t *testing.T) {
	redisURL := "redis://localhost:6379"

	client, err := NewRedisClient(redisURL)
	if err != nil {
		t.Skipf("Redis not available: %v", err)
	}
	defer client.Close()

	ctx := context.Background()
	phone := "test-phone-attempts"

	// Test increment
	attempts1, err := client.IncrementOTPAttempts(ctx, phone)
	if err != nil {
		t.Fatalf("IncrementOTPAttempts failed: %v", err)
	}
	if attempts1 != 1 {
		t.Errorf("Expected 1 attempt, got %d", attempts1)
	}

	// Increment again
	attempts2, err := client.IncrementOTPAttempts(ctx, phone)
	if err != nil {
		t.Fatalf("IncrementOTPAttempts failed: %v", err)
	}
	if attempts2 != 2 {
		t.Errorf("Expected 2 attempts, got %d", attempts2)
	}

	// Test reset
	err = client.ResetOTPAttempts(ctx, phone)
	if err != nil {
		t.Fatalf("ResetOTPAttempts failed: %v", err)
	}

	// Verify reset
	attempts3, _ := client.GetOTPAttempts(ctx, phone)
	if attempts3 != 0 {
		t.Errorf("Expected 0 attempts after reset, got %d", attempts3)
	}
}

func TestRedisOTPLockout(t *testing.T) {
	redisURL := "redis://localhost:6379"

	client, err := NewRedisClient(redisURL)
	if err != nil {
		t.Skipf("Redis not available: %v", err)
	}
	defer client.Close()

	ctx := context.Background()
	phone := "test-phone-lock"

	// Initially not locked
	locked, _ := client.IsOTPLocked(ctx, phone)
	if locked {
		t.Error("Phone should not be locked initially")
	}

	// Lock
	err = client.LockOTP(ctx, phone, 1*time.Minute)
	if err != nil {
		t.Fatalf("LockOTP failed: %v", err)
	}

	// Verify locked
	locked, _ = client.IsOTPLocked(ctx, phone)
	if !locked {
		t.Error("Phone should be locked")
	}
}

func TestRedisBlacklist(t *testing.T) {
	redisURL := "redis://localhost:6379"

	client, err := NewRedisClient(redisURL)
	if err != nil {
		t.Skipf("Redis not available: %v", err)
	}
	defer client.Close()

	ctx := context.Background()
	tokenID := "test-token-id"

	// Initially not blacklisted
	blacklisted, _ := client.IsBlacklisted(ctx, tokenID)
	if blacklisted {
		t.Error("Token should not be blacklisted initially")
	}

	// Add to blacklist
	err = client.AddToBlacklist(ctx, tokenID, 1*time.Hour)
	if err != nil {
		t.Fatalf("AddToBlacklist failed: %v", err)
	}

	// Verify blacklisted
	blacklisted, _ = client.IsBlacklisted(ctx, tokenID)
	if !blacklisted {
		t.Error("Token should be blacklisted")
	}
}

func TestRedisCache(t *testing.T) {
	redisURL := "redis://localhost:6379"

	client, err := NewRedisClient(redisURL)
	if err != nil {
		t.Skipf("Redis not available: %v", err)
	}
	defer client.Close()

	ctx := context.Background()
	key := "test-key"
	value := "test-value"

	// Set cache
	err = client.SetCache(ctx, key, value, 5*time.Minute)
	if err != nil {
		t.Fatalf("SetCache failed: %v", err)
	}

	// Get cache
	stored, err := client.GetCache(ctx, key)
	if err != nil {
		t.Fatalf("GetCache failed: %v", err)
	}
	if stored != value {
		t.Errorf("Expected '%s', got '%s'", value, stored)
	}

	// Delete cache
	err = client.DeleteCache(ctx, key)
	if err != nil {
		t.Fatalf("DeleteCache failed: %v", err)
	}
}
