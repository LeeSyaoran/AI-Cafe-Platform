package main

import (
	"context"
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
	"github.com/gofiber/fiber/v2/middleware/recover"
	"github.com/gofiber/websocket/v2"
	"github.com/jackc/pgx/v5/pgxpool"
)

func main() {
	// Load config
	cfg := config.Load()

	// Create database pool for direct queries
	dbPool, err := pgxpool.New(context.Background(), cfg.DatabaseURL)
	if err != nil {
		log.Fatalf("Failed to create database pool: %v", err)
	}
	defer dbPool.Close()

	// Test connection
	if err := dbPool.Ping(context.Background()); err != nil {
		log.Fatalf("Failed to ping database: %v", err)
	}
	log.Println("Connected to database")

	// Init repository
	repo := repository.NewPostgresRepo(cfg.DatabaseURL)
	defer repo.Close()

	// Init services
	authService := service.NewAuthService(repo, cfg.JWT)
	_ = service.NewProductService(repo) // Initialize if needed
	creditService := service.NewCreditService(repo)

	// Init handlers
	authHandler := handler.NewAuthHandler(repo, cfg.JWT)
	productHandler := handler.NewProductHandler(dbPool)
	orderHandler := handler.NewOrderHandler(repo)
	cartHandler := handler.NewCartHandler(repo)
	aiHandler := handler.NewAIHandler(creditService)

	// Init middleware
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

	// Database middleware - attach db pool to all requests
	app.Use(func(c *fiber.Ctx) error {
		c.Locals("db", dbPool)
		return c.Next()
	})

	// Health check
	app.Get("/health", handler.HealthCheck)

	// API version
	app.Get("/v1", handler.APIVersion)

	// WebSocket for AI chat
	app.Use("/ws", middleware.WebSocketUpgrade())
	app.Get("/ws/chat", websocket.New(aiHandler.ChatWebSocket))

	// ==================== PUBLIC ROUTES ====================
	v1 := app.Group("/v1")

	// Auth routes (OTP-based)
	auth := v1.Group("/auth")
	auth.Post("/send-otp", authHandler.SendOTP)
	auth.Post("/verify-otp", authHandler.VerifyOTP)
	auth.Post("/refresh", authHandler.RefreshToken)

	// Menu / Products (public)
	v1.Get("/menu", handler.GetMenu)
	v1.Get("/cafes", handler.ListCafes)
	v1.Get("/cafes/:id", handler.GetCafe)
	v1.Get("/categories", handler.ListCategories)
	v1.Get("/categories/:id", handler.GetCategory)

	products := v1.Group("/products")
	products.Get("/", productHandler.ListProducts)
	products.Get("/featured", productHandler.GetFeaturedProducts)
	products.Get("/best-sellers", productHandler.GetBestSellers)
	products.Get("/:id", productHandler.GetProduct)

	// ==================== PROTECTED ROUTES ====================
	protected := v1.Group("/", authMiddleware.JWTAuth())

	// Auth (protected)
	protected.Post("/auth/logout", authHandler.Logout)
	protected.Get("/me", authHandler.GetProfile)
	protected.Put("/me", authHandler.UpdateProfile)

	// Cart
	cart := protected.Group("/cart")
	cart.Get("/", cartHandler.GetCart)
	cart.Post("/items", cartHandler.AddItem)
	cart.Put("/items/:id", cartHandler.UpdateItem)
	cart.Delete("/items/:id", cartHandler.RemoveItem)
	cart.Post("/clear", cartHandler.ClearCart)
	cart.Post("/checkout", cartHandler.Checkout)

	// Orders
	orders := protected.Group("/orders")
	orders.Get("/", orderHandler.ListOrders)
	orders.Post("/", orderHandler.CreateOrder)
	orders.Get("/:id", orderHandler.GetOrder)
	orders.Put("/:id/cancel", orderHandler.CancelOrder)

	// Wallet & Credits
	protected.Get("/wallet", handler.GetWallet)
	protected.Get("/credits/balance", handler.GetCreditBalance)

	// ==================== ADMIN ROUTES ====================
	admin := protected.Group("/admin")
	admin.Use(authMiddleware.RequireRole("admin", "manager"))

	adminProducts := admin.Group("/products")
	adminProducts.Post("/", productHandler.CreateProduct)
	adminProducts.Put("/:id", productHandler.UpdateProduct)
	adminProducts.Delete("/:id", productHandler.DeleteProduct)

	adminCategories := admin.Group("/categories")
	adminCategories.Post("/", handler.CreateCategory)
	adminCategories.Put("/:id", handler.UpdateCategory)
	adminCategories.Delete("/:id", handler.DeleteCategory)

	adminCafes := admin.Group("/cafes")
	adminCafes.Post("/", handler.CreateCafe)
	adminCafes.Put("/:id", handler.UpdateCafe)
	adminCafes.Delete("/:id", handler.DeleteCafe)

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
