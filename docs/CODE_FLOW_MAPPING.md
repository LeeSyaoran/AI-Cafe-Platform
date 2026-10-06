# AI Café Platform - Code Flow Mapping

## Mục lục
1. [Auth Flow (Register/Login)](#1-auth-flow)
2. [Create Order Flow](#2-create-order-flow)
3. [Payment Flow](#3-payment-flow)
4. [Payment Callback Flow](#4-payment-callback-flow)

---

## 1. Auth Flow

### 1.1 Register Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                              REGISTER FLOW                                                  │
│                                                                                             │
│  Client          Controller              Service              Repository         Database  │
│  ──────         ──────────             ───────             ──────────         ────────  │
│                                                                                             │
│  POST /v1/auth/register                                                                           │
│  {                                                                                               │
│    email,      ─────────────────────────────────────────────────────────────────────────────▶  │
│    password,   @Valid RegisterRequest                                                            │
│    phone      ──────────────                                                                    │
│  }                        │                                                                     │
│                           ▼                                                                     │
│                    ┌─────────────────┐                                                          │
│                    │ @Valid Check   │  • @NotBlank email                                      │
│                    │ Validate Input  │  • @Email format                                         │
│                    │                 │  • @NotBlank password                                   │
│                    │                 │  • @Size(min=8, max=128) password                       │
│                    └────────┬────────┘                                                          │
│                             │ valid                                                              │
│                             ▼                                                                   │
│                    ┌─────────────────┐     userService.createUser()                             │
│                    │ AuthController │─────────────────────────────────────────────────────────▶ │
│                    │ register()     │     Create User Record                                    │
│                    └────────┬────────┘     • Hash password                                      │
│                             │              • Set role = "customer"                              │
│                             │              • Return User entity                                 │
│                             ▼                                                                   │
│                    ┌─────────────────┐     authService.login()                                  │
│                    │                 │────────────────────────────────────────────────────────▶  │
│                    │ authService     │     Validate credentials                                  │
│                    │ .login()       │     • findByEmail(email)                                  │
│                    │                 │     • Compare password hash                               │
│                    └────────┬────────┘     • Check account status                               │
│                             │                                                                  │
│                             ▼                                                                  │
│                    ┌─────────────────┐     generateAccessToken(user)                            │
│                    │ Generate JWT    │────────────────────────────────────────────────────────▶  │
│                    │                 │     Create Access Token                                   │
│                    │ Access Token:   │     • Claims: userId, email, role                        │
│                    │  - userId      │     • Expiry: 15 minutes                                 │
│                    │  - email       │     • Sign: HMAC-SHA512                                  │
│                    │  - role        │                                                          │
│                    │  - exp: 15m    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                  │
│                    ┌─────────────────┐     generateRefreshToken(user)                           │
│                    │ Generate JWT    │────────────────────────────────────────────────────────▶  │
│                    │                 │     Create Refresh Token                                 │
│                    │ Refresh Token:  │     • Claims: userId, type="refresh"                    │
│                    │  - userId      │     • Expiry: 7 days                                    │
│                    │  - type        │     • Store in refresh_tokens table                       │
│                    │  - exp: 7d     │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                  │
│                    ┌─────────────────┐                                                          │
│                    │ Build Response  │     AuthResponse.builder()                              │
│                    │                 │     • accessToken                                        │
│                    │ {               │     • refreshToken                                       │
│                    │   accessToken,  │     • tokenType: "Bearer"                               │
│                    │   refreshToken, │     • expiresIn: 900 (15 min)                          │
│                    │   user: {...}  │     • user: UserResponse                                │
│                    │ }               │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│  ◀─────────────────────────────────────────────────────────────────────────────── Response   │
│  201 Created                                                                         │
│  {                                                                                               │
│    "success": true,                                                                  │
│    "data": {                                                                         │
│      "accessToken": "...",                                                            │
│      "refreshToken": "...",                                                           │
│      "user": {...}                                                                   │
│    }                                                                                 │
│  }                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 Login Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                LOGIN FLOW                                                   │
│                                                                                             │
│  Client          Controller              Service              Repository         Database  │
│  ──────         ──────────             ───────             ──────────         ────────  │
│                                                                                             │
│  POST /v1/auth/login                                                                          │
│  {                                                                                               │
│    email,      ─────────────────────────────────────────────────────────────────────────────▶  │
│    password   @Valid LoginRequest                                                              │
│  }                        │                                                                     │
│                           ▼                                                                     │
│                    ┌─────────────────┐                                                          │
│                    │ @Valid Check   │  • @NotBlank email                                      │
│                    │ Validate Input  │  • @NotBlank password                                   │
│                    └────────┬────────┘                                                          │
│                             │ valid                                                              │
│                             ▼                                                                   │
│                    ┌─────────────────┐     authService.login()                                  │
│                    │ AuthController │────────────────────────────────────────────────────────▶  │
│                    │ .login()       │     userRepository.findByEmail(email)                    │
│                    └────────┬────────┘     • SELECT * FROM users WHERE email = ?             │
│                             │              • Return Optional<User>                             │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Validate Password                                    │
│                    │ Password Check │────────────────────────────────────────────────────────▶  │
│                    │                 │     • Compare password with stored hash                 │
│                    │ if (!password   │     • If mismatch → 401 UNAUTHORIZED                  │
│                    │     .equals     │                                                          │
│                    │ (user.getPass   │                                                          │
│                    │  Hash)) {       │                                                          │
│                    │   throw 401    │                                                          │
│                    │ }               │                                                          │
│                    └────────┬────────┘                                                          │
│                             │ password valid                                                    │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Check Account Status                                  │
│                    │ Status Check   │────────────────────────────────────────────────────────▶  │
│                    │                 │     • If status = "inactive" → 403 FORBIDDEN           │
│                    │ if ("inactive" │                                                          │
│                    │   .equals      │                                                          │
│                    │ (user.status))  │                                                          │
│                    │ { throw 403 } │                                                          │
│                    └────────┬────────┘                                                          │
│                             │ active                                                            │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Generate Tokens                                       │
│                    │ JWT Generation │     (Same as Register)                                    │
│                    │                 │     • Access Token (15 min)                              │
│                    │ generateAccess │     • Refresh Token (7 days)                             │
│                    │ Token(user)    │     • Store refresh token in DB                         │
│                    │                 │                                                          │
│                    │ generateRefresh │                                                          │
│                    │ Token(user)    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                  │
│                    ┌─────────────────┐     userService.getUserByEmail()                          │
│                    │ Fetch User     │────────────────────────────────────────────────────────▶  │
│                    │ Profile        │     • SELECT u.*, up.* FROM users u                     │
│                    │                 │       LEFT JOIN user_profiles up ON ...                  │
│                    │ toUserResponse │     • Return full User entity                           │
│                    │ (user)         │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│  ◀─────────────────────────────────────────────────────────────────────────────── Response   │
│  200 OK                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 Refresh Token Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                            REFRESH TOKEN FLOW                                               │
│                                                                                             │
│  Client          Controller              Service              Repository         Database  │
│  ──────         ──────────             ───────             ──────────         ────────  │
│                                                                                             │
│  POST /v1/auth/refresh                                                                          │
│  {                                                                                               │
│    refreshToken  ─────────────────────────────────────────────────────────────────────────────▶  │
│  }                        │                                                                     │
│                           ▼                                                                     │
│                    ┌─────────────────┐     authService.refresh(refreshToken)                   │
│                    │ AuthController │────────────────────────────────────────────────────────▶  │
│                    │ .refresh()     │     refreshTokenRepository.findByToken(token)            │
│                    └────────┬────────┘     • SELECT * FROM refresh_tokens WHERE token = ?      │
│                             │              • Return Optional<RefreshToken>                    │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Validate Token Status                               │
│                    │ Token Validation│────────────────────────────────────────────────────────▶  │
│                    │                 │     • If revoked = true → 401 TOKEN_REVOKED           │
│                    │ Check:          │     • If expired → 401 TOKEN_EXPIRED                  │
│                    │  - revoked?    │                                                          │
│                    │  - expired?    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │ valid                                                              │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Revoke Old Token                                    │
│                    │ Revoke Token   │────────────────────────────────────────────────────────▶  │
│                    │                 │     UPDATE refresh_tokens SET                            │
│                    │ stored.setRev  │       revoked = true,                                   │
│                    │  (true)        │       revoked_at = NOW()                                │
│                    │ stored.setRev  │       WHERE id = ?                                     │
│                    │  At(NOW())    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Generate New Tokens                                  │
│                    │ JWT Generation │     • New Access Token (15 min)                           │
│                    │                 │     • New Refresh Token (7 days)                         │
│                    │ generateNewAcc  │     • Store new refresh token in DB                     │
│                    │ essToken()     │                                                          │
│                    │                 │                                                          │
│                    │ generateNewRef  │                                                          │
│                    │ reshToken()    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│  ◀─────────────────────────────────────────────────────────────────────────────── Response   │
│  200 OK                                                                                         │
│  {                                                                                               │
│    "accessToken": "...",                                                               │
│    "refreshToken": "..."                                                               │
│  }                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.4 Source Code Reference

**AuthController.java** (`user-service`)
```java
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        // 1. Create User
        var user = userService.createUser(
            request.getEmail(), 
            request.getPassword(), 
            request.getPhone(), 
            "customer"
        );
        
        // 2. Login (Get tokens)
        var tokens = authService.login(request.getEmail(), request.getPassword());

        // 3. Build Response
        AuthResponse response = AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType("Bearer")
                .expiresIn(tokens.expiresIn())
                .user(toUserResponse(user))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        var tokens = authService.login(request.getEmail(), request.getPassword());
        var user = userService.getUserByEmail(request.getEmail());
        // ... build response
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        var tokens = authService.refresh(request.getRefreshToken());
        // ... build response
    }
}
```

**AuthService.java** (`user-service`)
```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthTokens login(String email, String password) {
        // 1. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "INVALID_CREDENTIALS", ...));

        // 2. Validate password
        if (!password.equals(user.getPasswordHash())) {
            throw new ApiException(UNAUTHORIZED, "INVALID_CREDENTIALS", ...);
        }

        // 3. Check status
        if ("inactive".equals(user.getStatus())) {
            throw new ApiException(FORBIDDEN, "ACCOUNT_INACTIVE", ...);
        }

        // 4. Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return new AuthTokens(accessToken, refreshToken, accessTokenExpiration);
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        // 1. Find stored token
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "INVALID_TOKEN", ...));

        // 2. Validate token
        if (stored.getRevoked()) {
            throw new ApiException(UNAUTHORIZED, "TOKEN_REVOKED", ...);
        }
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(UNAUTHORIZED, "TOKEN_EXPIRED", ...);
        }

        // 3. Get user
        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new ApiException(NOT_FOUND, "USER_NOT_FOUND", ...));

        // 4. Revoke old token
        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        refreshTokenRepository.save(stored);

        // 5. Generate new tokens
        return new AuthTokens(
            generateAccessToken(user), 
            generateRefreshToken(user), 
            accessTokenExpiration
        );
    }

    private String generateAccessToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(UTF_8));
        Instant now = Instant.now();
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenExpiration, SECONDS)))
                .issuer(issuer)
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(User user) {
        // ... similar to access token but with longer expiry
        // Store in database for revocation capability
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(user.getId())
                .expiresAt(expiry)
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
```

---

## 2. Create Order Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                              CREATE ORDER FLOW                                             │
│                                                                                             │
│  Client          Controller              Service              Repository         Database  │
│  ──────         ──────────             ───────             ──────────         ────────  │
│                                                                                             │
│  POST /v1/orders                                                                             │
│  X-User-ID: xxx                                                                              │
│  {                                                                                               │
│    cafeId,        ─────────────────────────────────────────────────────────────────────────────▶  │
│    orderType,     @Valid CreateOrderRequest                                                   │
│    items: [...],  • @NotNull cafeId                                                          │
│    promoCode      • @NotBlank orderType                                                      │
│  }                        │             • @NotNull items                                     │
│                           ▼                                                                   │
│                    ┌─────────────────┐                                                      │
│                    │ OrderController │     orderService.createOrder()                         │
│                    │ .createOrder() │────────────────────────────────────────────────────────▶  │
│                    └────────┬────────┘     Step 1: Get Cart                                   │
│                             │              cartRepository.findByUserIdAndCafeId(userId, cafeId)  │
│                             ▼               • SELECT * FROM carts WHERE user_id = ? AND cafe_id = ?  │
│                    ┌─────────────────┐       • Return Optional<Cart>                          │
│                    │ Get User's Cart │────────────────────────────────────────────────────────▶  │
│                    │                 │     Step 2: Validate Cart                               │
│                    │ cartRepository  │       if (cart.items.isEmpty()) → throw CART_EMPTY     │
│                    │ .findByUserId  │                                                          │
│                    │ AndCafeId()    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │ cart found                                                       │
│                             ▼                                                                   │
│                    ┌─────────────────┐                                                      │
│                    │ Generate Order │     generateOrderNumber()                               │
│                    │ Number         │     • Format: "ORD" + yyyyMMdd + XXXX                   │
│                    │                 │     • Example: "ORD20240115A3F2"                      │
│                    │ "ORD" +        │                                                          │
│                    │ yyyyMMdd +     │                                                          │
│                    │ XXXX          │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Calculate Subtotal                                 │
│                    │ Calculate       │     Step 3: Calculate totals                           │
│                    │ Totals          │     • subtotal = cart.getSubtotal()                    │
│                    │                 │     • discountAmount = 0 (default)                    │
│                    │ subtotal =      │     • taxRate = 0.10 (10% VAT)                         │
│                    │   cart.getSub  │     • taxAmount = (subtotal - discount) * 0.10         │
│                    │   total()      │     • totalAmount = subtotal - discount + taxAmount     │
│                    │                 │                                                          │
│                    │ discount = 0    │                                                          │
│                    │ tax = 10%      │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Apply Promotion (if promoCode provided)             │
│                    │ Apply Promo    │────────────────────────────────────────────────────────▶  │
│                    │                 │     promotionRepository.findByCode(promoCode)            │
│                    │ promoRepository │     • SELECT * FROM promotions WHERE code = ?           │
│                    │ .findByCode()  │                                                          │
│                    │                 │     Validate Promotion:                                  │
│                    │ if (promoCode  │       • Check start_date <= now                        │
│                    │   != null)    │       • Check end_date >= now                           │
│                    │ {              │       • Check usage_count < usage_limit                │
│                    │   discount =   │       • Check min_order_amount <= subtotal              │
│                    │     applyPromo │                                                          │
│                    │ }              │     Calculate Discount:                                  │
│                    │                 │       • If percentage: subtotal * (value / 100)       │
│                    │                 │       • If fixed: value                                  │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Build Order Entity                                   │
│                    │ Build Order    │     Order.builder()                                      │
│                    │ Entity         │     • orderNumber                                        │
│                    │                 │     • userId, companyId, cafeId                         │
│                    │ Order.builder()│     • orderType                                          │
│                    │   .orderNumber │     • status = "pending"                                │
│                    │   .status()   │     • subtotal, discountAmount, taxAmount, totalAmount   │
│                    │   .total()    │     • paymentStatus = "pending"                          │
│                    │   .build()    │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Copy Items from Cart                                 │
│                    │ Copy Cart      │     for (CartItem cartItem : cart.getItems()) {         │
│                    │ Items to Order │       OrderItem orderItem = OrderItem.builder()          │
│                    │                 │           .productId(cartItem.getProductId())            │
│                    │ for each       │           .quantity(cartItem.getQuantity())              │
│                    │ cartItem:      │           .unitPrice(cartItem.getUnitPrice())           │
│                    │                 │           .build();                                     │
│                    │   order.addItem│       order.addItem(orderItem); // Bidirectional        │
│                    │   (orderItem)  │     }                                                   │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Save Order                                           │
│                    │ Save Order     │────────────────────────────────────────────────────────▶  │
│                    │                 │     orderRepository.save(order)                          │
│                    │ orderRepository│     • INSERT INTO orders (...) VALUES (...)               │
│                    │ .save(order)  │     • INSERT INTO order_items (...) VALUES (...)         │
│                    │                 │     • Return saved Order with ID                       │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Clear Cart                                           │
│                    │ Clear Cart     │────────────────────────────────────────────────────────▶  │
│                    │                 │     cartItemRepository.deleteAllByCartId(cart.getId())  │
│                    │ deleteAllBy    │     • DELETE FROM cart_items WHERE cart_id = ?           │
│                    │ CartId()       │     cartRepository.delete(cart)                         │
│                    │                 │     • DELETE FROM carts WHERE id = ?                    │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│  ◀─────────────────────────────────────────────────────────────────────────────── Response   │
│  200 OK                                                                                         │
│  {                                                                                               │
│    "id": "uuid",                                                                         │
│    "orderNumber": "ORD20240115A3F2",                                                     │
│    "status": "pending",                                                                 │
│    "totalAmount": 125000,                                                                │
│    "paymentStatus": "pending"                                                             │
│  }                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.1 Source Code Reference

**OrderController.java** (`order-service`)
```java
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        // Call service
        Order order = orderService.createOrder(
                userId,
                request.getCafeId(),
                request.getOrderType(),
                request.getDeliveryAddressId(),
                request.getCustomerNote(),
                request.getPromoCode()
        );

        return ResponseEntity.ok(ApiResponse.ok(toResponse(order)));
    }
}
```

**OrderService.java** (`order-service`)
```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PromotionRepository promotionRepository;

    @Transactional
    public Order createOrder(UUID userId, UUID cafeId, String orderType,
                           UUID deliveryAddressId, String customerNote, String promoCode) {
        
        // 1. Get cart
        Cart cart = cartRepository.findByUserIdAndCafeId(userId, cafeId)
                .orElseThrow(() -> new ApiException(NOT_FOUND, "CART_EMPTY", "Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new ApiException(BAD_REQUEST, "CART_EMPTY", "Cart is empty");
        }

        // 2. Generate order number
        String orderNumber = generateOrderNumber();

        // 3. Calculate totals
        double subtotal = cart.getSubtotal();
        double discountAmount = 0;

        // 4. Apply promotion if provided
        if (promoCode != null && !promoCode.isEmpty()) {
            discountAmount = applyPromotion(cart, promoCode);
        }

        // 5. Calculate tax and total
        double taxRate = 0.10; // 10% VAT
        double taxAmount = (subtotal - discountAmount) * taxRate;
        double totalAmount = subtotal - discountAmount + taxAmount;

        // 6. Build order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .companyId(companyId)
                .cafeId(cafeId)
                .orderType(orderType)
                .status("pending")
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .totalPaid(0.0)
                .paymentStatus("pending")
                .deliveryAddressId(deliveryAddressId)
                .customerNote(customerNote)
                .build();

        // 7. Copy items from cart
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .variantId(cartItem.getVariantId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .lineTotal(cartItem.getLineTotal())
                    .build();
            order.addItem(orderItem);
        }

        // 8. Save order
        order = orderRepository.save(order);

        // 9. Clear cart
        cartItemRepository.deleteAllByCartId(cart.getId());
        cartRepository.delete(cart);

        log.info("Created order {} for user {}", orderNumber, userId);
        return order;
    }

    private double applyPromotion(Cart cart, String promoCode) {
        Promotion promo = promotionRepository.findByCode(promoCode)
                .orElseThrow(() -> new ApiException(BAD_REQUEST, "INVALID_PROMO", "Invalid code"));

        // Validate promotion
        Instant now = Instant.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) {
            throw new ApiException(BAD_REQUEST, "PROMO_NOT_STARTED", ...);
        }
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) {
            throw new ApiException(BAD_REQUEST, "PROMO_EXPIRED", ...);
        }

        // Calculate discount
        double subtotal = cart.getSubtotal();
        if ("percentage".equals(promo.getDiscountType())) {
            double discount = subtotal * (promo.getDiscountValue() / 100);
            if (promo.getMaxDiscountAmount() != null && discount > promo.getMaxDiscountAmount()) {
                discount = promo.getMaxDiscountAmount();
            }
            return discount;
        } else {
            return promo.getDiscountValue();
        }
    }
}
```

---

## 3. Payment Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                              PAYMENT FLOW                                                   │
│                                                                                             │
│  Client          Controller              Service              Repository         External   │
│  ──────         ──────────             ───────             ──────────         ────────  │
│                                                                                             │
│  POST /v1/payments                                                                             │
│  X-User-ID: xxx                                                                              │
│  {                                                                                               │
│    orderId,        ─────────────────────────────────────────────────────────────────────────▶  │
│    amount,         @Valid CreatePaymentRequest                                                │
│    paymentMethod:  • @NotNull orderId                                                        │
│      "VNPAY"       • @NotNull amount                                                          │
│  }                        │             • @Positive amount                                    │
│                           ▼             • @NotBlank paymentMethod                             │
│                    ┌─────────────────┐                                                          │
│                    │ PaymentControl │     paymentService.createPayment()                        │
│                    │ ler.createPay  │────────────────────────────────────────────────────────▶  │
│                    │ ment()         │     Step 1: Check existing payment                        │
│                    └────────┬────────┘     paymentRepository.findByOrderId(orderId)            │
│                             │              • If exists & pending → throw 409 CONFLICT         │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 2: Generate Transaction ID                      │
│                    │ Generate        │     • Format: "TX" + timestamp + UUID.substring(0,8)    │
│                    │ Transaction ID  │     • Example: "TX1705314567A3F2CD1E"                  │
│                    │                 │                                                          │
│                    │ "TX" + ts +    │                                                          │
│                    │ UUID.substring │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 3: Create Payment Entity                       │
│                    │ Build Payment  │     Payment.builder()                                    │
│                    │ Entity         │     • orderId                                           │
│                    │                 │     • userId                                           │
│                    │ Payment.builder│     • transactionId                                     │
│                    │   .orderId()  │     • amount                                            │
│                    │   .amount()   │     • paymentMethod                                      │
│                    │   .build()    │     • status = "pending"                                │
│                    │                 │     • expiredAt = now + 15 minutes                     │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 4: Generate Payment URL                         │
│                    │ Route to        │     switch (paymentMethod) {                            │
│                    │ Payment Gateway │       case "VNPAY":                                     │
│                    │                 │         → vnPayService.createPaymentUrl(payment)        │
│                    │ switch(method) │       case "MOMO":                                      │
│                    │   VNPAY →      │         → moMoService.createPaymentUrl(payment)         │
│                    │   vnPayService │       case "ZALOPAY":                                   │
│                    │   MOMO →       │         → zaloPayService.createPaymentUrl(payment)       │
│                    │   moMoService  │     }                                                   │
│                    │   ZALOPAY →    │                                                          │
│                    │   zaloPaySvc   │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     VNPayService.createPaymentUrl()                      │
│                    │ VNPay URL      │     ┌─────────────────────────────────────────────────│ │
│                    │ Generation     │     │ 1. Build params:                                 │ │
│                    │                 │     │    vnp_Amount = amount * 100                     │ │
│                    │ Build param    │     │    vnp_TxnRef = transactionId                     │ │
│                    │ map with:      │     │    vnp_ReturnUrl = configured URL                 │ │
│                    │                 │     │ 2. Create signature:                            │ │
│                    │ vnp_Amount     │     │    HMAC-SHA512(hashSecret, queryString)           │ │
│                    │ vnp_TxnRef     │     │ 3. Build URL:                                    │ │
│                    │ vnp_ReturnUrl  │     │    apiUrl + "?" + query + signature               │ │
│                    │                 │     └─────────────────────────────────────────────────│ │
│                    │ HMAC-SHA512   │                                                          │
│                    │ signature     │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 5: Save Payment                                 │
│                    │ Save Payment  │────────────────────────────────────────────────────────▶  │
│                    │                 │     paymentRepository.save(payment)                    │
│                    │ payment.setPay │     • INSERT INTO payments (...) VALUES (...)           │
│                    │ mentUrl(url)   │     • Return saved Payment with ID                      │
│                    │                 │                                                          │
│                    │ paymentReposi  │                                                          │
│                    │ tory.save()   │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 6: Log Transaction                             │
│                    │ Log Transaction│────────────────────────────────────────────────────────▶  │
│                    │                 │     transactionRepository.save(PaymentTransaction)       │
│                    │ logTransaction │     • Type: "CREATE"                                   │
│                    │ (...)          │     • Status: "SUCCESS"                                 │
│                    │                 │     • Provider: paymentMethod                           │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│  ◀─────────────────────────────────────────────────────────────────────────────── Response   │
│  200 OK                                                                                         │
│  {                                                                                               │
│    "id": "uuid",                                                                         │
│    "transactionId": "TX1705314567A3F2CD1E",                                             │
│    "paymentUrl": "https://sandbox.vnpayment.vn/.../?...",                             │
│    "status": "pending",                                                                 │
│    "amount": 125000                                                                     │
│  }                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.1 Source Code Reference

**PaymentController.java** (`payment-service`)
```java
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader("X-User-ID") UUID userId) {

        Payment payment = paymentService.createPayment(
                request.getOrderId(),
                userId,
                request.getCompanyId(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getReturnUrl()
        );

        return ResponseEntity.ok(ApiResponse.ok(toResponse(payment)));
    }
}
```

**PaymentService.java** (`payment-service`)
```java
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final VnPayService vnPayService;
    private final MoMoService moMoService;
    private final ZaloPayService zaloPayService;

    @Transactional
    public Payment createPayment(UUID orderId, UUID userId, UUID companyId, 
                                Double amount, String method, String returnUrl) {
        
        // 1. Check existing payment
        paymentRepository.findByOrderId(orderId).ifPresent(existing -> {
            if ("pending".equals(existing.getStatus()) || "processing".equals(existing.getStatus())) {
                throw new ApiException(CONFLICT, "PAYMENT_EXISTS", 
                    "Payment already exists for this order");
            }
        });

        // 2. Generate transaction ID
        String transactionId = generateTransactionId();

        // 3. Build payment entity
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .companyId(companyId)
                .transactionId(transactionId)
                .paymentMethod(method)
                .status("pending")
                .amount(amount)
                .currency("VND")
                .returnUrl(returnUrl)
                .expiredAt(Instant.now().plusSeconds(900)) // 15 minutes
                .build();

        // 4. Generate payment URL based on method
        String paymentUrl = switch (method.toUpperCase()) {
            case "VNPAY" -> vnPayService.createPaymentUrl(payment);
            case "MOMO" -> moMoService.createPaymentUrl(payment);
            case "ZALOPAY" -> zaloPayService.createPaymentUrl(payment);
            default -> throw new ApiException(BAD_REQUEST, "INVALID_METHOD", 
                "Invalid payment method");
        };

        // 5. Save payment
        payment.setPaymentUrl(paymentUrl);
        payment = paymentRepository.save(payment);

        // 6. Log transaction
        logTransaction(payment.getId(), "CREATE", "SUCCESS", method, null, null);

        return payment;
    }
}
```

**VnPayService.java** (`payment-service`)
```java
@Service
public class VnPayService {

    @Value("${payment.vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${payment.vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${payment.vnpay.api-url}")
    private String vnpApiUrl;

    public String createPaymentUrl(Payment payment) {
        // 1. Build parameters
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", VERSION);
        params.put("vnp_Command", COMMAND);
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", String.valueOf((long)(payment.getAmount() * 100)));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", payment.getTransactionId());
        params.put("vnp_OrderInfo", "Thanh toan don hang " + payment.getTransactionId());
        params.put("vnp_OrderType", ORDER_TYPE);
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", Instant.now()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // 2. Build query string
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                query.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), UTF_8)).append("&");
            }
        }

        // 3. Generate signature
        String signData = query.toString().substring(0, query.length() - 1);
        String signature = hmacSHA512(vnpHashSecret, signData);

        // 4. Build final URL
        query.append("vnp_SecureHashType=HmacSHA512&");
        query.append("vnp_SecureHash=").append(signature);

        return vnpApiUrl + "?" + query;
    }
}
```

---

## 4. Payment Callback Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                          PAYMENT CALLBACK FLOW                                               │
│                                                                                             │
│  Payment          Controller              Service              Repository        Database    │
│  Gateway         ──────────             ───────             ──────────        ────────    │
│  ─────────                                                                                  │
│                                                                                             │
│  GET/POST from VNPay/MoMo/ZaloPay Gateway                                                     │
│  ?vnp_ResponseCode=00&vnp_TransactionStatus=00&...                                              │
│                           │                                                                   │
│                           ▼                                                                   │
│                    ┌─────────────────┐                                                      │
│                    │ Callback URL    │     paymentService.handleCallback()                    │
│                    │ /v1/payments/   │───────────────────────────────────────────────────────▶  │
│                    │ callback/{method}     Step 1: Parse callback data                       │
│                    └────────┬────────┘     • Extract vnp_ResponseCode                       │
│                             │              • Extract vnp_TransactionStatus                   │
│                             │              • Extract other params                            │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 2: Verify & Get Payment                       │
│                    │ Route to        │     switch (method) {                                 │
│                    │ Payment Service │       case "VNPAY":                                   │
│                    │                 │         → vnPayService.verifyCallback(data)           │
│                    │ handleCallback │       case "MOMO":                                    │
│                    │ (method, data) │         → moMoService.verifyCallback(data)            │
│                    │                 │     }                                                │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     VNPay Verification                                 │
│                    │ VNPay Callback │     ┌─────────────────────────────────────────────────┐ │
│                    │ Verification    │     │ 1. Parse query string to map                   │ │
│                    │                 │     │ 2. Extract vnp_SecureHash                     │ │
│                    │ verifyCallback │     │ 3. Remove secure hash from params              │ │
│                    │ (data)         │     │ 4. Recreate signature from params              │ │
│                    │                 │     │ 5. Compare signatures                        │ │
│                    │                 │     │ 6. If match & code=00 → success              │ │
│                    │                 │     │ 7. If mismatch or error → failed              │ │
│                    │                 │     └─────────────────────────────────────────────────┘ │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 3: Update Payment Status                      │
│                    │ Update Payment │───────────────────────────────────────────────────────▶  │
│                    │ Status         │     payment.setStatus("completed")                     │
│                    │                 │     payment.setPaidAt(Instant.now())                  │
│                    │ if (code == 00 │     paymentRepository.save(payment)                   │
│                    │   && status==00│     • UPDATE payments SET status = 'completed', ...   │
│                    │ ) {           │                                                          │
│                    │   status =     │                                                          │
│                    │   "completed" │                                                          │
│                    │ } else {      │                                                          │
│                    │   status =     │                                                          │
│                    │   "failed"    │                                                          │
│                    │ }             │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 4: Log Transaction                             │
│                    │ Log Transaction│───────────────────────────────────────────────────────▶  │
│                    │                 │     transactionRepository.save(                         │
│                    │ logTransaction │       PaymentTransaction.builder()                       │
│                    │ (...)          │         .paymentId(paymentId)                           │
│                    │                 │         .transactionType("CALLBACK")                     │
│                    │                 │         .status("SUCCESS")                              │
│                    │                 │         .provider(method)                               │
│                    └────────┬────────┘         .build()                                      │
│                             │                                                              │
│                             ▼                                                                   │
│                    ┌─────────────────┐     Step 5: Update Order Status (TODO)                  │
│                    │ TODO: Update   │     ┌─────────────────────────────────────────────────┐ │
│                    │ Order & Notify │     │ orderService.updatePaymentStatus()              │ │
│                    │                 │     │ notificationService.send(...)                  │ │
│                    │ // TODO:       │     │ (Currently not implemented)                    │ │
│                    │ orderService   │     └─────────────────────────────────────────────────┘ │
│                    │ .updateOrder() │                                                          │
│                    │                 │                                                          │
│                    │ notification    │                                                          │
│                    │ .send(...)     │                                                          │
│                    └────────┬────────┘                                                          │
│                             │                                                                  │
│  ◀─────────────────────────────────────────────────────────────────── Redirect to Client    │
│  (Redirect to returnUrl with status)                                                            │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 4.1 Source Code Reference

**PaymentCallbackController.java** (via PaymentService)
```java
// Callback is handled in PaymentService.handleCallback()
@Service
public class PaymentService {
    
    @Transactional
    public void handleCallback(String method, String data) {
        log.info("Received callback from {}: {}", method, data);

        // Verify callback based on payment method
        Payment payment = switch (method.toUpperCase()) {
            case "VNPAY" -> vnPayService.verifyCallback(data);
            case "MOMO" -> moMoService.verifyCallback(data);
            case "ZALOPAY" -> zaloPayService.verifyCallback(data);
            default -> throw new ApiException(BAD_REQUEST, "INVALID_METHOD", ...);
        };

        // Save updated payment
        paymentRepository.save(payment);
        
        // Log transaction
        logTransaction(payment.getId(), "CALLBACK", "SUCCESS", method, null, null);

        // TODO: Update order status, send notification
        log.info("Payment {} completed successfully for order {}", 
            payment.getId(), payment.getOrderId());
    }
}
```

**VnPayService.verifyCallback()**
```java
public Payment verifyCallback(String callbackData) {
    try {
        // 1. Parse callback query string
        Map<String, String> params = parseQueryString(callbackData);

        // 2. Get secure hash and order info
        String secureHash = params.remove("vnp_SecureHash");
        String orderInfo = params.get("vnp_OrderInfo");

        // 3. Extract transaction ID
        String transactionId = orderInfo.replace("Thanh toan don hang ", "");

        // 4. Verify signature (compare with recalculated hash)
        // ... signature verification logic ...

        // 5. Build payment result
        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setStatus("completed");
        payment.setPaidAt(Instant.now());

        return payment;
    } catch (Exception e) {
        log.error("Error verifying VNPay callback", e);
        Payment payment = new Payment();
        payment.setStatus("failed");
        return payment;
    }
}
```

---

## 5. Tổng hợp Database Tables

### 5.1 Auth-related Tables

```sql
-- users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50),
    status VARCHAR(50),
    company_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- user_profiles table
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    full_name VARCHAR(255),
    display_name VARCHAR(255),
    avatar_url TEXT,
    date_of_birth DATE,
    gender VARCHAR(20),
    language VARCHAR(10),
    timezone VARCHAR(50),
    bio TEXT
);

-- refresh_tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    token TEXT UNIQUE NOT NULL,
    expires_at TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP
);
```

### 5.2 Order-related Tables

```sql
-- orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    company_id UUID NOT NULL,
    cafe_id UUID NOT NULL,
    order_type VARCHAR(50),
    status VARCHAR(50),
    subtotal DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    tax_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    total_paid DECIMAL(10,2),
    payment_status VARCHAR(50),
    payment_method VARCHAR(50),
    payment_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- order_items table
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    product_id UUID,
    variant_id UUID,
    product_name VARCHAR(255),
    quantity INTEGER,
    unit_price DECIMAL(10,2),
    line_total DECIMAL(10,2),
    item_status VARCHAR(50),
    created_at TIMESTAMP
);

-- carts table
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    company_id UUID NOT NULL,
    cafe_id UUID NOT NULL,
    subtotal DECIMAL(10,2),
    item_count INTEGER,
    expires_at TIMESTAMP,
    created_at TIMESTAMP
);

-- cart_items table
CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES carts(id),
    product_id UUID,
    variant_id UUID,
    quantity INTEGER,
    unit_price DECIMAL(10,2),
    line_total DECIMAL(10,2)
);

-- promotions table
CREATE TABLE promotions (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255),
    discount_type VARCHAR(50),
    discount_value DECIMAL(10,2),
    min_order_amount DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    usage_limit INTEGER,
    used_count INTEGER,
    status VARCHAR(50)
);
```

### 5.3 Payment-related Tables

```sql
-- payments table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    user_id UUID NOT NULL,
    company_id UUID NOT NULL,
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(50),
    amount DECIMAL(10,2),
    currency VARCHAR(10),
    payment_url TEXT,
    payment_id VARCHAR(255),
    return_url TEXT,
    paid_at TIMESTAMP,
    expired_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- payment_transactions table
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY,
    payment_id UUID REFERENCES payments(id),
    transaction_type VARCHAR(50),
    status VARCHAR(50),
    provider VARCHAR(50),
    provider_transaction_id VARCHAR(255),
    error_message TEXT,
    created_at TIMESTAMP
);
```

---

## 6. Exception Handling Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                           EXCEPTION HANDLING FLOW                                            │
│                                                                                             │
│  ┌─────────┐                                                                               │
│  │ Service │ throws ApiException                                                             │
│  │  Layer  │    new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found") │
│  └────┬────┘                                                                               │
│       │                                                                                     │
│       ▼                                                                                     │
│  ┌────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │ @ControllerAdvice Global Exception Handler                                              │ │
│  │                                                                                        │ │
│  │  @ExceptionHandler(ApiException.class)                                                 │ │
│  │  public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {               │ │
│  │      return ResponseEntity                                                              │ │
│  │          .status(ex.getStatus())                                                       │ │
│  │          .body(ApiResponse.error(ex.getCode(), ex.getMessage()));                       │ │
│  │  }                                                                                     │ │
│  └────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                             │
│  Response to Client:                                                                        │
│  ┌────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │ HTTP 404 Not Found                                                                       │ │
│  │ {                                                                                      │ │
│  │   "success": false,                                                                    │ │
│  │   "error": {                                                                            │ │
│  │     "code": "ORDER_NOT_FOUND",                                                          │ │
│  │     "message": "Order not found"                                                        │ │
│  │   }                                                                                    │ │
│  │ }                                                                                      │ │
│  └────────────────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Transaction Management

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                           TRANSACTION FLOW                                                   │
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │ @Transactional Annotation                                                              │ │
│  │                                                                                        │ │
│  │  @Transactional                                                                        │ │
│  │  public Order createOrder(...) {                                                       │ │
│  │      // All operations in this method are in ONE transaction                         │ │
│  │                                                                                        │ │
│  │      Order order = orderRepository.save(order);    ← Save 1 (INSERT)               │ │
│  │      // ...                                                                           │ │
│  │      cartItemRepository.deleteAllByCartId(...);  ← Delete (DELETE)                   │ │
│  │      cartRepository.delete(cart);                ← Delete (DELETE)                   │ │
│  │                                                                                        │ │
│  │      return order;  ← COMMIT if no exception                                         │ │
│  │  }                                                                                     │ │
│  │                                                                                        │ │
│  │  // If ANY exception is thrown → ROLLBACK all changes                                │ │
│  └────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │ Read-Only Transaction Optimization                                                     │ │
│  │                                                                                        │ │
│  │  @Transactional(readOnly = true)                                                        │ │
│  │  public Order getOrder(UUID orderId) {                                                │ │
│  │      // Hibernate can optimize: skip dirty checking, skip write locks                 │ │
│  │      return orderRepository.findById(orderId).orElseThrow(...);                      │ │
│  │  }                                                                                     │ │
│  └────────────────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

**Document Version:** 1.0  
**Last Updated:** 2024  
**Author:** AI Café Platform Team
