# AI Café Platform - Database Design

## 1. Tổng Quan Kiến Trúc Database

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         DATABASE ARCHITECTURE                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                          ┌─────────────────────┐                                   │
│                          │   PostgreSQL 15     │                                   │
│                          │   (Primary DB)      │                                   │
│                          └──────────┬──────────┘                                   │
│                                     │                                               │
│         ┌───────────────────────────┼───────────────────────────┐                 │
│         │                           │                           │                 │
│         ▼                           ▼                           ▼                 │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐         │
│  │   Users    │           │   Orders   │           │     AI     │         │
│  │  Service  │           │  Service  │           │  Gateway   │         │
│  └─────────────┘           └─────────────┘           └─────────────┘         │
│                                                                                      │
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                         MAIN TABLES                                           │ │
│  │  ───────────────────────────────────────────────────────────────────────│  │
│  │                                                                              │ │
│  │  👤 users                    → Tài khoản người dùng                           │ │
│  │  🔐 auth_tokens             → JWT tokens, refresh tokens                      │ │
│  │  ☕ cafes                    → Quán cafe/chi nhánh                           │ │
│  │  📦 products                → Sản phẩm (đồ uống,...)                        │ │
│  │  🛒 orders                  → Đơn hàng                                       │ │
│  │  💳 payments                → Thanh toán                                      │ │
│  │  💰 credits                 → Credits AI                                      │ │
│  │  🤖 ai_sessions             → Phiên chat AI                                  │ │
│  │  💬 ai_messages             → Tin nhắn AI                                    │ │
│  │  📊 ai_usage                → Lịch sử sử dụng AI                             │ │
│  │  📱 devices                 → Thiết bị đăng nhập                              │ │
│  │  🔔 notifications           → Thông báo                                       │ │
│  │                                                                              │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              ERD - AI CAFE PLATFORM                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                                                                      │
│   ┌──────────────┐         ┌──────────────┐         ┌──────────────┐           │
│   │    users     │         │  user_profiles│         │   cafes      │           │
│   ├──────────────┤         ├──────────────┤         ├──────────────┤           │
│   │ PK id        │──┐      │ PK user_id   │◄──┐    │ PK id        │           │
│   │    email     │  │      │    phone     │   │    │    name      │           │
│   │    password  │  │      │    name      │   │    │    address   │           │
│   │    role      │  │      │    avatar    │   │    │    phone     │           │
│   │ FK cafe_id   │  │      │    dob       │   │    │    status    │           │
│   │    status    │  │      │    gender    │   │    │    timezone  │           │
│   └──────────────┘  │      └──────────────┘   │    └──────┬───────┘           │
│          │          │              │          │           │                    │
│          │          │              │          │           │                    │
│          │    ┌─────┴──────┐       │          │           │                    │
│          │    │   orders    │       │          │           │                    │
│          │    ├────────────┤       │          │           │                    │
│          │    │ PK id      │◄─────┘           │           │                    │
│          │    │ FK user_id │─────────┐         │           │                    │
│          │    │ FK cafe_id │─────────┼─────────┘           │                    │
│          │    │    type    │         │                     │                    │
│          │    │    status  │         │                     │                    │
│          │    │  total_amount│       │                     │                    │
│          │    │  created_at │         │                     │                    │
│          │    └─────┬──────┘         │                     │                    │
│          │          │                 │                     │                    │
│          │          ▼                 │                     │                    │
│          │   ┌─────────────┐         │                     │                    │
│          │   │ order_items │         │                     │                    │
│          │   ├─────────────┤         │                     │                    │
│          │   │ PK id       │         │                     │                    │
│          │   │ FK order_id │─────────┘                     │                    │
│          │   │ FK product_id│                              │                    │
│          │   │   quantity  │                               │                    │
│          │   │   price     │                               │                    │
│          │   │   notes     │                               │                    │
│          │   └─────────────┘                               │                    │
│          │                                                   │                    │
│          │   ┌─────────────┐         ┌─────────────┐       │                    │
│          │   │   products  │         │   payments   │       │                    │
│          │   ├─────────────┤         ├─────────────┤       │                    │
│          │   │ PK id       │◄────────│ PK id       │       │                    │
│          │   │ FK cafe_id  │         │ FK order_id │◄──────┘                    │
│          │   │    name     │         │   method    │                             │
│          │   │   category  │         │    status   │                             │
│          │   │   price     │         │   amount    │                             │
│          │   │   image     │         │ transaction_id│                            │
│          │   │   status    │         │   paid_at   │                             │
│          │   └─────────────┘         └─────────────┘                             │
│          │                                                               │
│          │   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐           │
│          │   │    seats    │   │ reservations│   │    staff    │           │
│          │   ├─────────────┤   ├─────────────┤   ├─────────────┤           │
│          │   │ PK id       │   │ PK id       │   │ PK id       │           │
│          │   │ FK cafe_id  │◄──│ FK cafe_id  │   │ FK cafe_id  │           │
│          │   │   number   │   │ FK user_id  │◄─┤ FK user_id  │           │
│          │   │    zone    │   │   date_time │   │   role      │           │
│          │   │   capacity │   │   status   │   │   status   │           │
│          │   └─────────────┘   │   notes    │   └─────────────┘           │
│          │                     └─────────────┘                                │
│          │                                                               │
│          │                                                               │
│          ▼                                                               │
│   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐               │
│   │ ai_sessions  │   │ ai_messages  │   │  ai_usage    │               │
│   ├──────────────┤   ├──────────────┤   ├──────────────┤               │
│   │ PK id        │◄──│ PK id        │   │ PK id        │               │
│   │ FK user_id   │───│ FK session_id│◄──│ FK user_id   │               │
│   │ FK model_id  │   │    role     │   │ FK model_id │               │
│   │    status   │   │   content   │   │   tokens     │               │
│   │ started_at  │   │   tokens    │   │   cost       │               │
│   │ ended_at    │   │ created_at │   │ created_at  │               │
│   └──────────────┘   └─────────────┘   └─────────────┘               │
│          │                                                               │
│          ▼                                                               │
│   ┌──────────────┐                                                       │
│   │   credits    │                                                       │
│   ├──────────────┤                                                       │
│   │ PK id        │                                                       │
│   │ FK user_id   │                                                       │
│   │   balance    │                                                       │
│   │   frozen     │                                                       │
│   │ updated_at   │                                                       │
│   └──────────────┘                                                       │
│          │                                                               │
│          ▼                                                               │
│   ┌──────────────┐                                                       │
│   │credit_transactions│                                                  │
│   ├──────────────┤                                                       │
│   │ PK id        │                                                       │
│   │ FK user_id   │                                                       │
│   │   type       │                                                       │
│   │   amount     │                                                       │
│   │   balance_before│                                                   │
│   │   balance_after│                                                   │
│   │   reference_id│                                                      │
│   │ created_at   │                                                       │
│   └──────────────┘                                                       │
│                                                                                      │
│                                                                                      │
│   LEGEND:                                                                            │
│   PK = Primary Key                                                                   │
│   FK = Foreign Key                                                                   │
│   ─── = One-to-Many relationship                                                   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Table Definitions

### 3.1 Users & Authentication

```sql
-- ================================================
-- USERS TABLE
-- ================================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'customer',
    -- customer, staff, manager, admin
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    -- active, inactive, suspended, banned
    email_verified_at TIMESTAMP,
    phone_verified_at TIMESTAMP,
    last_login_at TIMESTAMP,
    last_login_ip INET,
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

COMMENT ON TABLE users IS 'Main users table for authentication';
COMMENT ON COLUMN users.role IS 'customer, staff, manager, admin';
COMMENT ON COLUMN users.status IS 'active, inactive, suspended, banned';

-- ================================================
-- USER PROFILES TABLE
-- ================================================
CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255),
    display_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url TEXT,
    date_of_birth DATE,
    gender VARCHAR(20),
    -- male, female, other, prefer_not_to_say
    bio TEXT,
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100) DEFAULT 'Vietnam',
    postal_code VARCHAR(20),
    language VARCHAR(10) DEFAULT 'vi',
    timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    notification_preferences JSONB DEFAULT '{"email": true, "sms": true, "push": true}',
    preferences JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_profiles_phone ON user_profiles(phone);
CREATE INDEX idx_user_profiles_full_name ON user_profiles(full_name);

COMMENT ON TABLE user_profiles IS 'Extended user profile information';

-- ================================================
-- AUTH TOKENS TABLE
-- ================================================
CREATE TABLE auth_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    -- access_token, refresh_token, password_reset, email_verification
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    ip_address INET,
    user_agent TEXT,
    device_info JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auth_tokens_user_id ON auth_tokens(user_id);
CREATE INDEX idx_auth_tokens_token_hash ON auth_tokens(token_hash);
CREATE INDEX idx_auth_tokens_expires_at ON auth_tokens(expires_at) WHERE revoked_at IS NULL;

COMMENT ON TABLE auth_tokens IS 'JWT and refresh tokens storage';

-- ================================================
-- USER DEVICES TABLE
-- ================================================
CREATE TABLE user_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_type VARCHAR(50) NOT NULL,
    -- web, ios, android, pos
    device_name VARCHAR(255),
    device_token TEXT,
    -- FCM token for push notifications
    os_version VARCHAR(50),
    app_version VARCHAR(50),
    last_active_at TIMESTAMP WITH TIME ZONE,
    ip_address INET,
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_devices_user_id ON user_devices(user_id);
CREATE INDEX idx_user_devices_device_token ON user_devices(device_token) WHERE device_token IS NOT NULL;

COMMENT ON TABLE user_devices IS 'User devices for push notifications';
```

### 3.2 Cafes & Locations

```sql
-- ================================================
-- CAFES TABLE
-- ================================================
CREATE TABLE cafes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    ward VARCHAR(100),
    postal_code VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(255),
    website TEXT,
    logo_url TEXT,
    cover_image_url TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    opening_hours JSONB DEFAULT '{"mon": {"open": "07:00", "close": "22:00"}, "tue": {...}}',
    -- Operating hours for each day
    status VARCHAR(50) DEFAULT 'active',
    -- active, inactive, maintenance
    owner_id UUID REFERENCES users(id),
    manager_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_cafes_slug ON cafes(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_cafes_city ON cafes(city);
CREATE INDEX idx_cafes_status ON cafes(status);
CREATE INDEX idx_cafes_location ON cafes(latitude, longitude);

COMMENT ON TABLE cafes IS 'Cafe branches/locations';

-- ================================================
-- CAFE SEATS TABLE
-- ================================================
CREATE TABLE cafe_seats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    seat_number VARCHAR(20) NOT NULL,
    zone VARCHAR(100),
    -- vip, normal, outdoor, smoking
    seat_type VARCHAR(50) DEFAULT 'standard',
    -- standard, booth, table, bar
    capacity INT DEFAULT 4,
    position_x INT,
    position_y INT,
    -- For floor plan layout
    is_available BOOLEAN DEFAULT TRUE,
    status VARCHAR(50) DEFAULT 'available',
    -- available, reserved, occupied, unavailable
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cafe_id, seat_number)
);

CREATE INDEX idx_cafe_seats_cafe_id ON cafe_seats(cafe_id);
CREATE INDEX idx_cafe_seats_zone ON cafe_seats(zone);
CREATE INDEX idx_cafe_seats_status ON cafe_seats(status);

-- ================================================
-- STAFF TABLE
-- ================================================
CREATE TABLE staff (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    -- barista, cashier, manager, owner
    employee_code VARCHAR(50) UNIQUE,
    hire_date DATE,
    salary DECIMAL(12, 2),
    status VARCHAR(50) DEFAULT 'active',
    -- active, on_leave, terminated
    terminated_at TIMESTAMP WITH TIME ZONE,
    termination_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, cafe_id)
);

CREATE INDEX idx_staff_user_id ON staff(user_id);
CREATE INDEX idx_staff_cafe_id ON staff(cafe_id);
CREATE INDEX idx_staff_role ON staff(role);

COMMENT ON TABLE staff IS 'Staff assignments to cafes';

-- ================================================
-- RESERVATIONS TABLE
-- ================================================
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    seat_id UUID REFERENCES cafe_seats(id),
    reservation_code VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(255),
    party_size INT NOT NULL DEFAULT 1,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME,
    status VARCHAR(50) DEFAULT 'pending',
    -- pending, confirmed, cancelled, completed, no_show
    notes TEXT,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservations_cafe_id ON reservations(cafe_id);
CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_date ON reservations(reservation_date);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_code ON reservations(reservation_code);

COMMENT ON TABLE reservations IS 'Table reservations';
```

### 3.3 Products & Menu

```sql
-- ================================================
-- CATEGORIES TABLE
-- ================================================
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    -- NULL for global categories
    parent_id UUID REFERENCES categories(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    image_url TEXT,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cafe_id, slug)
);

CREATE INDEX idx_categories_cafe_id ON categories(cafe_id);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_sort_order ON categories(sort_order);

-- ================================================
-- PRODUCTS TABLE
-- ================================================
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    short_description VARCHAR(500),
    price DECIMAL(12, 2) NOT NULL,
    compare_at_price DECIMAL(12, 2),
    -- Original price for discount display
    cost_price DECIMAL(12, 2),
    sku VARCHAR(100) UNIQUE,
    barcode VARCHAR(100),
    image_url TEXT,
    images JSONB DEFAULT '[]',
    -- Array of image URLs
    options JSONB DEFAULT '[]',
    -- Size, sugar level, ice level options
    variants JSONB DEFAULT '[]',
    -- Product variants with different prices
    calories INT,
    preparation_time INT DEFAULT 5,
    -- Minutes to prepare
    is_featured BOOLEAN DEFAULT FALSE,
    is_best_seller BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_available BOOLEAN DEFAULT TRUE,
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 10,
    sort_order INT DEFAULT 0,
    tags TEXT[],
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(cafe_id, slug)
);

CREATE INDEX idx_products_cafe_id ON products(cafe_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_slug ON products(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_sku ON products(sku) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_is_featured ON products(is_featured) WHERE is_featured = TRUE;
CREATE INDEX idx_products_is_best_seller ON products(is_best_seller) WHERE is_best_seller = TRUE;
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_tags ON products USING GIN(tags);

COMMENT ON TABLE products IS 'Products/menu items';
COMMENT ON COLUMN products.options IS '{"name": "Size", "required": true, "options": [{"value": "S", "price_add": 0}, {"value": "M", "price_add": 5000}, {"value": "L", "price_add": 10000}]}';

-- ================================================
-- PRODUCT OPTIONS TABLE
-- ================================================
CREATE TABLE product_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    -- Size, Sugar Level, Ice Level
    option_type VARCHAR(50) NOT NULL,
    -- size, sugar, ice, custom
    is_required BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_options_product_id ON product_options(product_id);

-- ================================================
-- PRODUCT OPTION VALUES TABLE
-- ================================================
CREATE TABLE product_option_values (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    option_id UUID NOT NULL REFERENCES product_options(id) ON DELETE CASCADE,
    value VARCHAR(100) NOT NULL,
    price_adjustment DECIMAL(12, 2) DEFAULT 0,
    is_default BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_option_values_option_id ON product_option_values(option_id);

-- ================================================
-- PRODUCT MODIFIERS TABLE
-- ================================================
CREATE TABLE product_modifiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(12, 2) DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_modifiers_product_id ON product_modifiers(product_id);
```

### 3.4 Orders

```sql
-- ================================================
-- ORDERS TABLE
-- ================================================
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE RESTRICT,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    staff_id UUID REFERENCES staff(id),
    seat_id UUID REFERENCES cafe_seats(id),
    order_type VARCHAR(50) NOT NULL,
    -- dine_in, take_away, delivery
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    -- pending, confirmed, preparing, ready, completed, cancelled
    priority VARCHAR(20) DEFAULT 'normal',
    -- low, normal, high, urgent
    subtotal DECIMAL(12, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    discount_code VARCHAR(50),
    tax_amount DECIMAL(12, 2) DEFAULT 0,
    tax_rate DECIMAL(5, 4) DEFAULT 0,
    shipping_fee DECIMAL(12, 2) DEFAULT 0,
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    -- All amounts in VND
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    customer_note TEXT,
    delivery_address TEXT,
    delivery_latitude DECIMAL(10, 8),
    delivery_longitude DECIMAL(11, 8),
    estimated_ready_at TIMESTAMP WITH TIME ZONE,
    ready_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    cancelled_by UUID REFERENCES users(id),
    payment_status VARCHAR(50) DEFAULT 'unpaid',
    -- unpaid, partially_paid, paid, refunded
    payment_method VARCHAR(50),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_orders_cafe_id ON orders(cafe_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_date ON orders(DATE(created_at));
CREATE INDEX idx_orders_payment_status ON orders(payment_status);

COMMENT ON TABLE orders IS 'Customer orders';
COMMENT ON COLUMN orders.order_type IS 'dine_in, take_away, delivery';

-- ================================================
-- ORDER ITEMS TABLE
-- ================================================
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    variant_id UUID,
    product_name VARCHAR(255) NOT NULL,
    -- Snapshot of product name at time of order
    variant_name VARCHAR(255),
    -- Snapshot of variant name
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(12, 2) NOT NULL,
    -- Price per unit at time of order
    options JSONB DEFAULT '[]',
    -- Selected options: [{"name": "Size", "value": "M", "price": 5000}]
    modifiers JSONB DEFAULT '[]',
    -- Extra toppings/additions
    modifiers_price DECIMAL(12, 2) DEFAULT 0,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    total_price DECIMAL(12, 2) NOT NULL,
    notes TEXT,
    item_status VARCHAR(50) DEFAULT 'pending',
    -- pending, preparing, ready, served
    prepared_at TIMESTAMP WITH TIME ZONE,
    served_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

COMMENT ON TABLE order_items IS 'Individual items in an order';

-- ================================================
-- ORDER STATUS HISTORY TABLE
-- ================================================
CREATE TABLE order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    changed_by UUID REFERENCES users(id),
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_created_at ON order_status_history(created_at DESC);

-- ================================================
-- CART TABLE (for online ordering)
-- ================================================
CREATE TABLE carts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, cafe_id)
);

CREATE INDEX idx_carts_user_id ON carts(user_id);
CREATE INDEX idx_carts_cafe_id ON carts(cafe_id);

-- ================================================
-- CART ITEMS TABLE
-- ================================================
CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    options JSONB DEFAULT '[]',
    modifiers JSONB DEFAULT '[]',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
```

### 3.5 Payments

```sql
-- ================================================
-- PAYMENTS TABLE
-- ================================================
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    payment_method VARCHAR(50) NOT NULL,
    -- cash, vnpay, stripe, momo, zalopay
    payment_type VARCHAR(50) DEFAULT 'full',
    -- full, partial, refund
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'VND',
    exchange_rate DECIMAL(10, 4) DEFAULT 1,
    -- For USD payments
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    -- pending, processing, completed, failed, refunded, cancelled
    transaction_id VARCHAR(255),
    -- External transaction ID
    provider_reference VARCHAR(255),
    -- VNPay reference, Stripe payment ID, etc.
    provider_response JSONB,
    -- Full response from payment provider
    paid_at TIMESTAMP WITH TIME ZONE,
    refunded_at TIMESTAMP WITH TIME ZONE,
    refund_reason TEXT,
    refunded_by UUID REFERENCES users(id),
    refund_transaction_id VARCHAR(255),
    notes TEXT,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);

COMMENT ON TABLE payments IS 'Payment transactions';

-- ================================================
-- PROMOTIONS TABLE
-- ================================================
CREATE TABLE promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cafe_id UUID REFERENCES cafes(id) ON DELETE CASCADE,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(50) NOT NULL,
    -- percentage, fixed_amount, free_item
    discount_value DECIMAL(12, 2) NOT NULL,
    max_discount_amount DECIMAL(12, 2),
    min_order_amount DECIMAL(12, 2) DEFAULT 0,
    max_uses INT,
    used_count INT DEFAULT 0,
    max_uses_per_user INT DEFAULT 1,
    applicable_products UUID[],
    -- Product IDs this applies to
    applicable_categories UUID[],
    -- Category IDs this applies to
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_public BOOLEAN DEFAULT FALSE,
    -- Visible to all users
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_promotions_code ON promotions(code);
CREATE INDEX idx_promotions_cafe_id ON promotions(cafe_id);
CREATE INDEX idx_promotions_dates ON promotions(start_date, end_date);

-- ================================================
-- PROMOTION USAGE TABLE
-- ================================================
CREATE TABLE promotion_usage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promotion_id UUID NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    discount_amount DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(promotion_id, user_id, order_id)
);

CREATE INDEX idx_promotion_usage_promotion_id ON promotion_usage(promotion_id);
CREATE INDEX idx_promotion_usage_user_id ON promotion_usage(user_id);
```

### 3.6 Credits & AI

```sql
-- ================================================
-- USER CREDITS TABLE
-- ================================================
CREATE TABLE user_credits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    balance BIGINT NOT NULL DEFAULT 0,
    -- In credits (1 credit = ~10 tokens)
    frozen BIGINT DEFAULT 0,
    -- Credits being used in ongoing requests
    lifetime_earned BIGINT DEFAULT 0,
    lifetime_used BIGINT DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1
    -- For optimistic locking
);

CREATE INDEX idx_user_credits_user_id ON user_credits(user_id);

COMMENT ON TABLE user_credits IS 'User AI credits balance';

-- ================================================
-- CREDIT TRANSACTIONS TABLE
-- ================================================
CREATE TABLE credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    -- earned, used, refunded, bonus, promotion, purchase, expired
    amount BIGINT NOT NULL,
    -- Positive for credit, negative for debit
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    reference_type VARCHAR(50),
    -- purchase, subscription, ai_usage, promotion, refund
    reference_id UUID,
    -- ID of related record
    description TEXT,
    expires_at TIMESTAMP WITH TIME ZONE,
    -- For bonus credits with expiration
    expired_at TIMESTAMP WITH TIME ZONE,
    -- When credits expired
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_transactions_user_id ON credit_transactions(user_id);
CREATE INDEX idx_credit_transactions_type ON credit_transactions(type);
CREATE INDEX idx_credit_transactions_created_at ON credit_transactions(created_at DESC);
CREATE INDEX idx_credit_transactions_reference ON credit_transactions(reference_type, reference_id);

COMMENT ON TABLE credit_transactions IS 'Credit transaction history';

-- ================================================
-- AI MODELS TABLE
-- ================================================
CREATE TABLE ai_models (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    -- openai, anthropic, google, huggingface
    description TEXT,
    input_cost_per_1k DECIMAL(10, 6) NOT NULL,
    -- Cost per 1000 input tokens
    output_cost_per_1k DECIMAL(10, 6) NOT NULL,
    -- Cost per 1000 output tokens
    max_tokens INT DEFAULT 4096,
    context_window INT,
    is_available BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert default models
INSERT INTO ai_models (id, name, provider, input_cost_per_1k, output_cost_per_1k, max_tokens, context_window, is_default) VALUES
('gpt-4', 'GPT-4', 'openai', 0.03, 0.06, 8192, 128000, FALSE),
('gpt-4-turbo', 'GPT-4 Turbo', 'openai', 0.01, 0.03, 128000, 128000, FALSE),
('gpt-3.5-turbo', 'GPT-3.5 Turbo', 'openai', 0.0015, 0.002, 16385, 16385, TRUE),
('claude-3-opus', 'Claude 3 Opus', 'anthropic', 0.015, 0.075, 4096, 200000, FALSE),
('claude-3-sonnet', 'Claude 3 Sonnet', 'anthropic', 0.003, 0.015, 4096, 200000, TRUE),
('claude-3-haiku', 'Claude 3 Haiku', 'anthropic', 0.00025, 0.00125, 4096, 200000, FALSE),
('gemini-pro', 'Gemini Pro', 'google', 0.00125, 0.00375, 8192, 32768, FALSE),
('gemini-1.5-pro', 'Gemini 1.5 Pro', 'google', 0.00125, 0.005, 8192, 1000000, TRUE),
('gemini-1.5-flash', 'Gemini 1.5 Flash', 'google', 0.000075, 0.0003, 8192, 1000000, FALSE),
('mistral-7b-instruct', 'Mistral 7B', 'huggingface', 0, 0, 4096, 8192, FALSE);

-- ================================================
-- AI SESSIONS TABLE
-- ================================================
CREATE TABLE ai_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_type VARCHAR(50) DEFAULT 'chat',
    -- chat, completion, image, embedding
    model_id VARCHAR(100) REFERENCES ai_models(id),
    title VARCHAR(255),
    -- Auto-generated or user-defined title
    status VARCHAR(50) DEFAULT 'active',
    -- active, archived
    message_count INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    total_cost DECIMAL(12, 4) DEFAULT 0,
    last_message_at TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB DEFAULT '{}'
);

CREATE INDEX idx_ai_sessions_user_id ON ai_sessions(user_id);
CREATE INDEX idx_ai_sessions_status ON ai_sessions(status);
CREATE INDEX idx_ai_sessions_created_at ON ai_sessions(created_at DESC);
CREATE INDEX idx_ai_sessions_last_message ON ai_sessions(last_message_at DESC) WHERE status = 'active';

COMMENT ON TABLE ai_sessions IS 'AI chat sessions';

-- ================================================
-- AI MESSAGES TABLE
-- ================================================
CREATE TABLE ai_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES ai_sessions(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    -- system, user, assistant
    content TEXT NOT NULL,
    model_id VARCHAR(100),
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    cost DECIMAL(12, 6) DEFAULT 0,
    provider VARCHAR(50),
    -- Actual provider used (may differ from requested)
    provider_response JSONB,
    -- Full API response for debugging
    cached BOOLEAN DEFAULT FALSE,
    latency_ms INT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_messages_session_id ON ai_messages(session_id);
CREATE INDEX idx_ai_messages_created_at ON ai_messages(created_at DESC);
CREATE INDEX idx_ai_messages_role ON ai_messages(role);

COMMENT ON TABLE ai_messages IS 'Individual messages in AI sessions';

-- ================================================
-- AI USAGE TABLE (for analytics)
-- ================================================
CREATE TABLE ai_usage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    model_id VARCHAR(100) NOT NULL REFERENCES ai_models(id),
    provider VARCHAR(50) NOT NULL,
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    cost DECIMAL(12, 6) DEFAULT 0,
    credits_used BIGINT DEFAULT 0,
    session_id UUID REFERENCES ai_sessions(id),
    endpoint VARCHAR(100),
    -- /chat, /completion, etc.
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_usage_user_id ON ai_usage(user_id);
CREATE INDEX idx_ai_usage_model_id ON ai_usage(model_id);
CREATE INDEX idx_ai_usage_created_at ON ai_usage(created_at DESC);
CREATE INDEX idx_ai_usage_date ON ai_usage(DATE(created_at));

COMMENT ON TABLE ai_usage IS 'Daily usage tracking for analytics and billing';

-- ================================================
-- AI PROMPTS LIBRARY (saved prompts)
-- ================================================
CREATE TABLE ai_prompts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    prompt_text TEXT NOT NULL,
    model_id VARCHAR(100),
    is_public BOOLEAN DEFAULT FALSE,
    use_count INT DEFAULT 0,
    tags TEXT[],
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_prompts_user_id ON ai_prompts(user_id);
CREATE INDEX idx_ai_prompts_tags ON ai_prompts USING GIN(tags);
```

### 3.7 Notifications

```sql
-- ================================================
-- NOTIFICATIONS TABLE
-- ================================================
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    -- order_status, payment, promotion, ai_credits, system
    title VARCHAR(255) NOT NULL,
    body TEXT,
    data JSONB DEFAULT '{}',
    -- Additional data for deep linking
    image_url TEXT,
    action_url TEXT,
    action_type VARCHAR(50),
    -- open_app, open_url, open_screen
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    read_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read) WHERE is_read = FALSE;
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_type ON notifications(type);

-- ================================================
-- USER NOTIFICATION SETTINGS TABLE
-- ================================================
CREATE TABLE user_notification_settings (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    email_order_updates BOOLEAN DEFAULT TRUE,
    email_promotions BOOLEAN DEFAULT TRUE,
    email_credits BOOLEAN DEFAULT TRUE,
    email_system BOOLEAN DEFAULT TRUE,
    push_order_updates BOOLEAN DEFAULT TRUE,
    push_promotions BOOLEAN DEFAULT TRUE,
    push_credits BOOLEAN DEFAULT TRUE,
    push_system BOOLEAN DEFAULT TRUE,
    sms_order_updates BOOLEAN DEFAULT FALSE,
    sms_promotions BOOLEAN DEFAULT FALSE,
    sms_credits BOOLEAN DEFAULT FALSE,
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    quiet_hours_timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- EMAIL LOGS TABLE
-- ================================================
CREATE TABLE email_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    to_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    template_name VARCHAR(100),
    template_data JSONB,
    status VARCHAR(50) DEFAULT 'pending',
    -- pending, sent, delivered, bounced, complained, failed
    provider_message_id VARCHAR(255),
    provider_response JSONB,
    opened_at TIMESTAMP WITH TIME ZONE,
    clicked_at TIMESTAMP WITH TIME ZONE,
    bounced_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_email_logs_user_id ON email_logs(user_id);
CREATE INDEX idx_email_logs_status ON email_logs(status);
CREATE INDEX idx_email_logs_created_at ON email_logs(created_at DESC);

-- ================================================
-- SMS LOGS TABLE
-- ================================================
CREATE TABLE sms_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    phone VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    template_name VARCHAR(100),
    template_data JSONB,
    status VARCHAR(50) DEFAULT 'pending',
    -- pending, sent, delivered, failed, undelivered
    provider_message_id VARCHAR(255),
    provider_response JSONB,
    error_message TEXT,
    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sms_logs_user_id ON sms_logs(user_id);
CREATE INDEX idx_sms_logs_phone ON sms_logs(phone);
CREATE INDEX idx_sms_logs_status ON sms_logs(status);
```

---

## 4. Functions & Triggers

```sql
-- ================================================
-- AUTO UPDATE UPDATED_AT TRIGGER
-- ================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply to all tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cafes_updated_at BEFORE UPDATE ON cafes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ai_sessions_updated_at BEFORE UPDATE ON ai_sessions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ================================================
-- GENERATE ORDER NUMBER FUNCTION
-- ================================================
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TRIGGER AS $$
DECLARE
    prefix VARCHAR(10);
    seq_num INT;
BEGIN
    prefix := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');
    
    SELECT COALESCE(MAX(
        CAST(SUBSTRING(order_number FROM 10) AS INT)
    ), 0) + 1 INTO seq_num
    FROM orders
    WHERE order_number LIKE prefix || '%';
    
    NEW.order_number := prefix || LPAD(seq_num::TEXT, 6, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_order_number_trigger
    BEFORE INSERT ON orders
    FOR EACH ROW
    WHEN (NEW.order_number IS NULL)
    EXECUTE FUNCTION generate_order_number();

-- ================================================
-- GENERATE RESERVATION CODE FUNCTION
-- ================================================
CREATE OR REPLACE FUNCTION generate_reservation_code()
RETURNS TRIGGER AS $$
DECLARE
    chars TEXT := 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    code VARCHAR(8) := '';
    i INT;
BEGIN
    FOR i IN 1..8 LOOP
        code := code || SUBSTRING(chars FROM floor(random() * 24 + 1)::INT FOR 1);
    END LOOP;
    
    NEW.reservation_code := code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_reservation_code_trigger
    BEFORE INSERT ON reservations
    FOR EACH ROW
    WHEN (NEW.reservation_code IS NULL)
    EXECUTE FUNCTION generate_reservation_code();

-- ================================================
-- UPDATE AI SESSION STATS TRIGGER
-- ================================================
CREATE OR REPLACE FUNCTION update_ai_session_stats()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ai_sessions
    SET 
        message_count = message_count + 1,
        total_tokens = total_tokens + NEW.total_tokens,
        total_cost = total_cost + NEW.cost,
        last_message_at = NEW.created_at
    WHERE id = NEW.session_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_ai_session_stats_trigger
    AFTER INSERT ON ai_messages
    FOR EACH ROW
    EXECUTE FUNCTION update_ai_session_stats();

-- ================================================
-- DEDUCT CREDITS FUNCTION
-- ================================================
CREATE OR REPLACE FUNCTION deduct_credits(
    p_user_id UUID,
    p_amount BIGINT,
    p_reference_type VARCHAR(50),
    p_reference_id UUID,
    p_description TEXT
)
RETURNS TABLE(success BOOLEAN, new_balance BIGINT, error_message TEXT) AS $$
DECLARE
    v_balance BIGINT;
    v_new_balance BIGINT;
BEGIN
    -- Get current balance with lock
    SELECT balance INTO v_balance
    FROM user_credits
    WHERE user_id = p_user_id
    FOR UPDATE;
    
    -- Check if user has enough credits
    IF v_balance IS NULL THEN
        RETURN QUERY SELECT FALSE, BIGINT '0', 'User credits record not found'::TEXT;
        RETURN;
    END IF;
    
    IF v_balance < p_amount THEN
        RETURN QUERY SELECT FALSE, v_balance, 'Insufficient credits'::TEXT;
        RETURN;
    END IF;
    
    -- Update balance
    v_new_balance := v_balance - p_amount;
    
    UPDATE user_credits
    SET 
        balance = v_new_balance,
        lifetime_used = lifetime_used + p_amount,
        updated_at = CURRENT_TIMESTAMP,
        version = version + 1
    WHERE user_id = p_user_id;
    
    -- Record transaction
    INSERT INTO credit_transactions (
        user_id, type, amount, balance_before, balance_after,
        reference_type, reference_id, description
    ) VALUES (
        p_user_id, 'used', -p_amount, v_balance, v_new_balance,
        p_reference_type, p_reference_id, p_description
    );
    
    RETURN QUERY SELECT TRUE, v_new_balance, NULL::TEXT;
END;
$$ LANGUAGE plpgsql;

-- ================================================
-- REFUND CREDITS FUNCTION
-- ================================================
CREATE OR REPLACE FUNCTION refund_credits(
    p_user_id UUID,
    p_amount BIGINT,
    p_reference_type VARCHAR(50),
    p_reference_id UUID,
    p_description TEXT
)
RETURNS TABLE(success BOOLEAN, new_balance BIGINT, error_message TEXT) AS $$
DECLARE
    v_balance BIGINT;
    v_new_balance BIGINT;
BEGIN
    -- Get current balance
    SELECT balance INTO v_balance
    FROM user_credits
    WHERE user_id = p_user_id
    FOR UPDATE;
    
    IF v_balance IS NULL THEN
        -- Create credits record if not exists
        INSERT INTO user_credits (user_id, balance) VALUES (p_user_id, 0);
        v_balance := 0;
    END IF;
    
    v_new_balance := v_balance + p_amount;
    
    UPDATE user_credits
    SET 
        balance = v_new_balance,
        lifetime_earned = lifetime_earned + p_amount,
        updated_at = CURRENT_TIMESTAMP,
        version = version + 1
    WHERE user_id = p_user_id;
    
    -- Record transaction
    INSERT INTO credit_transactions (
        user_id, type, amount, balance_before, balance_after,
        reference_type, reference_id, description
    ) VALUES (
        p_user_id, 'refunded', p_amount, v_balance, v_new_balance,
        p_reference_type, p_reference_id, p_description
    );
    
    RETURN QUERY SELECT TRUE, v_new_balance, NULL::TEXT;
END;
$$ LANGUAGE plpgsql;

-- ================================================
-- RECORD AI USAGE FUNCTION
-- ================================================
CREATE OR REPLACE FUNCTION record_ai_usage()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ai_usage (
        user_id, model_id, provider, input_tokens, output_tokens,
        total_tokens, cost, session_id, endpoint
    ) VALUES (
        NEW.user_id, NEW.model_id, NEW.provider,
        NEW.input_tokens, NEW.output_tokens, NEW.total_tokens,
        NEW.cost, NEW.session_id, 'chat'
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER record_ai_usage_trigger
    AFTER INSERT ON ai_messages
    FOR EACH ROW
    WHEN (NEW.role = 'assistant' AND NEW.error_message IS NULL)
    EXECUTE FUNCTION record_ai_usage();
```

---

## 5. Sample Data

```sql
-- ================================================
-- SAMPLE DATA - FOR DEVELOPMENT ONLY
-- ================================================

-- Admin user (password: admin123)
INSERT INTO users (id, email, password_hash, role, status, email_verified_at) VALUES
('00000000-0000-0000-0000-000000000001', 'admin@aicafe.vn', 
 '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4/XQj8J8X4/X4Xe', 
 'admin', 'active', CURRENT_TIMESTAMP);

-- Test user (password: test123)
INSERT INTO users (id, email, password_hash, role, status, email_verified_at) VALUES
('00000000-0000-0000-0000-000000000002', 'test@aicafe.vn', 
 '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4/XQj8J8X4/X4Xe', 
 'customer', 'active', CURRENT_TIMESTAMP);

-- User profiles
INSERT INTO user_profiles (user_id, full_name, phone, date_of_birth, gender) VALUES
('00000000-0000-0000-0000-000000000001', 'Admin User', '+84909123456', '1990-01-01', 'male'),
('00000000-0000-0000-0000-000000000002', 'Test User', '+84909123457', '1995-05-15', 'female');

-- Give test user some credits
INSERT INTO user_credits (user_id, balance, lifetime_earned) VALUES
('00000000-0000-0000-0000-000000000001', 1000000, 1000000),
('00000000-0000-0000-0000-000000000002', 10000, 10000);

-- Credit transactions
INSERT INTO credit_transactions (user_id, type, amount, balance_before, balance_after, description) VALUES
('00000000-0000-0000-0000-000000000001', 'earned', 1000000, 0, 1000000, 'Initial admin credits'),
('00000000-0000-0000-0000-000000000002', 'earned', 10000, 0, 10000, 'Welcome bonus');

-- Sample cafe
INSERT INTO cafes (id, name, slug, address, city, phone, email, status) VALUES
('00000000-0000-0000-0000-000000000010', 'AI Café Quận 1', 'aicafe-quan-1',
 '123 Nguyễn Huệ, Phường Bến Nghé', 'Ho Chi Minh City', '+84909123458',
 'quan1@aicafe.vn', 'active');

-- Sample categories
INSERT INTO categories (id, cafe_id, name, slug, sort_order) VALUES
('00000000-0000-0000-0000-000000000100', '00000000-0000-0000-0000-000000000010', 'Cà Phê', 'ca-phe', 1),
('00000000-0000-0000-0000-000000000101', '00000000-0000-0000-0000-000000000010', 'Trà', 'tra', 2),
('00000000-0000-0000-0000-000000000102', '00000000-0000-0000-0000-000000000010', 'Nước Ép', 'nuoc-ep', 3),
('00000000-0000-0000-0000-000000000103', '00000000-0000-0000-0000-000000000010', 'Sinh Tố', 'sinh-to', 4);

-- Sample products
INSERT INTO products (id, cafe_id, category_id, name, slug, price, description, is_best_seller, is_featured) VALUES
-- Coffee
('00000000-0000-0000-0000-000000001001', '00000000-0000-0000-0000-000000000010', 
 '00000000-0000-0000-0000-000000000100', 'Cà Phê Sữa Đá', 'ca-phe-sua-da', 35000,
 'Cà phê rang xay pha sữa đặc, thức uống đặc trưng Việt Nam', TRUE, TRUE),
('00000000-0000-0000-0000-000000001002', '00000000-0000-0000-0000-000000000010', 
 '00000000-0000-0000-0000-000000000100', 'Cappuccino', 'cappuccino', 55000,
 'Espresso với sữa nóng và bọt sữa mịn', FALSE, FALSE),
('00000000-0000-0000-0000-000000001003', '00000000-0000-0000-0000-000000000010', 
 '00000000-0000-0000-0000-000000000100', 'Latte', 'latte', 50000,
 'Espresso với sữa nóng mịn màng', FALSE, FALSE),
-- Tea
('00000000-0000-0000-0000-000000001004', '00000000-0000-0000-0000-000000000010', 
 '00000000-0000-0000-0000-000000000101', 'Trà Đá', 'tra-da', 20000,
 'Trà sen hồng Thăng Long, giải khát mát lạnh', TRUE, TRUE),
('00000000-0000-0000-0000-000000001005', '00000000-0000-0000-0000-000000000010', 
 '00000000-0000-0000-0000-000000000101', 'Matcha Latte', 'matcha-latte', 55000,
 'Matcha Nhật Bản pha với sữa', FALSE, FALSE),
-- Juice
('00000000-0000-0000-0000-000000001006', '00000000-0000-0000-0000-000000000010', 
 '00000000-0000-0000-0000-000000000102', 'Nước Ép Cam', 'nuoc-ep-cam', 45000,
 'Cam vắt tươi 100%', FALSE, FALSE);

-- Sample seats
INSERT INTO cafe_seats (cafe_id, seat_number, zone, seat_type, capacity, position_x, position_y) VALUES
('00000000-0000-0000-0000-000000000010', 'A1', 'VIP', 'booth', 4, 10, 10),
('00000000-0000-0000-0000-000000000010', 'A2', 'VIP', 'booth', 4, 10, 60),
('00000000-0000-0000-0000-000000000010', 'B1', 'Normal', 'table', 2, 100, 10),
('00000000-0000-0000-0000-000000000010', 'B2', 'Normal', 'table', 2, 100, 60),
('00000000-0000-0000-0000-000000000010', 'B3', 'Normal', 'table', 2, 100, 110),
('00000000-0000-0000-0000-000000000010', 'C1', 'Outdoor', 'table', 4, 200, 10),
('00000000-0000-0000-0000-000000000010', 'C2', 'Outdoor', 'table', 4, 200, 60);

-- Sample promotions
INSERT INTO promotions (code, name, discount_type, discount_value, min_order_amount, max_uses, start_date, end_date, is_public) VALUES
('WELCOME50', 'Chào mừng thành viên mới', 'fixed_amount', 50000, 0, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', TRUE),
('SUMMER2024', 'Ưu đãi mùa hè', 'percentage', 10, 100000, 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', TRUE);
```

---

## 6. Database Migration Setup

```yaml
# infrastructure/local/flyway.conf
flyway.url=jdbc:postgresql://localhost:5432/aicafe_dev
flyway.user=aicafe
flyway.password=devpassword123
flyway.locations=filesystem:./migrations
flyway.baselineOnMigrate=true
```

```
# Directory structure
services/
  user-service/
    src/main/resources/
      db/migration/
        V1__init_schema.sql
        V2__sample_data.sql
```

---

## 7. Indexes Summary

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         INDEXES OVERVIEW                                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  HIGH FREQUENCY QUERIES → Composite indexes with WHERE clauses                    │
│  ────────────────────────────────────────────────────────────────────────────   │
│                                                                                      │
│  USERS                                                                             │
│  ├─ idx_users_email (WHERE deleted_at IS NULL)                                    │
│  ├─ idx_users_role                                                                 │
│  └─ idx_users_status                                                                │
│                                                                                      │
│  PRODUCTS                                                                          │
│  ├─ idx_products_slug (WHERE deleted_at IS NULL)                                  │
│  ├─ idx_products_sku (WHERE deleted_at IS NULL)                                   │
│  ├─ idx_products_is_featured (WHERE is_featured = TRUE)                            │
│  ├─ idx_products_is_best_seller (WHERE is_best_seller = TRUE)                     │
│  └─ idx_products_tags (GIN index)                                                 │
│                                                                                      │
│  ORDERS                                                                            │
│  ├─ idx_orders_order_number                                                        │
│  ├─ idx_orders_status                                                              │
│  ├─ idx_orders_date (DATE(created_at))                                             │
│  └─ idx_orders_payment_status                                                       │
│                                                                                      │
│  AI                                                                                │
│  ├─ idx_ai_sessions_last_message (WHERE status = 'active')                        │
│  ├─ idx_ai_usage_date (DATE(created_at))                                          │
│  └─ idx_ai_prompts_tags (GIN index)                                               │
│                                                                                      │
│  NOTIFICATIONS                                                                     │
│  └─ idx_notifications_is_read (WHERE is_read = FALSE)                             │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Performance Considerations

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         PERFORMANCE TIPS                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  1. PARTITION TABLES (for large data)                                             │
│  ──────────────────────────────────────────                                        │
│  • orders - partition by month (created_at)                                        │
│  • ai_messages - partition by month (created_at)                                  │
│  • ai_usage - partition by month (created_at)                                     │
│                                                                                      │
│  2. CONNECTIONS POOLING                                                            │
│  ──────────────────────────                                                        │
│  • Use PgBouncer for connection pooling                                           │
│  • Mode: transaction (for high concurrency)                                       │
│  • Max connections: 100-200                                                       │
│                                                                                      │
│  3. CACHING STRATEGY                                                              │
│  ─────────────────────                                                              │
│  • Redis for hot data: sessions, AI cache, rate limits                            │
│  • Cache AI messages for 5 minutes                                                 │
│  • Cache product catalog for 1 hour                                                │
│                                                                                      │
│  4. QUERY OPTIMIZATION                                                             │
│  ─────────────────────                                                              │
│  • Avoid SELECT * - always specify columns                                         │
│  • Use EXPLAIN ANALYZE to optimize slow queries                                   │
│  • Batch inserts for order items                                                   │
│                                                                                      │
│  5. BACKUP STRATEGY                                                               │
│  ─────────────────────                                                             │
│  • Daily full backup (pg_dump)                                                     │
│  • WAL archiving for point-in-time recovery                                        │
│  • Retain 7 daily, 4 weekly, 12 monthly                                           │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Next Steps

| Step | Task | Description |
|------|------|-------------|
| ✅ | Database Design | Full ERD & schema |
| ⬜ | Create Migration Files | V1, V2, V3... SQL files |
| ⬜ | Setup ORM/DTOs | TypeORM or JPA entities |
| ⬜ | Create Repositories | Data access layer |
| ⬜ | Implement Services | Business logic |
| ⬜ | Create API Endpoints | REST controllers |
| ⬜ | Write Unit Tests | Test coverage |

---

Bạn muốn tôi tiếp tục với bước nào?
1. **Create Migration Files** - Tạo SQL migration files
2. **Setup TypeORM Entities** - Tạo TypeScript entities cho Node.js
3. **Setup Spring Boot Entities** - Tạo JPA entities cho Java services
4. **Create Repository Pattern** - Data access layer
