package middleware

import (
	"sync"
	"time"

	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/pkg/response"

	"github.com/gofiber/fiber/v2"
)

type RateLimiter struct {
	requests map[string][]time.Time
	mu       sync.RWMutex
	capacity int
	duration time.Duration
}

func NewRateLimiter(cfg config.RateLimitConfig) *RateLimiter {
	rl := &RateLimiter{
		requests: make(map[string][]time.Time),
		capacity: cfg.Capacity,
		duration: time.Duration(cfg.Duration) * time.Second,
	}

	// Cleanup goroutine
	go func() {
		ticker := time.NewTicker(time.Minute)
		for range ticker.C {
			rl.cleanup()
		}
	}()

	return rl
}

func (rl *RateLimiter) cleanup() {
	rl.mu.Lock()
	defer rl.mu.Unlock()
	now := time.Now()
	for ip, times := range rl.requests {
		var valid []time.Time
		for _, t := range times {
			if now.Sub(t) < rl.duration {
				valid = append(valid, t)
			}
		}
		if len(valid) == 0 {
			delete(rl.requests, ip)
		} else {
			rl.requests[ip] = valid
		}
	}
}

func (rl *RateLimiter) Allow(ip string) bool {
	rl.mu.Lock()
	defer rl.mu.Unlock()

	now := time.Now()
	windowStart := now.Add(-rl.duration)

	// Filter old requests
	var valid []time.Time
	for _, t := range rl.requests[ip] {
		if t.After(windowStart) {
			valid = append(valid, t)
		}
	}

	if len(valid) >= rl.capacity {
		rl.requests[ip] = valid
		return false
	}

	valid = append(valid, now)
	rl.requests[ip] = valid
	return true
}

func RateLimiterMiddleware(cfg config.RateLimitConfig) fiber.Handler {
	rl := NewRateLimiter(cfg)

	return func(c *fiber.Ctx) error {
		ip := c.IP()

		if !rl.Allow(ip) {
			return c.Status(fiber.StatusTooManyRequests).JSON(response.Error("RATE_LIMIT_EXCEEDED", "Too many requests. Please try again later."))
		}

		return c.Next()
	}
}
