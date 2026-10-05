# Database Schema Design

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DATABASE ARCHITECTURE                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                           PostgreSQL 15+                                     │   │
│  │                                                                             │   │
│  │   ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │   │
│  │   │   USERS SCHEMA  │  │  CREDIT SCHEMA │  │  ORDER SCHEMA  │            │   │
│  │   │                 │  │                 │  │                 │            │   │
│  │   │  • users        │  │  • wallets     │  │  • orders      │            │   │
│  │   │  • user_pref    │  │  • transactions│  │  • order_items │            │   │
│  │   │  • sessions     │  │  • daily_usage │  │  • invoices    │            │   │
│  │   │                 │  │                 │  │                 │            │   │
│  │   └─────────────────┘  └─────────────────┘  └─────────────────┘            │   │
│  │                                                                             │   │
│  │   ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │   │
│  │   │MEMBERSHIP SCHEMA│  │  LOYALTY SCHEMA│  │  ANALYTICS     │            │   │
│  │   │                 │  │                 │  │  SCHEMA        │            │   │
│  │   │  • member-     │  │  • loyalty_    │  │  • usage_logs  │            │   │
│  │   │    ships       │  │    accounts   │  │  • api_logs    │            │   │
│  │   │  • sub_history │  │  • loyalty_   │  │  • audit_logs  │            │   │
│  │   │  • billing_    │  │    txns       │  │  • events      │            │   │
│  │   │    cycles      │  │  • rewards    │  │                 │            │   │
│  │   │  • payment_    │  │  • referrals  │  │                 │            │   │
│  │   │    methods     │  │                 │  │                 │            │   │
│  │   └─────────────────┘  └─────────────────┘  └─────────────────┘            │   │
│  │                                                                             │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                              Redis (Cache & Session)                         │   │
│  │                                                                             │   │
│  │   Session Store      │  Rate Limiting      │  Response Cache              │   │
│  │   ─────────────     │  ──────────────     │  ─────────────              │   │
│  │   • auth:{user_id}  │  • ratelimit:{id}  │  • ai:{cache_key}           │   │
│  │   • refresh:{token} │                     │                             │   │
│  │   • ws:{session}    │                     │                             │   │
│  │                                                                             │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. PostgreSQL Schema

### 2.1 Users Schema

```sql
-- =====================================================
-- USERS SCHEMA
-- Managed by: Java User Service
-- =====================================================

-- Main users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    avatar_url VARCHAR(500),
    
    -- Tier management (from membership)
    tier VARCHAR(20) NOT NULL DEFAULT 'BASIC' 
        CHECK (tier IN ('BASIC', 'DEVELOPER', 'PRO', 'BUILDER')),
    
    -- Account status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED', 'PENDING')),
    
    -- Verification
    phone_verified BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    -- Constraints
    CONSTRAINT users_phone_format CHECK (phone ~ '^\+?[0-9]{10,15}$'),
    CONSTRAINT users_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Indexes for users
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_tier ON users(tier);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_users_last_login ON users(last_login_at DESC);

-- User preferences (1:1 relationship)
CREATE TABLE user_preferences (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    
    -- Notifications
    notify_email BOOLEAN DEFAULT TRUE,
    notify_sms BOOLEAN DEFAULT TRUE,
    notify_push BOOLEAN DEFAULT TRUE,
    
    -- Privacy
    profile_public BOOLEAN DEFAULT FALSE,
    show_usage_stats BOOLEAN DEFAULT TRUE,
    
    -- Language & Region
    language VARCHAR(10) DEFAULT 'vi',
    timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    currency VARCHAR(3) DEFAULT 'VND',
    
    -- AI Preferences
    ai_model_preference VARCHAR(50) DEFAULT 'gpt-4o',
    ai_temperature DECIMAL(3,2) DEFAULT 0.7,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Metadata (theme, shortcuts, etc.)
    settings JSONB DEFAULT '{}'
);

CREATE INDEX idx_user_prefs_language ON user_preferences(language);
CREATE INDEX idx_user_prefs_timezone ON user_preferences(timezone);

-- Sessions table (for tracking active sessions)
CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Session info
    refresh_token_hash VARCHAR(255) NOT NULL,
    device_info JSONB,
    ip_address INET,
    user_agent TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    revoked_at TIMESTAMP WITH TIME ZONE,
    revoked_reason VARCHAR(100),
    
    -- Constraints
    CONSTRAINT sessions_user_id_fk FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_sessions_refresh_hash ON user_sessions(refresh_token_hash);
CREATE INDEX idx_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX idx_sessions_active ON user_sessions(user_id, is_active) WHERE is_active = TRUE;

-- OTP codes (for auth - managed by Go Auth Service)
CREATE TABLE otp_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    
    -- Type
    type VARCHAR(20) NOT NULL DEFAULT 'LOGIN'
        CHECK (type IN ('LOGIN', 'REGISTER', 'RESET_PASSWORD', 'CHANGE_PHONE')),
    
    -- Status
    attempts INTEGER DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    
    -- Rate limiting
    ip_address INET,
    
    -- Constraints
    CONSTRAINT otp_code_length CHECK (LENGTH(code) >= 4 AND LENGTH(code) <= 8)
);

CREATE INDEX idx_otp_phone ON otp_codes(phone);
CREATE INDEX idx_otp_phone_created ON otp_codes(phone, created_at DESC);
CREATE INDEX idx_otp_expires ON otp_codes(expires_at);
CREATE INDEX idx_otp_type ON otp_codes(type, phone);
```

### 2.2 Credit Schema (Managed by Go Credit Service)

```sql
-- =====================================================
-- CREDIT SCHEMA
-- Managed by: Go Credit Service
-- Note: High-performance operations, uses optimistic locking
-- =====================================================

-- User wallets
CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Balances
    credits INTEGER NOT NULL DEFAULT 0 CHECK (credits >= 0),
    workspace_minutes INTEGER NOT NULL DEFAULT 0 CHECK (workspace_minutes >= 0),
    
    -- Credit packages (credits with expiration)
    credit_packages JSONB DEFAULT '[]',
    /* Format: [
        {
            "package_id": "uuid",
            "amount": 1000,
            "expires_at": "2024-12-31T23:59:59Z",
            "purchased_at": "2024-01-15T10:30:00Z"
        }
    ] */
    
    -- Version for optimistic locking
    version INTEGER NOT NULL DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Last activity
    last_transaction_at TIMESTAMP WITH TIME ZONE,
    
    -- Constraints
    CONSTRAINT wallets_credits_positive CHECK (credits >= 0),
    CONSTRAINT wallets_minutes_positive CHECK (workspace_minutes >= 0)
);

CREATE UNIQUE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_wallets_updated_at ON wallets(updated_at DESC);

-- Credit transactions (append-only for audit)
CREATE TABLE credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Transaction type
    type VARCHAR(30) NOT NULL
        CHECK (type IN (
            'PURCHASE',        -- Credits purchased
            'DEDUCT',          -- Credits spent
            'REFUND',          -- Credits refunded
            'BONUS',           -- Promotional credits
            'EXPIRE',          -- Credits expired
            'TRANSFER_IN',     -- Credits transferred in
            'TRANSFER_OUT',     -- Credits transferred out
            'ADJUSTMENT',       -- Manual adjustment
            'SUBSCRIPTION_ADD' -- Monthly subscription credits
        )),
    
    -- Amount (can be negative for deductions)
    amount INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    
    -- Source reference
    source_type VARCHAR(30),  -- ORDER, SUBSCRIPTION, PROMO, REFERRAL, ADMIN
    source_id UUID,
    
    -- AI service that consumed credits (if deduct)
    service VARCHAR(50),  -- CHAT_GPT4, CLAUDE, DALL_E, etc.
    model VARCHAR(100),
    
    -- Description
    description TEXT,
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_txn_user_id ON credit_transactions(user_id);
CREATE INDEX idx_credit_txn_type ON credit_transactions(type);
CREATE INDEX idx_credit_txn_created_at ON credit_transactions(created_at DESC);
CREATE INDEX idx_credit_txn_source ON credit_transactions(source_type, source_id);
CREATE INDEX idx_credit_txn_user_date ON credit_transactions(user_id, created_at DESC);
CREATE INDEX idx_credit_txn_service ON credit_transactions(service, created_at DESC);

-- Daily usage tracking (for rate limiting)
CREATE TABLE daily_usage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Date
    date DATE NOT NULL,
    
    -- Usage counts by service
    chat_requests INTEGER DEFAULT 0,
    image_requests INTEGER DEFAULT 0,
    video_requests INTEGER DEFAULT 0,
    
    -- Credits consumed by service
    chat_credits INTEGER DEFAULT 0,
    image_credits INTEGER DEFAULT 0,
    video_credits INTEGER DEFAULT 0,
    
    -- Total
    total_credits INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint
    CONSTRAINT daily_usage_user_date UNIQUE (user_id, date)
);

CREATE INDEX idx_daily_usage_user_id ON daily_usage(user_id);
CREATE INDEX idx_daily_usage_date ON daily_usage(date DESC);
CREATE INDEX idx_daily_usage_user_date ON daily_usage(user_id, date DESC);

-- Credit packages (templates)
CREATE TABLE credit_packages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Package info
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INTEGER NOT NULL CHECK (credits > 0),
    price DECIMAL(12,2) NOT NULL CHECK (price >= 0),
    
    -- Validity
    validity_days INTEGER NOT NULL DEFAULT 30,
    
    -- Bonus
    bonus_credits INTEGER DEFAULT 0,
    bonus_percentage INTEGER DEFAULT 0,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_packages_active ON credit_packages(is_active, sort_order);

-- Credit pricing (cost per AI operation)
CREATE TABLE ai_credit_pricing (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Service & model
    service VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    
    -- Pricing
    cost_per_1k_input_tokens DECIMAL(10,4) DEFAULT 0,
    cost_per_1k_output_tokens DECIMAL(10,4) DEFAULT 0,
    cost_per_request DECIMAL(10,4) DEFAULT 0,
    cost_per_image DECIMAL(10,4) DEFAULT 0,
    cost_per_second DECIMAL(10,4) DEFAULT 0, -- For video
    
    -- Credit cost (calculated from price)
    input_credits_per_1k_tokens INTEGER DEFAULT 1,
    output_credits_per_1k_tokens INTEGER DEFAULT 1,
    credits_per_request INTEGER DEFAULT 1,
    credits_per_image INTEGER DEFAULT 50,
    credits_per_video_second INTEGER DEFAULT 100,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint
    CONSTRAINT ai_pricing_service_model UNIQUE (service, model)
);

CREATE INDEX idx_ai_pricing_service ON ai_credit_pricing(service, is_active);

-- Insert default pricing
INSERT INTO ai_credit_pricing (service, model, input_credits_per_1k_tokens, output_credits_per_1k_tokens, credits_per_request, credits_per_image, credits_per_video_second) VALUES
('chat', 'gpt-4o', 1, 1, 0, 0, 0),
('chat', 'gpt-4o-mini', 0, 0, 0, 0, 0),
('chat', 'claude-3-5-sonnet', 1, 1, 0, 0, 0),
('chat', 'claude-3-5-haiku', 0, 0, 0, 0, 0),
('chat', 'gemini-1.5-pro', 0, 0, 0, 0, 0),
('chat', 'gemini-1.5-flash', 0, 0, 0, 0, 0),
('image', 'dall-e-3', 0, 0, 0, 50, 0),
('image', 'stable-diffusion-xl', 0, 0, 0, 10, 0),
('video', 'sora', 0, 0, 0, 0, 100),
('video', 'stable-video', 0, 0, 0, 0, 50);
```

### 2.3 Membership Schema

```sql
-- =====================================================
-- MEMBERSHIP SCHEMA
-- Managed by: Java Membership Service
-- =====================================================

-- Membership tiers configuration
CREATE TABLE membership_tiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Tier info
    tier VARCHAR(20) UNIQUE NOT NULL
        CHECK (tier IN ('BASIC', 'DEVELOPER', 'PRO', 'BUILDER')),
    display_name VARCHAR(50) NOT NULL,
    description TEXT,
    
    -- Benefits
    monthly_credits INTEGER NOT NULL,
    monthly_workspace_minutes INTEGER NOT NULL,
    rate_limit_rpm INTEGER NOT NULL,
    concurrent_connections INTEGER NOT NULL,
    
    -- Features
    features JSONB DEFAULT '[]',
    /* Format: [
        "feature_1",
        "feature_2"
    ] */
    
    -- Priority (for ordering)
    priority INTEGER NOT NULL DEFAULT 0,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert default tiers
INSERT INTO membership_tiers (tier, display_name, description, monthly_credits, monthly_workspace_minutes, rate_limit_rpm, concurrent_connections, priority, features) VALUES
('BASIC', 'Basic', 'Bắt đầu với gói cơ bản', 2000, 60, 10, 2, 1, '["chat", "image"]'),
('DEVELOPER', 'Developer', 'Dành cho lập trình viên', 5000, 120, 30, 5, 2, '["chat", "image", "code"]'),
('PRO', 'Pro', 'Gói chuyên nghiệp', 10000, 240, 60, 10, 3, '["chat", "image", "video", "code", "priority_support"]'),
('BUILDER', 'Builder', 'Dành cho doanh nghiệp', 18000, 480, 120, 20, 4, '["chat", "image", "video", "code", "priority_support", "dedicated_account_manager"]');

-- User memberships (active subscriptions)
CREATE TABLE memberships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Tier
    tier VARCHAR(20) NOT NULL REFERENCES membership_tiers(tier),
    
    -- Period
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    current_period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    current_period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    
    -- Auto-renewal
    auto_renew BOOLEAN DEFAULT TRUE,
    canceled_at TIMESTAMP WITH TIME ZONE,
    
    -- Payment
    payment_method VARCHAR(20),
    last_payment_id UUID,
    
    -- Credits granted this period
    credits_granted INTEGER DEFAULT 0,
    credits_remaining INTEGER DEFAULT 0,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'PAUSED', 'CANCELED', 'EXPIRED', 'PENDING')),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_memberships_user_id ON memberships(user_id);
CREATE INDEX idx_memberships_tier ON memberships(tier);
CREATE INDEX idx_memberships_status ON memberships(status);
CREATE INDEX idx_memberships_period_end ON memberships(current_period_end);
CREATE INDEX idx_memberships_auto_renew ON memberships(auto_renew) WHERE auto_renew = TRUE;

-- Subscription history
CREATE TABLE subscription_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    membership_id UUID REFERENCES memberships(id) ON DELETE SET NULL,
    
    -- Action
    action VARCHAR(30) NOT NULL
        CHECK (action IN (
            'SUBSCRIBED', 'RENEWED', 'UPGRADED', 'DOWNGRADED',
            'CANCELED', 'EXPIRED', 'PAUSED', 'RESUMED'
        )),
    
    -- From/To
    from_tier VARCHAR(20),
    to_tier VARCHAR(20),
    
    -- Payment
    payment_id UUID,
    amount DECIMAL(12,2),
    
    -- Details
    description TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sub_history_user_id ON subscription_history(user_id);
CREATE INDEX idx_sub_history_membership_id ON subscription_history(membership_id);
CREATE INDEX idx_sub_history_action ON subscription_history(action);
CREATE INDEX idx_sub_history_created_at ON subscription_history(created_at DESC);

-- Billing cycles
CREATE TABLE billing_cycles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    membership_id UUID REFERENCES memberships(id) ON DELETE SET NULL,
    
    -- Period
    period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'ACTIVE', 'COMPLETED', 'FAILED', 'REFUNDED')),
    
    -- Amount
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'VND',
    
    -- Payment
    payment_id UUID,
    payment_method VARCHAR(20),
    paid_at TIMESTAMP WITH TIME ZONE,
    
    -- Credits
    credits_included INTEGER,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_billing_user_id ON billing_cycles(user_id);
CREATE INDEX idx_billing_membership_id ON billing_cycles(membership_id);
CREATE INDEX idx_billing_status ON billing_cycles(status);
CREATE INDEX idx_billing_period_end ON billing_cycles(period_end);

-- Payment methods (for saved cards)
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Provider
    provider VARCHAR(20) NOT NULL
        CHECK (provider IN ('VNPAY', 'MOMO', 'ZALOPAY', 'CARD')),
    
    -- Card details (encrypted)
    card_type VARCHAR(20),  -- VISA, MASTER, JCB
    last_four VARCHAR(4),
    expiry_month INTEGER,
    expiry_year INTEGER,
    card_holder_name VARCHAR(100),
    
    -- Provider reference
    provider_token TEXT,
    provider_customer_id TEXT,
    
    -- Status
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pay_methods_user_id ON payment_methods(user_id);
CREATE INDEX idx_pay_methods_provider ON payment_methods(provider);
CREATE INDEX idx_pay_methods_default ON payment_methods(user_id, is_default) WHERE is_default = TRUE;
```

### 2.4 Order Schema

```sql
-- =====================================================
-- ORDER SCHEMA
-- Managed by: Java Order Service
-- =====================================================

-- Orders
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Order info
    order_number VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL
        CHECK (type IN ('CREDIT_PURCHASE', 'MEMBERSHIP_SUBSCRIPTION', 'CREDIT_TOPUP', 'UPGRADE')),
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN (
            'PENDING', 'PROCESSING', 'COMPLETED', 'FAILED',
            'CANCELED', 'REFUNDED', 'PARTIAL_REFUND'
        )),
    
    -- Amount
    subtotal DECIMAL(12,2) NOT NULL,
    discount DECIMAL(12,2) DEFAULT 0,
    tax DECIMAL(12,2) DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'VND',
    
    -- Payment
    payment_method VARCHAR(20),
    payment_id VARCHAR(100),
    payment_status VARCHAR(20) DEFAULT 'PENDING'
        CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    paid_at TIMESTAMP WITH TIME ZONE,
    
    -- Delivery
    delivered BOOLEAN DEFAULT FALSE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    delivery_description TEXT,
    
    -- Items (JSONB for flexibility)
    items JSONB NOT NULL DEFAULT '[]',
    /* Format: [
        {
            "item_id": "uuid",
            "type": "CREDITS",
            "name": "1000 Credits",
            "quantity": 1,
            "unit_price": 99000,
            "total": 99000,
            "metadata": {}
        }
    ] */
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    -- Coupon applied
    coupon_id UUID,
    coupon_code VARCHAR(50),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_type ON orders(type);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_payment_id ON orders(payment_id);

-- Order items (normalized version)
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Item info
    item_type VARCHAR(30) NOT NULL,
    item_id UUID,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Quantity & pricing
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    
    -- Delivery
    delivered BOOLEAN DEFAULT FALSE,
    delivered_quantity INTEGER DEFAULT 0,
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_type ON order_items(item_type);

-- Invoices
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Invoice number
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_type VARCHAR(20) DEFAULT 'INVOICE',
    
    -- Business info
    business_name VARCHAR(255),
    business_address TEXT,
    business_tax_code VARCHAR(20),
    
    -- Customer info
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(20),
    customer_address TEXT,
    
    -- Amount
    subtotal DECIMAL(12,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(12,2) DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    total_in_words VARCHAR(255),
    currency VARCHAR(3) DEFAULT 'VND',
    
    -- Payment
    payment_status VARCHAR(20) DEFAULT 'UNPAID',
    paid_at TIMESTAMP WITH TIME ZONE,
    
    -- PDF
    pdf_url VARCHAR(500),
    
    -- Timestamps
    issue_date DATE NOT NULL,
    due_date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_invoices_user_id ON invoices(user_id);
CREATE INDEX idx_invoices_number ON invoices(invoice_number);
CREATE INDEX idx_invoices_issue_date ON invoices(issue_date DESC);

-- Coupons/Promotions
CREATE TABLE coupons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Code
    code VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100),
    description TEXT,
    
    -- Type
    type VARCHAR(20) NOT NULL
        CHECK (type IN ('PERCENTAGE', 'FIXED_AMOUNT', 'FREE_CREDITS', 'BUNDLE')),
    
    -- Discount value
    discount_value DECIMAL(12,2),
    discount_percentage INTEGER,
    free_credits INTEGER,
    
    -- Conditions
    min_order_amount DECIMAL(12,2) DEFAULT 0,
    max_discount_amount DECIMAL(12,2),
    applicable_tiers VARCHAR(20)[] DEFAULT ARRAY['BASIC', 'DEVELOPER', 'PRO', 'BUILDER'],
    applicable_packages UUID[],
    
    -- Limits
    total_uses INTEGER,
    max_uses_per_user INTEGER DEFAULT 1,
    current_uses INTEGER DEFAULT 0,
    
    -- Validity
    starts_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_public BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(is_active, starts_at, expires_at);

-- Coupon usage tracking
CREATE TABLE coupon_usage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id UUID NOT NULL REFERENCES coupons(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    
    discount_amount DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupon_usage_coupon_id ON coupon_usage(coupon_id);
CREATE INDEX idx_coupon_usage_user_id ON coupon_usage(user_id);
CREATE UNIQUE INDEX idx_coupon_usage_user_coupon ON coupon_usage(user_id, coupon_id);
```

### 2.5 Loyalty Schema

```sql
-- =====================================================
-- LOYALTY SCHEMA
-- Managed by: Java Loyalty Service
-- =====================================================

-- Loyalty program tiers
CREATE TABLE loyalty_tiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    tier VARCHAR(20) UNIQUE NOT NULL
        CHECK (tier IN ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM')),
    display_name VARCHAR(50) NOT NULL,
    description TEXT,
    
    -- Requirements
    min_lifetime_points INTEGER DEFAULT 0,
    min_orders INTEGER DEFAULT 0,
    
    -- Benefits
    earn_multiplier DECIMAL(3,2) DEFAULT 1.00,  -- Points multiplier
    redeem_multiplier DECIMAL(3,2) DEFAULT 1.00,  -- Value multiplier
    monthly_bonus_points INTEGER DEFAULT 0,
    
    -- Benefits list
    benefits JSONB DEFAULT '[]',
    
    -- Priority
    priority INTEGER NOT NULL DEFAULT 0,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert default loyalty tiers
INSERT INTO loyalty_tiers (tier, display_name, description, min_lifetime_points, earn_multiplier, redeem_multiplier, priority, benefits) VALUES
('BRONZE', 'Bronze', 'Thành viên Bronze', 0, 1.00, 1.00, 1, '["basic_benefits"]'),
('SILVER', 'Silver', 'Thành viên Bạc', 5000, 1.25, 1.10, 2, '["basic_benefits", "silver_benefits"]'),
('GOLD', 'Gold', 'Thành viên Vàng', 20000, 1.50, 1.25, 3, '["basic_benefits", "silver_benefits", "gold_benefits"]'),
('PLATINUM', 'Platinum', 'Thành viên Bạch Kim', 50000, 2.00, 1.50, 4, '["all_benefits"]');

-- Loyalty accounts
CREATE TABLE loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Points
    points INTEGER NOT NULL DEFAULT 0 CHECK (points >= 0),
    lifetime_points INTEGER NOT NULL DEFAULT 0 CHECK (lifetime_points >= 0),
    
    -- Tier
    tier VARCHAR(20) NOT NULL DEFAULT 'BRONZE'
        REFERENCES loyalty_tiers(tier) DEFAULT 'BRONZE',
    tier_updated_at TIMESTAMP WITH TIME ZONE,
    
    -- Stats
    total_orders INTEGER DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0,
    
    -- Benefits
    available_rewards JSONB DEFAULT '[]',
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    suspended_at TIMESTAMP WITH TIME ZONE,
    suspension_reason VARCHAR(255),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_account_user_id ON loyalty_accounts(user_id);
CREATE INDEX idx_loyalty_account_tier ON loyalty_accounts(tier);
CREATE INDEX idx_loyalty_account_points ON loyalty_accounts(points DESC);

-- Loyalty transactions
CREATE TABLE loyalty_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES loyalty_accounts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Transaction type
    type VARCHAR(20) NOT NULL
        CHECK (type IN ('EARN', 'REDEEM', 'EXPIRE', 'ADJUSTMENT', 'BONUS', 'REFUND')),
    
    -- Points
    points INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    
    -- Source
    source_type VARCHAR(30),  -- ORDER, PROMO, REFERRAL, ADMIN, EXPIRY
    source_id UUID,
    
    -- For EARN transactions
    order_id UUID,
    order_amount DECIMAL(12,2),
    
    -- For REDEEM transactions
    reward_id UUID,
    reward_name VARCHAR(255),
    
    -- Description
    description TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_txn_account_id ON loyalty_transactions(account_id);
CREATE INDEX idx_loyalty_txn_user_id ON loyalty_transactions(user_id);
CREATE INDEX idx_loyalty_txn_type ON loyalty_transactions(type);
CREATE INDEX idx_loyalty_txn_created_at ON loyalty_transactions(created_at DESC);
CREATE INDEX idx_loyalty_txn_source ON loyalty_transactions(source_type, source_id);

-- Rewards catalog
CREATE TABLE rewards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Reward info
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    
    -- Value
    points_cost INTEGER NOT NULL CHECK (points_cost > 0),
    monetary_value DECIMAL(12,2),  -- Approximate cash value
    
    -- Redemption
    type VARCHAR(30) NOT NULL
        CHECK (type IN ('CREDIT', 'VOUCHER', 'MERCHANDISE', 'SUBSCRIPTION', 'GIFT_CARD')),
    
    -- Terms
    terms TEXT,
    usage_instructions TEXT,
    
    -- Availability
    stock INTEGER,  -- NULL = unlimited
    total_redeemed INTEGER DEFAULT 0,
    
    -- Validity
    valid_from TIMESTAMP WITH TIME ZONE,
    valid_until TIMESTAMP WITH TIME ZONE,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rewards_active ON rewards(is_active, sort_order);
CREATE INDEX idx_rewards_type ON rewards(type);
CREATE INDEX idx_rewards_points_cost ON rewards(points_cost);

-- Redemptions
CREATE TABLE redemptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_id UUID NOT NULL REFERENCES loyalty_accounts(id) ON DELETE CASCADE,
    reward_id UUID NOT NULL REFERENCES rewards(id),
    
    -- Points
    points_spent INTEGER NOT NULL,
    
    -- Code/Value
    redemption_code VARCHAR(100),
    redemption_value DECIMAL(12,2),
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'COMPLETED', 'EXPIRED', 'CANCELED', 'USED')),
    
    -- Delivery
    delivered BOOLEAN DEFAULT FALSE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    delivery_method VARCHAR(30),
    delivery_info JSONB,
    
    -- Usage
    used_at TIMESTAMP WITH TIME ZONE,
    used_for_order_id UUID,
    
    -- Expiry
    expires_at TIMESTAMP WITH TIME ZONE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_redemptions_user_id ON redemptions(user_id);
CREATE INDEX idx_redemptions_reward_id ON redemptions(reward_id);
CREATE INDEX idx_redemptions_status ON redemptions(status);
CREATE INDEX idx_redemptions_expires ON redemptions(expires_at);

-- Referrals
CREATE TABLE referrals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Referrer (who refers)
    referrer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Referral code
    code VARCHAR(20) UNIQUE NOT NULL,
    
    -- Rewards
    referrer_reward_points INTEGER NOT NULL DEFAULT 500,
    referee_reward_points INTEGER NOT NULL DEFAULT 500,
    referrer_reward_credits INTEGER NOT NULL DEFAULT 0,
    
    -- Limits
    max_referrals INTEGER,  -- NULL = unlimited
    current_referrals INTEGER DEFAULT 0,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_referrals_referrer_id ON referrals(referrer_id);
CREATE INDEX idx_referrals_code ON referrals(code);

-- Referral uses
CREATE TABLE referral_uses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referral_id UUID NOT NULL REFERENCES referrals(id) ON DELETE CASCADE,
    referrer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    referee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELED')),
    
    -- When referee makes first purchase
    referee_first_order_id UUID,
    completed_at TIMESTAMP WITH TIME ZONE,
    
    -- Rewards given
    referrer_points_awarded INTEGER DEFAULT 0,
    referee_points_awarded INTEGER DEFAULT 0,
    referrer_credits_awarded INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_referral_uses_referee ON referral_uses(referee_id);
CREATE INDEX idx_referral_uses_referrer_id ON referral_uses(referrer_id);
CREATE INDEX idx_referral_uses_status ON referral_uses(status);
```

### 2.6 Analytics Schema

```sql
-- =====================================================
-- ANALYTICS SCHEMA
-- Used by: All services for logging
-- =====================================================

-- API usage logs
CREATE TABLE api_usage_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- User & session
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    session_id UUID,
    
    -- Request info
    service VARCHAR(50) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    
    -- AI specific
    ai_model VARCHAR(100),
    input_tokens INTEGER,
    output_tokens INTEGER,
    processing_time_ms INTEGER,
    
    -- Cost & credits
    credits_used INTEGER DEFAULT 0,
    cost_usd DECIMAL(10,4),
    
    -- Response
    status_code INTEGER,
    error_message TEXT,
    
    -- Context
    ip_address INET,
    user_agent TEXT,
    request_id VARCHAR(100),
    trace_id VARCHAR(100),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Partitioning by month for performance
CREATE INDEX idx_api_logs_user_id ON api_usage_logs(user_id);
CREATE INDEX idx_api_logs_service ON api_usage_logs(service);
CREATE INDEX idx_api_logs_created_at ON api_usage_logs(created_at DESC);
CREATE INDEX idx_api_logs_ai_model ON api_usage_logs(ai_model, created_at DESC);

-- Audit logs
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Actor
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    actor_type VARCHAR(20) DEFAULT 'USER' CHECK (actor_type IN ('USER', 'ADMIN', 'SYSTEM')),
    
    -- Action
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    
    -- Changes
    old_values JSONB,
    new_values JSONB,
    
    -- Context
    ip_address INET,
    user_agent TEXT,
    
    -- Description
    description TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);

-- Events (for event sourcing)
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Event type
    event_type VARCHAR(100) NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id UUID NOT NULL,
    
    -- Versioning
    version INTEGER NOT NULL DEFAULT 1,
    
    -- Event data
    event_data JSONB NOT NULL,
    
    -- Metadata
    correlation_id UUID,
    causation_id UUID,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_aggregate ON events(aggregate_type, aggregate_id);
CREATE INDEX idx_events_type ON events(event_type);
CREATE INDEX idx_events_created_at ON events(created_at DESC);
```

---

## 3. Redis Data Structures

### 3.1 Session & Auth

```redis
# Session data (Hash)
HSET session:{user_id}:{session_id}
    refresh_token_hash "hashed_value"
    device_info '{"type":"mobile","os":"iOS"}'
    ip_address "192.168.1.1"
    created_at "2024-01-15T10:30:00Z"
    expires_at "2024-01-22T10:30:00Z"
EXPIRE session:{user_id}:{session_id} 604800  ; 7 days

# Active sessions list
SADD user:{user_id}:sessions {session_id}
EXPIRE user:{user_id}:sessions 604800

# Revoked tokens (Set)
SADD revoked_tokens {token_jti}
EXPIRE revoked_tokens 86400  ; 24 hours after token expiry

# OTP codes
SET otp:{phone}:{code} {user_id} EX 300  ; 5 minutes
INCR otp:attempts:{phone}
EXPIRE otp:attempts:{phone} 300

# Rate limiting
INCR ratelimit:{user_id}:{endpoint}:{minute}
EXPIRE ratelimit:{user_id}:{endpoint}:{minute} 60
```

### 3.2 Credit Cache

```redis
# Balance cache
SET credit:balance:{user_id} "{credits}:{workspace_minutes}:{version}" EX 300

# Daily usage
INCR credit:daily:{user_id}:{date}:{service} {amount}
EXPIRE credit:daily:{user_id}:{date}:{service} 86400

# Credit packages (expiring)
ZADD credit:expiring:{user_id} {timestamp} "{package_id}:{amount}"
```

### 3.3 AI Response Cache

```redis
# AI response cache (for identical requests)
SET ai:cache:{hash} "{response}" EX 3600  ; 1 hour

# Rate limiting by model
INCR ai:ratelimit:{user_id}:{model}:{minute}
EXPIRE ai:ratelimit:{user_id}:{model}:{minute} 60
```

---

## 4. Database Migrations

### 4.1 Migration Tool: Flyway

```yaml
# Java services use Flyway for migrations
# Location: src/main/resources/db/migration/

# V1__init_schema.sql - Core tables
# V2__add_credit_tables.sql - Credit schema
# V3__add_membership_tables.sql - Membership schema
# V4__add_order_tables.sql - Order schema
# V5__add_loyalty_tables.sql - Loyalty schema
# V6__add_analytics_tables.sql - Analytics schema
```

### 4.2 Go Services - Direct SQL

```go
// Go services use raw SQL with sqlx
// Migrations managed by api-gateway startup

const migrations = `
-- Migration: 001_init
CREATE TABLE IF NOT EXISTS wallets (...);

-- Migration: 002_credit_tables
CREATE TABLE IF NOT EXISTS credit_transactions (...);
`
```

---

## 5. Performance Optimizations

### 5.1 Indexing Strategy

```sql
-- Composite indexes for common queries
CREATE INDEX idx_orders_user_status_date ON orders(user_id, status, created_at DESC);
CREATE INDEX idx_credit_txn_user_type_date ON credit_transactions(user_id, type, created_at DESC);
CREATE INDEX idx_loyalty_txn_account_created ON loyalty_transactions(account_id, created_at DESC);

-- Partial indexes for active records
CREATE INDEX idx_users_active ON users(created_at DESC) WHERE status = 'ACTIVE';
CREATE INDEX idx_memberships_active ON memberships(current_period_end) WHERE status = 'ACTIVE';

-- Covering indexes for read optimization
CREATE INDEX idx_api_logs_covering ON api_usage_logs(user_id, created_at DESC) 
    INCLUDE (service, credits_used);
```

### 5.2 Table Partitioning

```sql
-- Partition api_usage_logs by month
CREATE TABLE api_usage_logs (
    id UUID DEFAULT gen_random_uuid(),
    ...
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

CREATE TABLE api_usage_logs_2024_01 PARTITION OF api_usage_logs
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- Partition credit_transactions by month for archival
CREATE TABLE credit_transactions_2024_01 PARTITION OF credit_transactions
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

---

## 6. Backup & Recovery

### 6.1 Backup Schedule

| Type | Frequency | Retention | Location |
|------|-----------|-----------|----------|
| Full Backup | Daily | 30 days | Primary + Cross-region |
| WAL Archiving | Continuous | 7 days | S3 |
| Point-in-Time | Continuous | 35 days | RDS |
| Table Export | Weekly | 90 days | S3 Glacier |

### 6.2 Recovery Procedures

```bash
# Point-in-time recovery
aws rds restore-db-instance-to-point-in-time \
    --source-db-instance-identifier aicafe-prod \
    --target-db-instance-identifier aicafe-restored \
    --restore-time "2024-01-15T10:30:00Z"

# Table restore (from export)
pg_restore -h hostname -U username -d aicafe -t orders backup.dump
```

---

## 7. Security

### 7.1 Row-Level Security

```sql
-- Enable RLS
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE wallets ENABLE ROW LEVEL SECURITY;

-- Users can only see their own data
CREATE POLICY users_own_orders ON orders
    FOR ALL
    USING (user_id = current_setting('app.current_user_id')::UUID);

CREATE POLICY users_own_wallet ON wallets
    FOR ALL
    USING (user_id = current_setting('app.current_user_id')::UUID);
```

### 7.2 Encryption

```sql
-- Column encryption for sensitive data
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Encrypt phone number
ALTER TABLE users ADD COLUMN phone_encrypted BYTEA;
UPDATE users SET phone_encrypted = pgp_sym_encrypt(phone, current_setting('app.db_key'));

-- Use encrypted column for lookups
CREATE INDEX idx_users_phone_encrypted ON users(phone_encrypted);
```

---

## 8. ER Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              ENTITY RELATIONSHIP DIAGRAM                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                              ┌─────────────┐                                        │
│                              │    users    │                                        │
│                              └──────┬──────┘                                        │
│                                     │                                               │
│         ┌──────────────────────────┼──────────────────────────┐                      │
│         │                          │                          │                      │
│         ▼                          ▼                          ▼                      │
│   ┌───────────┐            ┌─────────────┐            ┌─────────────┐               │
│   │user_pref  │            │  wallets    │            │ memberships │               │
│   └─────┬─────┘            └──────┬──────┘            └──────┬──────┘               │
│         │                         │                          │                       │
│         │                         │                          │                       │
│         │                         ▼                          │                       │
│         │                   ┌──────────────┐                │                       │
│         │                   │credit_txns   │                │                       │
│         │                   └──────┬───────┘                │                       │
│         │                          │                        │                       │
│         │                          │                        ▼                       │
│         │                          │               ┌─────────────────┐             │
│         │                          │               │subscription_hist│             │
│         │                          │               └────────┬────────┘             │
│         │                          │                        │                        │
│         │                          │                        │                        │
│         ▼                          │                        │                        │
│   ┌───────────┐                   │                        │                        │
│   │sessions   │                   │                        │                        │
│   └─────┬─────┘                   │                        │                        │
│         │                          │                        │                        │
│         │                          ▼                        ▼                        │
│         │                   ┌─────────────┐          ┌──────────┐                │
│         │                   │ daily_usage  │          │  orders  │                │
│         │                   └─────────────┘          └────┬─────┘                │
│         │                                               │                         │
│         │                                               ▼                         │
│         │                                        ┌─────────────┐                  │
│         │                                        │ order_items │                  │
│         │                                        └─────────────┘                  │
│         │                                               │                         │
│         │                                               ▼                         │
│         │                                        ┌─────────────┐                  │
│         │                                        │  invoices   │                  │
│         │                                        └─────────────┘                  │
│         │                                               │                         │
│         │                                               │                         │
│         ▼                                               │                         │
│   ┌───────────┐                                        │                         │
│   │ otp_codes │                                        │                         │
│   └───────────┘                                        │                         │
│                                                        │                         │
│                          ┌─────────────────────────────┤                         │
│                          │                             │                         │
│                          ▼                             ▼                         │
│                    ┌─────────────┐              ┌─────────────┐               │
│                    │loyalty_accts│              │   coupons   │               │
│                    └──────┬──────┘              └─────────────┘               │
│                           │                                                 │
│                           ▼                                                 │
│                    ┌──────────────┐                                        │
│                    │loyalty_txns  │                                        │
│                    └──────────────┘                                        │
│                           │                                                 │
│                           ▼                                                 │
│                    ┌─────────────┐                                        │
│                    │  rewards    │                                        │
│                    └──────┬──────┘                                        │
│                           │                                                 │
│                           ▼                                                 │
│                    ┌─────────────┐                                        │
│                    │ redemptions │                                        │
│                    └─────────────┘                                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────────────┘
```
