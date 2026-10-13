package main

import (
	"log"
	"os"
	"os/signal"
	"syscall"

	"aicafe-api-gateway/internal/config"
	"aicafe-api-gateway/internal/handler"
	"aicafe-api-gateway/internal/middleware"
	"aicafe-api-gateway/internal/repository"
	"aicafe-api-gateway/internal/service"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/gofiber/fiber/v2/middleware/proxy"
	"github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/gofiber/websocket/v2"
)

func main() {
	// Load config
	cfg := config.Load()

	// ==================== INIT REDIS ====================
	redis, err := repository.NewRedisClient(cfg.RedisURL)
	if err != nil {
		log.Printf("[WARN] Failed to connect to Redis: %v. OTP will not work properly.", err)
		// Continue anyway - Redis is optional for MVP
	} else {
		log.Println("Connected to Redis")
		defer redis.Close()
	}

	// ==================== INIT POSTGRES ====================
	repo := repository.NewPostgresRepo(cfg.DatabaseURL)
	defer repo.Close()

	// ==================== INIT SERVICES ====================
	authService := service.NewAuthService(repo, redis, cfg.JWT)
	creditService := service.NewCreditService(repo)

	// ==================== INIT HANDLERS ====================
	authHandler := handler.NewAuthHandler(authService)
	aiHandler := handler.NewAIHandler(creditService)

	// ==================== INIT MIDDLEWARE ====================
	authMiddleware := middleware.NewAuthMiddleware(authService, cfg.JWT)

	// Create Fiber app
	app := fiber.New(fiber.Config{
		AppName:      "AI Café API Gateway",
		ErrorHandler: handler.ErrorHandler,
	})

	// Global middleware
	app.Use(recover.New())
	app.Use(logger.New(logger.Config{
		Format: "[${time}] ${status} - ${latency} ${method} ${path}\n",
	}))
	app.Use(cors.New(cors.Config{
		AllowOrigins:     cfg.CORSOrigins,
		AllowHeaders:     "Origin, Content-Type, Accept, Authorization, X-Company-ID, X-User-ID",
		AllowMethods:     "GET, POST, PUT, PATCH, DELETE, OPTIONS",
		AllowCredentials: true,
	}))

	// Rate limiter
	app.Use(middleware.RateLimiterMiddleware(cfg.RateLimit))

	// Health check
	app.Get("/health", handler.HealthCheck)

	// API version
	app.Get("/v1", handler.APIVersion)

	// WebSocket for AI chat
	app.Use("/ws", middleware.WebSocketUpgrade())
	app.Get("/ws/chat", websocket.New(aiHandler.ChatWebSocket))

	// Helper to proxy requests to microservices
	proxyPass := func(targetHost string) fiber.Handler {
		return func(c *fiber.Ctx) error {
			target := targetHost + c.OriginalURL()
			if err := proxy.Do(c, target); err != nil {
				return c.Status(fiber.StatusBadGateway).JSON(fiber.Map{
					"success":   false,
					"message":   "Service temporarily unavailable: " + err.Error(),
					"errorCode": "SERVICE_UNAVAILABLE",
				})
			}
			c.Response().Header.Del(fiber.HeaderServer)
			return nil
		}
	}

	// ==================== MICROSERVICES PROXY ROUTES ====================
	// 1. Admin Service (Protected with Admin Role)
	app.All("/v1/admin/*", authMiddleware.JWTAuth(), authMiddleware.RequireRole("admin"), proxyPass(cfg.AdminServiceURL))
	app.All("/v1/admin", authMiddleware.JWTAuth(), authMiddleware.RequireRole("admin"), proxyPass(cfg.AdminServiceURL))
	app.All("/admin/*", authMiddleware.JWTAuth(), authMiddleware.RequireRole("admin"), proxyPass(cfg.AdminServiceURL))
	app.All("/admin", authMiddleware.JWTAuth(), authMiddleware.RequireRole("admin"), proxyPass(cfg.AdminServiceURL))

	// 2. Cafe Service (cafes, seats, reservations)
	app.All("/v1/cafes/*", proxyPass(cfg.CafeServiceURL))
	app.All("/v1/cafes", proxyPass(cfg.CafeServiceURL))
	app.All("/v1/seats/*", proxyPass(cfg.CafeServiceURL))
	app.All("/v1/seats", proxyPass(cfg.CafeServiceURL))
	app.All("/v1/reservations/*", proxyPass(cfg.CafeServiceURL))
	app.All("/v1/reservations", proxyPass(cfg.CafeServiceURL))

	// 3. Payment Service
	app.All("/v1/payments/*", proxyPass(cfg.PaymentServiceURL))
	app.All("/v1/payments", proxyPass(cfg.PaymentServiceURL))

	// 4. Member Service (loyalty, subscriptions)
	app.All("/v1/loyalty/*", proxyPass(cfg.MemberServiceURL))
	app.All("/v1/loyalty", proxyPass(cfg.MemberServiceURL))
	app.All("/v1/subscriptions/*", proxyPass(cfg.MemberServiceURL))
	app.All("/v1/subscriptions", proxyPass(cfg.MemberServiceURL))

	// 5. Notification Service
	app.All("/v1/notifications/*", proxyPass(cfg.NotificationServiceURL))
	app.All("/v1/notifications", proxyPass(cfg.NotificationServiceURL))

	// 6. Order Service (products, cart, orders)
	app.All("/v1/products/*", proxyPass(cfg.OrderServiceURL))
	app.All("/v1/products", proxyPass(cfg.OrderServiceURL))
	app.All("/v1/cart/*", proxyPass(cfg.OrderServiceURL))
	app.All("/v1/cart", proxyPass(cfg.OrderServiceURL))
	app.All("/v1/orders/*", proxyPass(cfg.OrderServiceURL))
	app.All("/v1/orders", proxyPass(cfg.OrderServiceURL))

	// 7. User Service
	app.All("/v1/users/*", proxyPass(cfg.UserServiceURL))
	app.All("/v1/users", proxyPass(cfg.UserServiceURL))
	app.All("/v1/me/*", proxyPass(cfg.UserServiceURL))
	app.All("/v1/me", proxyPass(cfg.UserServiceURL))
	app.Post("/v1/auth/register", proxyPass(cfg.UserServiceURL))
	app.Post("/v1/auth/login", proxyPass(cfg.UserServiceURL))
	app.Post("/v1/auth/refresh", proxyPass(cfg.UserServiceURL))
	app.Post("/v1/auth/logout", proxyPass(cfg.UserServiceURL))

	// ==================== NATIVE GATEWAY ROUTES ====================
	v1 := app.Group("/v1")
	auth := v1.Group("/auth")
	auth.Post("/send-otp", authHandler.SendOTP)
	auth.Post("/verify-otp", authHandler.VerifyOTP)

	protected := v1.Group("/", authMiddleware.JWTAuth())
	protected.Get("/wallet", handler.GetWallet)
	protected.Get("/credits/balance", handler.GetCreditBalance)

	// Start server
	go func() {
		addr := ":" + cfg.Port
		log.Printf("Starting AI Café API Gateway on %s", addr)
		if err := app.Listen(addr); err != nil {
			log.Fatalf("Failed to start server: %v", err)
		}
	}()

	// Graceful shutdown
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Println("Shutting down server...")
	if err := app.Shutdown(); err != nil {
		log.Fatalf("Server shutdown failed: %v", err)
	}
}
