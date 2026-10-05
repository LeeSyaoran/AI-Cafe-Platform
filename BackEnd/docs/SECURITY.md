# Security Documentation

## 1. Security Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                            SECURITY ARCHITECTURE                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                               CLIENTS                                          │   │
│  │                                                                             │   │
│  │    Mobile App          │        Web App         │       SDK/API              │   │
│  │    ─────────          │        ──────         │       ───────              │   │
│  │    • iOS/Android      │        • SPA          │       • REST API           │   │
│  │    • Keychain/Keystore│        • HTTPS        │       • OAuth 2.0          │   │
│  │    • Certificate Pin   │        • CSP          │       • API Keys           │   │
│  │                       │                        │                            │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                               │
│                                      ▼                                               │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                            EDGE LAYER                                         │   │
│  │                                                                             │   │
│  │    CloudFlare WAF          │        Load Balancer                             │   │
│  │    ─────────────────       │        ─────────────                             │   │
│  │    • DDoS Protection       │        • SSL Termination                         │   │
│  │    • Bot Management        │        • Rate Limiting                            │   │
│  │    • IP Blocking           │        • Health Checks                           │   │
│  │    • SQL/XSS Detection     │        • Connection Pooling                       │   │
│  │    • Geo-blocking          │                                                    │   │
│  │                                                                             │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                               │
│                                      ▼                                               │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                           API GATEWAY (Kong)                                   │   │
│  │                                                                             │   │
│  │    Authentication           │        Authorization                            │   │
│  │    ─────────────           │        ──────────────                            │   │
│  │    • JWT Validation         │        • RBAC Check                             │   │
│  │    • Token Introspection    │        • Scope Validation                        │   │
│  │    • SSO Integration        │        • Ownership Verification                  │   │
│  │    • API Key Validation     │        • Subscription Tiers                     │   │
│  │                                                                             │   │
│  │    Rate Limiting            │        Request Processing                        │   │
│  │    ──────────────          │        ──────────────────                        │   │
│  │    • Per-user limits       │        • Input Validation                        │   │
│  │    • Per-endpoint limits   │        • Sanitization                            │   │
│  │    • Burst handling        │        • Normalization                           │   │
│  │    • Quota management      │        • Logging                                  │   │
│  │                                                                             │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                               │
│                                      ▼                                               │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                           BACKEND SERVICES                                    │   │
│  │                                                                             │   │
│  │    Java Services (Spring Boot)    │        Go Services                         │   │
│  │    ─────────────────────────     │        ───────────                         │   │
│  │    • Spring Security             │        • JWT Middleware                     │   │
│  │    • Session Management           │        • RBAC Middleware                    │   │
│  │    • CSRF Protection             │        • Input Validation                    │   │
│  │    • XSS Prevention              │        • Audit Logging                       │   │
│  │    • SQL Injection Prevention     │        • Rate Limiting                      │   │
│  │                                                                             │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                               │
│                                      ▼                                               │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                              DATA LAYER                                        │   │
│  │                                                                             │   │
│  │    PostgreSQL                 │        Redis                │        S3          │   │
│  │    ──────────                │        ─────                │        ──          │   │
│  │    • Row-Level Security       │        • ACL                │        • SSE       │   │
│  │    • Column Encryption       │        • Session TTL        │        • ACL       │   │
│  │    • Audit Logging           │        • Rate Limit         │        • Versioning │   │
│  │    • Backup Encryption       │        • Encrypted Keys     │        • MFA       │   │
│  │                                                                             │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Authentication

### 2.1 JWT Token Structure

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "2024-01-aicafe-key-1"
  },
  "payload": {
    "iss": "https://auth.aicafe.vn",
    "sub": "user-uuid",
    "aud": ["api.aicafe.vn"],
    "exp": 1705396200,
    "iat": 1705391700,
    "jti": "token-unique-id",
    "scope": "chat image video",
    "tier": "DEVELOPER",
    "user_id": "user-uuid"
  }
}
```

### 2.2 Token Lifecycle

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               TOKEN LIFECYCLE                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│    Access Token                    Refresh Token                    Session           │
│    ───────────                    ──────────────                    ───────           │
│                                                                                      │
│    ┌──────────┐                  ┌──────────┐                   ┌──────────┐        │
│    │ 15 min   │                  │ 7 days   │                   │ 30 days  │        │
│    │ validity │                  │ validity │                   │ inactivity │        │
│    └────┬─────┘                  └────┬─────┘                   └────┬─────┘        │
│         │                             │                             │               │
│         ▼                             ▼                             ▼               │
│    Used for                    Used to                     Stored in                 │
│    API requests               get new                      PostgreSQL                │
│                              access tokens                                          │
│                                                                                      │
│    ┌──────────┐                  ┌──────────┐                                        │
│    │ Auto     │                  │ Rotated  │                                        │
│    │ refresh  │─────────────────►│ on use   │                                        │
│    │ before   │                  │          │                                        │
│    │ expiry   │                  │ Old token│                                        │
│    └──────────┘                  │ revoked  │                                        │
│                                  └──────────┘                                        │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 OTP Authentication

```go
// OTP Generation
func GenerateOTP(phone string) (string, error) {
    // Generate 6-digit code
    code := fmt.Sprintf("%06d", rand.Intn(1000000))
    
    // Hash for storage
    hashedCode := sha256.Sum256([]byte(code))
    
    // Store in Redis with 5-minute TTL
    key := fmt.Sprintf("otp:%s", phone)
    err := redis.Set(ctx, key, hex.EncodeToString(hashedCode[:]), 5*time.Minute).Err()
    
    // Track attempts
    redis.Incr(ctx, fmt.Sprintf("otp:attempts:%s", phone))
    redis.Expire(ctx, fmt.Sprintf("otp:attempts:%s", phone), 24*time.Hour)
    
    return code, nil
}

// OTP Verification
func VerifyOTP(phone, code string) (bool, error) {
    key := fmt.Sprintf("otp:%s", phone)
    
    // Check attempt count
    attempts, _ := redis.Get(ctx, fmt.Sprintf("otp:attempts:%s", phone)).Int()
    if attempts >= 3 {
        return false, ErrOTPAttemptsExceeded
    }
    
    // Get stored hash
    storedHash, err := redis.Get(ctx, key).Result()
    if err == redis.Nil {
        return false, ErrOTPNotFound
    }
    
    // Compare
    inputHash := sha256.Sum256([]byte(code))
    if hex.EncodeToString(inputHash[:]) != storedHash {
        return false, ErrOTPInvalid
    }
    
    // Delete used OTP
    redis.Del(ctx, key)
    
    return true, nil
}
```

---

## 3. Data Encryption

### 3.1 Encryption at Rest

```sql
-- Enable pgcrypto extension
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Encrypt sensitive columns
ALTER TABLE users ADD COLUMN phone_encrypted BYTEA;
ALTER TABLE users ADD COLUMN email_encrypted BYTEA;

-- Function to encrypt
CREATE OR REPLACE FUNCTION encrypt_column(value TEXT, key_id TEXT)
RETURNS BYTEA AS $$
DECLARE
    key_bytes BYTEA;
BEGIN
    -- Get encryption key from key management
    key_bytes := get_encryption_key(key_id);
    
    -- Encrypt using AES-256-GCM
    RETURN pgp_sym_encrypt(value, key_bytes, 'compress-algo=1, cipher-algo=aes256');
END;
$$ LANGUAGE plpgsql;

-- Function to decrypt
CREATE OR REPLACE FUNCTION decrypt_column(encrypted BYTEA, key_id TEXT)
RETURNS TEXT AS $$
DECLARE
    key_bytes BYTEA;
BEGIN
    key_bytes := get_encryption_key(key_id);
    RETURN pgp_sym_decrypt(encrypted, key_bytes);
END;
$$ LANGUAGE plpgsql;

-- Trigger for auto-encryption on insert/update
CREATE OR REPLACE FUNCTION encrypt_user_sensitive_data()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.phone IS NOT NULL THEN
        NEW.phone_encrypted := encrypt_column(NEW.phone, 'user-phone-v1');
        NEW.phone := NULL; -- Don't store plaintext
    END IF;
    
    IF NEW.email IS NOT NULL THEN
        NEW.email_encrypted := encrypt_column(NEW.email, 'user-email-v1');
        NEW.email := NULL;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_encrypt_user_data
    BEFORE INSERT OR UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION encrypt_user_sensitive_data();
```

### 3.2 Encryption in Transit

```yaml
# TLS Configuration
ssl:
  enabled: true
  min_version: "TLSv1.2"
  cipher_suites:
    - TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
    - TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
    - TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256
  
  certificates:
    - domain: "*.aicafe.vn"
      provider: "letsencrypt"
      auto_renew: true
```

### 3.3 Key Management

```go
// Key Management Service
type KeyManager struct {
    vaultAddr string
    transitPath string
}

func (km *KeyManager) GetEncryptionKey(keyID string) ([]byte, error) {
    // Request key from Vault Transit
    resp, err := http.PostForm(
        fmt.Sprintf("%s/v1/transit/decrypt/%s", km.vaultAddr, km.transitPath),
        url.Values{" ciphertext": {keyID}},
    )
    
    // Rotate keys annually
    // Store key versions for decryption of old data
}

func (km *KeyManager) RotateKey() error {
    // Generate new key version
    // Re-encrypt existing data with new key
    // Mark old key for retirement
}
```

---

## 4. API Security

### 4.1 Request Validation

```go
// Request validation middleware
func ValidationMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        // Validate Content-Type
        if c.GetHeader("Content-Type") != "application/json" {
            c.AbortWithStatusJSON(400, ErrorResponse{
                Code:    "INVALID_CONTENT_TYPE",
                Message: "Content-Type must be application/json",
            })
            return
        }
        
        // Validate Authorization header format
        auth := c.GetHeader("Authorization")
        if !strings.HasPrefix(auth, "Bearer ") {
            c.AbortWithStatusJSON(401, ErrorResponse{
                Code:    "INVALID_AUTH_FORMAT",
                Message: "Authorization header must start with Bearer",
            })
            return
        }
        
        // Validate request size
        if c.Request.ContentLength > 10*1024*1024 { // 10MB
            c.AbortWithStatusJSON(413, ErrorResponse{
                Code:    "PAYLOAD_TOO_LARGE",
                Message: "Request body exceeds 10MB limit",
            })
            return
        }
        
        c.Next()
    }
}

// Input sanitization
func SanitizeInput(input string) string {
    // Remove HTML tags
    re := regexp.MustCompile(`<[^>]*>`)
    sanitized := re.ReplaceAllString(input, "")
    
    // Escape special characters
    sanitized = html.EscapeString(sanitized)
    
    // Trim whitespace
    return strings.TrimSpace(sanitized)
}
```

### 4.2 SQL Injection Prevention

```go
// Use parameterized queries only
func GetUserByID(userID string) (*User, error) {
    query := `SELECT id, phone, name, tier, status 
              FROM users 
              WHERE id = $1 AND status = 'ACTIVE'`
    
    var user User
    err := db.QueryRow(query, userID).Scan(
        &user.ID, &user.Phone, &user.Name, &user.Tier, &user.Status,
    )
    return &user, err
}

// Transaction safety
func TransferCredits(fromUser, toUser string, amount int) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()
    
    // Lock rows for update
    _, err = tx.Exec(`UPDATE wallets SET credits = credits - $1 
                       WHERE user_id = $2 AND credits >= $1`, 
        amount, fromUser)
    if err != nil {
        return err
    }
    
    _, err = tx.Exec(`UPDATE wallets SET credits = credits + $1 
                       WHERE user_id = $2`, 
        amount, toUser)
    if err != nil {
        return err
    }
    
    return tx.Commit()
}
```

### 4.3 Rate Limiting

```go
// Rate limiter using Redis sliding window
type RateLimiter struct {
    redis *redis.Client
}

func (rl *RateLimiter) Allow(userID, endpoint string, limit int, window time.Duration) (bool, int, error) {
    now := time.Now().Unix()
    windowStart := now - int64(window.Seconds())
    
    key := fmt.Sprintf("ratelimit:%s:%s", userID, endpoint)
    
    // Remove old entries
    rl.redis.ZRemRangeByScore(ctx, key, "0", strconv.FormatInt(windowStart, 10))
    
    // Count current requests
    count, _ := rl.redis.ZCard(key).Result()
    
    if count >= int64(limit) {
        // Get oldest entry to calculate reset time
        oldest, _ := rl.redis.ZRange(key, 0, 0).Result()
        if len(oldest) > 0 {
            oldestTime, _ := strconv.ParseInt(oldest[0], 10, 64)
            resetIn := time.Duration(oldestTime+int64(window.Seconds())-now) * time.Second
            return false, int(resetIn.Seconds()), nil
        }
        return false, int(window.Seconds()), nil
    }
    
    // Add new request
    rl.redis.ZAdd(key, &redis.Z{
        Score:  float64(now),
        Member: strconv.FormatInt(now, 10) + ":" + uuid.New().String(),
    })
    rl.redis.Expire(key, window)
    
    remaining := limit - int(count) - 1
    return true, remaining, nil
}

// Rate limit headers
func SetRateLimitHeaders(c *gin.Context, limit, remaining int, resetIn int) {
    c.Header("X-RateLimit-Limit", strconv.Itoa(limit))
    c.Header("X-RateLimit-Remaining", strconv.Itoa(remaining))
    c.Header("X-RateLimit-Reset", strconv.FormatInt(time.Now().Unix()+int64(resetIn), 10))
    c.Header("X-RateLimit-Window", "60")
}
```

---

## 5. Payment Security

### 5.1 PCI DSS Compliance

```go
// Never store full card numbers
type PaymentMethod struct {
    ID           string `json:"id"`
    UserID       string `json:"user_id"`
    Provider     string `json:"provider"` // VNPAY, MOMO, ZALOPAY
    CardType     string `json:"card_type,omitempty"` // VISA, MASTER
    LastFour     string `json:"last_four,omitempty"` // Last 4 digits only
    ExpiryMonth  int    `json:"expiry_month,omitempty"`
    ExpiryYear   int    `json:"expiry_year,omitempty"`
    
    // Provider tokens (not card data)
    ProviderToken     string `json:"-"`
    ProviderCustomerID string `json:"-"`
    
    // DO NOT store: card_number, cvv, full_name
}

// Verify payment signature
func VerifyPaymentSignature(payload, signature, secret string) bool {
    // VNPay signature format
    expected := fmt.Sprintf("%s|%s", payload, secret)
    hash := sha256.Sum256([]byte(expected))
    
    return hex.EncodeToString(hash[:]) == signature
}

// Payment webhook validation
func ValidateWebhook(provider string, headers map[string]string, body []byte) error {
    switch provider {
    case "VNPAY":
        signature := headers["Vnp_SecureHash"]
        if !VerifyVNPaySignature(body, signature) {
            return ErrInvalidSignature
        }
        
        // Verify IP (VNPay IPs only)
        ip := headers["X-Forwarded-For"]
        if !isAllowedVNPayIP(ip) {
            return ErrUnauthorizedIP
        }
    }
    return nil
}
```

### 5.2 Order Integrity

```go
// Create order with idempotency
func CreateOrder(userID string, req CreateOrderRequest) (*Order, error) {
    // Check for duplicate request
    idempotencyKey := req.IdempotencyKey
    if idempotencyKey != "" {
        existing, err := getOrderByIdempotencyKey(idempotencyKey)
        if err == nil && existing != nil {
            return existing, nil // Return existing order
        }
    }
    
    // Calculate totals server-side (never trust client prices)
    items, subtotal, discount, tax := calculateOrderTotals(req.Items)
    
    // Create order atomically
    tx, err := db.Begin()
    if err != nil {
        return nil, err
    }
    defer tx.Rollback()
    
    order := &Order{
        ID:             uuid.New().String(),
        UserID:         userID,
        OrderNumber:    generateOrderNumber(),
        Subtotal:       subtotal,
        Discount:       discount,
        Tax:            tax,
        Total:          subtotal - discount + tax,
        Items:          items,
        Status:         "PENDING",
        IdempotencyKey: idempotencyKey,
    }
    
    _, err = tx.Exec(`
        INSERT INTO orders (id, user_id, order_number, items, total, idempotency_key)
        VALUES ($1, $2, $3, $4, $5, $6)
    `, order.ID, order.UserID, order.OrderNumber, order.Items, order.Total, idempotencyKey)
    
    if err != nil {
        return nil, err
    }
    
    return order, tx.Commit()
}
```

---

## 6. AI Security

### 6.1 Content Filtering

```go
// Content moderation
type ContentModerator struct {
    openAI *openai.Client
}

func (cm *ContentModerator) CheckContent(text string) (*ModerationResult, error) {
    resp, err := cm.openAI.Moderations(context.Background(), &openai.ModerationInput{
        Input: text,
    })
    
    if err != nil {
        return nil, err
    }
    
    result := &ModerationResult{
        Flagged: false,
        Categories: make(map[string]bool),
    }
    
    if len(resp.Results) > 0 {
        result.Flagged = resp.Results[0].Flagged
        for category, flagged := range resp.Results[0].Categories {
            result.Categories[category] = flagged
        }
    }
    
    return result, nil
}

// Sanitize AI prompts
func SanitizePrompt(prompt string) string {
    // Remove potential prompt injection patterns
    patterns := []string{
        `(?i)ignore previous instructions`,
        `(?i)ignore all previous`,
        `(?i)disregard previous`,
        `(?i)forget previous`,
        `(?i)new instructions`,
        `(?i)system prompt`,
        `(?i)you are now`,
    }
    
    for _, pattern := range patterns {
        re := regexp.MustCompile(pattern)
        prompt = re.ReplaceAllString(prompt, "[filtered]")
    }
    
    return prompt
}
```

### 6.2 Output Sanitization

```go
// Sanitize AI output
func SanitizeOutput(output string) string {
    // Remove potential harmful content
    output = removePII(output)
    output = removeSecrets(output)
    output = removePhoneNumbers(output)
    output = removeEmails(output)
    
    return output
}

func removePII(text string) string {
    // Email
    emailRegex := regexp.MustCompile(`[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}`)
    text = emailRegex.ReplaceAllString(text, "[email redacted]")
    
    // Phone
    phoneRegex := regexp.MustCompile(`\+?[0-9]{10,15}`)
    text = phoneRegex.ReplaceAllString(text, "[phone redacted]")
    
    // SSN-like patterns
    ssnRegex := regexp.MustCompile(`\b\d{3}-\d{2}-\d{4}\b`)
    text = ssnRegex.ReplaceAllString(text, "[ssn redacted]")
    
    return text
}
```

---

## 7. Audit Logging

### 7.1 Audit Log Structure

```go
type AuditLog struct {
    ID            string                 `json:"id"`
    UserID        string                 `json:"user_id,omitempty"`
    ActorType     string                 `json:"actor_type"` // USER, ADMIN, SYSTEM
    Action        string                 `json:"action"`
    EntityType    string                 `json:"entity_type"`
    EntityID      string                 `json:"entity_id,omitempty"`
    OldValues     map[string]interface{} `json:"old_values,omitempty"`
    NewValues     map[string]interface{} `json:"new_values,omitempty"`
    IPAddress     string                 `json:"ip_address,omitempty"`
    UserAgent     string                 `json:"user_agent,omitempty"`
    Description   string                 `json:"description,omitempty"`
    CorrelationID string                 `json:"correlation_id,omitempty"`
    Timestamp     time.Time              `json:"timestamp"`
}

// Log all sensitive operations
func LogAudit(userID, action, entityType, entityID string, 
              oldValues, newValues map[string]interface{}) {
    
    // Get user context
    user, _ := GetUserContext()
    
    auditLog := &AuditLog{
        ID:          uuid.New().String(),
        UserID:      userID,
        ActorType:   "USER",
        Action:      action,
        EntityType:  entityType,
        EntityID:    entityID,
        OldValues:   sanitizeSensitiveFields(oldValues),
        NewValues:   sanitizeSensitiveFields(newValues),
        IPAddress:   GetClientIP(),
        UserAgent:   GetUserAgent(),
        Timestamp:   time.Now(),
    }
    
    // Store in PostgreSQL (async)
    go storeAuditLog(auditLog)
}

// Tracked actions
const (
    ActionLogin          = "user.login"
    ActionLogout         = "user.logout"
    ActionProfileUpdate  = "user.profile_update"
    ActionCreditPurchase = "credit.purchase"
    ActionCreditDeduct   = "credit.deduct"
    ActionCreditRefund   = "credit.refund"
    ActionOrderCreate    = "order.create"
    ActionOrderPay       = "order.payment"
    ActionMembershipChange = "membership.change"
    ActionPaymentMethodAdd = "payment_method.add"
    ActionPaymentMethodDelete = "payment_method.delete"
    ActionAdminUserModify = "admin.user_modify"
)
```

### 7.2 Sensitive Field Masking

```go
var sensitiveFields = map[string]bool{
    "password":         true,
    "password_hash":     true,
    "token":             true,
    "secret":            true,
    "api_key":           true,
    "private_key":       true,
    "credit_card_number": true,
    "cvv":               true,
    "ssn":               true,
    "phone":             true,
    "email":             true,
    "address":           true,
}

func sanitizeSensitiveFields(data map[string]interface{}) map[string]interface{} {
    sanitized := make(map[string]interface{})
    
    for key, value := range data {
        if sensitiveFields[strings.ToLower(key)] {
            sanitized[key] = "[REDACTED]"
        } else if nested, ok := value.(map[string]interface{}); ok {
            sanitized[key] = sanitizeSensitiveFields(nested)
        } else {
            sanitized[key] = value
        }
    }
    
    return sanitized
}
```

---

## 8. Infrastructure Security

### 8.1 Network Security

```yaml
# Kubernetes Network Policies
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-allow-only-ingress
spec:
  podSelector:
    matchLabels:
      app: api-service
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - namespaceSelector:
            matchLabels:
              name: ingress-nginx
        - podSelector:
            matchLabels:
              app: gateway
      ports:
        - protocol: TCP
          port: 8080
  egress:
    - to:
        - podSelector:
            matchLabels:
              app: postgres
      ports:
        - protocol: TCP
          port: 5432
    - to:
        - podSelector:
            matchLabels:
              app: redis
      ports:
        - protocol: TCP
          port: 6379
    - to:
        - namespaceSelector: {}
          podSelector:
            matchLabels:
              k8s-app: kube-dns
      ports:
        - protocol: UDP
          port: 53
```

### 8.2 Secrets Management

```yaml
# Kubernetes Secrets (encrypted at rest)
apiVersion: v1
kind: Secret
metadata:
  name: aicafe-secrets
  annotations:
    encryption.kubernetes.io/prebuilt-cipher: AES256
type: Opaque
data:
  # Database credentials (encrypted by Kubernetes)
  db-username: <base64-encoded>
  db-password: <base64-encoded>
  
  # API keys (encrypted)
  openai-api-key: <base64-encoded>
  anthropic-api-key: <base64-encoded>
  
  # JWT secrets (encrypted)
  jwt-private-key: <base64-encoded>
  jwt-public-key: <base64-encoded>
  
  # Encryption keys (encrypted)
  encryption-key-v1: <base64-encoded>
```

### 8.3 Container Security

```dockerfile
# Secure container image
FROM eclipse-temurin:17-jre-alpine

# Create non-root user
RUN addgroup -S aicafe && adduser -S aicafe -G aicafe

# Copy application
COPY --chown=aicafe:aicafe app.jar /app/app.jar

# Set permissions
RUN chmod 444 /app/app.jar

# Switch to non-root user
USER aicafe

# Run as non-root
ENTRYPOINT ["java", "-jar", "-Djava.security.manager", "/app/app.jar"]
```

---

## 9. Security Headers

### 9.1 Response Headers

```go
func SecurityHeadersMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        // Prevent XSS
        c.Header("X-XSS-Protection", "1; mode=block")
        c.Header("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self'; connect-src 'self' https://api.aicafe.vn wss://api.aicafe.vn")
        
        // Prevent clickjacking
        c.Header("X-Frame-Options", "DENY")
        c.Header("X-Content-Type-Options", "nosniff")
        
        // HSTS
        c.Header("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
        
        // Referrer policy
        c.Header("Referrer-Policy", "strict-origin-when-cross-origin")
        
        // Permissions policy
        c.Header("Permissions-Policy", "camera=(), microphone=(), geolocation=()")
        
        // Cache control for sensitive data
        c.Header("Cache-Control", "no-store, no-cache, must-revalidate, private")
        c.Header("Pragma", "no-cache")
        
        c.Next()
    }
}
```

### 9.2 CORS Configuration

```go
func CORSConfig() gin.HandlerFunc {
    return func(c *gin.Context) {
        c.Header("Access-Control-Allow-Origin", "https://aicafe.vn")
        c.Header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
        c.Header("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, X-API-Version")
        c.Header("Access-Control-Allow-Credentials", "true")
        c.Header("Access-Control-Max-Age", "86400")
        
        if c.Request.Method == "OPTIONS" {
            c.AbortWithStatus(204)
            return
        }
        
        c.Next()
    }
}
```

---

## 10. Security Monitoring

### 10.1 Alert Rules

```yaml
# Prometheus alert rules for security
groups:
  - name: security-alerts
    rules:
      # Multiple failed logins
      - alert: MultipleFailedLogins
        expr: increase(auth_failed_login_total[5m]) > 10
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Multiple failed login attempts detected"
          
      # Credit anomaly
      - alert: UnusualCreditDeduction
        expr: abs(rate(credit_transactions_total[1h]) - avg_over_time(rate(credit_transactions_total[1h])[7d:1h])) > 3 * stddev_over_time(rate(credit_transactions_total[1h])[7d:1h])
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Unusual credit deduction pattern detected"
          
      # Rate limit violations
      - alert: HighRateLimitViolations
        expr: increase(rate_limit_exceeded_total[5m]) > 100
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High rate of rate limit violations"
          
      # Suspicious IP activity
      - alert: SuspiciousIPActivity
        expr: sum by (ip) (increase(api_requests_total{status="403"}[5m])) > 50
        for: 3m
        labels:
          severity: critical
        annotations:
          summary: "Suspicious IP activity detected"
```

### 10.2 SIEM Integration

```go
// Send security events to SIEM
func SendToSIEM(event SecurityEvent) error {
    // Format for Elastic SIEM
    document := map[string]interface{}{
        "@timestamp": event.Timestamp,
        "event.kind": "event",
        "event.category": event.Category,
        "event.type": event.Type,
        "event.action": event.Action,
        
        "source.ip": event.SourceIP,
        "source.user.id": event.UserID,
        "source.user.name": event.Username,
        
        "destination.ip": event.DestIP,
        "destination.port": event.DestPort,
        
        "user_agent.original": event.UserAgent,
        "http.request.method": event.Method,
        "http.url.path": event.Path,
        
        "file.name": event.Filename,
        "file.hash.sha256": event.FileHash,
        
        "aicafe.event_id": event.ID,
        "aicafe.severity": event.Severity,
    }
    
    // Send to Elastic
    return elasticClient.Index("security-events", document)
}

// Security event types
const (
    SecurityEventLogin        = "authentication.login"
    SecurityEventLoginFail    = "authentication.login_failure"
    SecurityEventLogout       = "authentication.logout"
    SecurityEventTokenRefresh = "authentication.token_refresh"
    SecurityEventAPIKeyCreate = "credentials.api_key_create"
    SecurityEventAPIKeyDelete = "credentials.api_key_delete"
    SecurityEventDataExport   = "data.export"
    SecurityEventAdminAction  = "admin.action"
    SecurityEventPolicyChange = "policy.change"
)
```

---

## 11. Compliance

### 11.1 Data Retention

```sql
-- Automatic data retention enforcement
CREATE OR REPLACE FUNCTION enforce_data_retention()
RETURNS TRIGGER AS $$
BEGIN
    -- Delete logs older than 90 days
    DELETE FROM api_usage_logs 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '90 days';
    
    -- Delete OTPs older than 24 hours
    DELETE FROM otp_codes 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '24 hours';
    
    -- Anonymize deleted user data after 30 days
    UPDATE users 
    SET phone_encrypted = NULL,
        email_encrypted = NULL,
        name = 'Deleted User',
        avatar_url = NULL
    WHERE deleted_at < CURRENT_TIMESTAMP - INTERVAL '30 days'
      AND status = 'DELETED'
      AND phone_encrypted IS NOT NULL;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Run daily at 3 AM
SELECT cron.schedule('data-retention', '0 3 * * *', 'SELECT enforce_data_retention()');
```

### 11.2 GDPR Compliance

```go
// Right to be forgotten implementation
func DeleteUserData(userID string) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()
    
    // Delete in correct order (respect foreign keys)
    tables := []string{
        "audit_logs",           // Log first
        "api_usage_logs",
        "sessions",
        "otp_codes",
        "order_items",
        "orders",
        "credit_transactions",
        "daily_usage",
        "redemptions",
        "loyalty_transactions",
        "subscription_history",
        "billing_cycles",
        "payment_methods",
        "memberships",
        "loyalty_accounts",
        "user_preferences",
        "wallets",
        "users",
    }
    
    for _, table := range tables {
        _, err = tx.Exec(fmt.Sprintf("DELETE FROM %s WHERE user_id = $1", table), userID)
        if err != nil {
            return fmt.Errorf("failed to delete from %s: %w", table, err)
        }
    }
    
    // Log deletion request
    logAudit(nil, "user.data_deleted", "user", userID, nil, map[string]interface{}{
        "user_id": userID,
        "deleted_tables": tables,
    })
    
    return tx.Commit()
}

// Data export (right to data portability)
func ExportUserData(userID string) (*UserDataExport, error) {
    export := &UserDataExport{
        UserID: userID,
        ExportDate: time.Now(),
    }
    
    // Export user profile
    db.QueryRow(`SELECT * FROM users WHERE id = $1`, userID).Scan(&export.User)
    
    // Export preferences
    db.QueryRow(`SELECT * FROM user_preferences WHERE user_id = $1`, userID).Scan(&export.Preferences)
    
    // Export orders (last 2 years)
    db.Select(&export.Orders, `
        SELECT * FROM orders 
        WHERE user_id = $1 
          AND created_at > CURRENT_TIMESTAMP - INTERVAL '2 years'`, userID)
    
    // Export credit history
    db.Select(&export.CreditHistory, `
        SELECT * FROM credit_transactions 
        WHERE user_id = $1 
          AND created_at > CURRENT_TIMESTAMP - INTERVAL '2 years'`, userID)
    
    return export, nil
}
```

---

## 12. Security Checklist

### 12.1 Pre-Deployment

- [ ] All secrets in Vault/Environment variables
- [ ] TLS 1.2+ enforced
- [ ] Security headers configured
- [ ] Rate limiting enabled
- [ ] Input validation complete
- [ ] SQL injection prevention verified
- [ ] XSS protection enabled
- [ ] CSRF tokens implemented
- [ ] Audit logging enabled
- [ ] Error messages sanitized

### 12.2 Post-Deployment

- [ ] Security scan completed
- [ ] Penetration test passed
- [ ] Dependencies audited
- [ ] WAF rules active
- [ ] Monitoring alerts configured
- [ ] Backup verified
- [ ] Incident response plan tested
- [ ] Team trained on security
