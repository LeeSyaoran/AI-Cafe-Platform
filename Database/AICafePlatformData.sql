-- ==============================================================================
-- AI CAFÉ PLATFORM - DATABASE SCHEMA DEFINITION & PROCEDURAL LOGIC
-- File: AICafePlatformData.sql
-- Description: Chứa toàn bộ cấu trúc tạo bảng (DDL), chỉ mục (Indexes),
--              ràng buộc (Constraints), Stored Functions và Triggers.
-- ==============================================================================

-- Bật các extensions cần thiết
ROLLBACK;
BEGIN;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================================================
-- 1. MODULE TỔ CHỨC & CHI NHÁNH (ORGANIZATIONS & CAFES)
-- ==============================================================================

-- 1.1 Bảng companies (Multi-tenant doanh nghiệp/công ty chủ quản)
CREATE TABLE IF NOT EXISTS companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    tax_id VARCHAR(50),
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    description TEXT,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'suspended', 'inactive')),
    is_active BOOLEAN DEFAULT TRUE,
    settings JSONB DEFAULT '{}',
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 1.2 Bảng regions (Vùng/khu vực hoạt động: Tỉnh/Thành phố, Quận/Huyện)
CREATE TABLE IF NOT EXISTS regions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(20) NOT NULL,
    region_type VARCHAR(20) DEFAULT 'city' CHECK (region_type IN ('city', 'district', 'area')),
    parent_id UUID REFERENCES regions(id),
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 1.3 Bảng cafes (Các chi nhánh quán café)
CREATE TABLE IF NOT EXISTS cafes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    region_id UUID REFERENCES regions(id) ON DELETE SET NULL,
    cafe_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    address TEXT,
    province VARCHAR(100),
    district VARCHAR(100),
    ward VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    email VARCHAR(255),
    opening_hours JSONB DEFAULT '{"mon": "07:00-22:00", "tue": "07:00-22:00", "wed": "07:00-22:00", "thu": "07:00-22:00", "fri": "07:00-23:00", "sat": "08:00-23:00", "sun": "08:00-22:00"}',
    image_url VARCHAR(500),
    images JSONB DEFAULT '[]',
    images_json TEXT DEFAULT '[]',
    is_active BOOLEAN DEFAULT TRUE,
    is_delivery_enabled BOOLEAN DEFAULT FALSE,
    is_pickup_enabled BOOLEAN DEFAULT TRUE,
    is_reservation_enabled BOOLEAN DEFAULT FALSE,
    minimum_order DECIMAL(12, 0) DEFAULT 0,
    delivery_radius DECIMAL(8, 0) DEFAULT 10,
    tax_rate DECIMAL(5, 4) DEFAULT 0.1,
    status VARCHAR(20) DEFAULT 'open' CHECK (status IN ('open', 'busy', 'closed', 'temporarily_closed')),
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 2. MODULE NGƯỜI DÙNG & TÀI KHOẢN (USERS & AUTHENTICATION)
-- ==============================================================================

-- 2.1 Bảng users (Tài khoản người dùng toàn hệ thống)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE SET NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(100),
    avatar_url VARCHAR(500),
    role VARCHAR(20) DEFAULT 'customer' CHECK (role IN ('customer', 'staff', 'manager', 'admin', 'super_admin')),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'suspended', 'pending', 'deleted')),
    tier VARCHAR(20) DEFAULT 'bronze' CHECK (tier IN ('bronze', 'silver', 'gold', 'platinum')),
    is_verified BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMP WITH TIME ZONE,
    phone_verified_at TIMESTAMP WITH TIME ZONE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB DEFAULT '{}',
    CONSTRAINT users_phone_format CHECK (phone ~ '^\+?[0-9]{9,15}$'),
    CONSTRAINT users_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- 2.2 Bảng user_profiles (Hồ sơ mở rộng của người dùng)
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url TEXT,
    date_of_birth DATE,
    gender VARCHAR(20) CHECK (gender IS NULL OR gender IN ('male', 'female', 'other', 'prefer_not_to_say')),
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

-- 2.3 Bảng user_preferences (Cài đặt tùy chọn cá nhân của người dùng)
CREATE TABLE IF NOT EXISTS user_preferences (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    language VARCHAR(10) DEFAULT 'vi',
    timezone VARCHAR(50) DEFAULT 'Asia/Ho_Chi_Minh',
    currency VARCHAR(3) DEFAULT 'VND',
    notify_email BOOLEAN DEFAULT TRUE,
    notify_sms BOOLEAN DEFAULT TRUE,
    notify_push BOOLEAN DEFAULT TRUE,
    theme VARCHAR(20) DEFAULT 'light',
    ai_model_preference VARCHAR(50) DEFAULT 'gpt-4o',
    ai_temperature DECIMAL(3, 2) DEFAULT 0.7,
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2.4 Bảng user_addresses (Sổ địa chỉ nhận hàng của khách)
CREATE TABLE IF NOT EXISTS user_addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    province VARCHAR(100),
    district VARCHAR(100),
    ward VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    receiver_name VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- View tương thích cho bảng addresses
CREATE OR REPLACE VIEW addresses AS SELECT * FROM user_addresses;

-- 2.5 Bảng refresh_tokens (Quản lý phiên đăng nhập JWT/Auth)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token_hash VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_info JSONB,
    ip_address INET,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2.6 Bảng staffs (Nhân sự phân bổ theo chi nhánh)
CREATE TABLE IF NOT EXISTS staffs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    cafe_id UUID REFERENCES cafes(id) ON DELETE SET NULL,
    staff_code VARCHAR(50) UNIQUE,
    role VARCHAR(50) NOT NULL, -- owner, manager, supervisor, cashier, barista, server, delivery
    department VARCHAR(100),
    hire_date DATE,
    termination_date DATE,
    salary_type VARCHAR(50),
    salary_amount DECIMAL(12, 2),
    status VARCHAR(50) DEFAULT 'active',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 3. MODULE NGUYÊN LIỆU & NHÀ CUNG CẤP (SUPPLIERS & INGREDIENTS)
-- ==============================================================================

-- 3.1 Bảng units (Đơn vị tính cho nguyên liệu và sản phẩm)
CREATE TABLE IF NOT EXISTS units (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_vi VARCHAR(100),
    abbreviation VARCHAR(20) NOT NULL,
    unit_type VARCHAR(20) NOT NULL CHECK (unit_type IN ('count', 'weight', 'volume', 'length')),
    conversion_factor INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3.2 Bảng allergens (Cảnh báo chất gây dị ứng)
CREATE TABLE IF NOT EXISTS allergens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    name_vi VARCHAR(100),
    icon VARCHAR(10),
    color VARCHAR(20),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3.3 Bảng suppliers (Nhà cung cấp nguyên vật liệu)
CREATE TABLE IF NOT EXISTS suppliers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    tax_id VARCHAR(50),
    contact_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    payment_terms VARCHAR(100),
    credit_limit DECIMAL(12, 2),
    rating DECIMAL(3, 2),
    is_active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3.4 Bảng ingredients (Kho nguyên liệu pha chế)
CREATE TABLE IF NOT EXISTS ingredients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    sku VARCHAR(100),
    barcode VARCHAR(100),
    category VARCHAR(100),
    unit_id VARCHAR(50) REFERENCES units(id) ON DELETE SET NULL,
    cost_price DECIMAL(12, 2) DEFAULT 0,
    min_stock_level INT DEFAULT 0,
    max_stock_level INT DEFAULT 1000,
    shelf_life_days INT,
    storage_conditions VARCHAR(100),
    supplier_id UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    supplier_sku VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 4. MODULE THỰC ĐƠN & SẢN PHẨM (MENU & PRODUCTS)
-- ==============================================================================

-- 4.1 Bảng categories (Danh mục sản phẩm)
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    slug VARCHAR(255) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(20),
    parent_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    description TEXT,
    description_vi TEXT,
    sort_order INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4.2 Bảng products (Sản phẩm đồ uống, thức ăn, combo)
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    slug VARCHAR(255) NOT NULL,
    sku VARCHAR(50),
    description TEXT,
    description_vi TEXT,
    short_description VARCHAR(255),
    price DECIMAL(12, 0) NOT NULL DEFAULT 0,
    original_price DECIMAL(12, 0),
    cost_price DECIMAL(12, 0),
    image_url VARCHAR(500),
    images JSONB DEFAULT '[]',
    images_json TEXT DEFAULT '[]',
    unit VARCHAR(20) DEFAULT 'item',
    stock INTEGER DEFAULT 0,
    stock_status VARCHAR(20) DEFAULT 'in_stock' CHECK (stock_status IN ('in_stock', 'low_stock', 'out_of_stock', 'preorder')),
    is_active BOOLEAN DEFAULT TRUE,
    is_available BOOLEAN DEFAULT TRUE,
    sold_count INTEGER DEFAULT 0,
    avg_rating DECIMAL(3, 2) DEFAULT 0,
    review_count INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_best_seller BOOLEAN DEFAULT FALSE,
    is_combo BOOLEAN DEFAULT FALSE,
    preparation_time INTEGER DEFAULT 15,
    calories INTEGER,
    options JSONB DEFAULT '[]',
    options_json TEXT DEFAULT '[]',
    modifiers JSONB DEFAULT '[]',
    modifiers_json TEXT DEFAULT '[]',
    allergens JSONB DEFAULT '[]',
    tags JSONB DEFAULT '[]',
    tags_json TEXT DEFAULT '[]',
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'draft', 'archived')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4.3 Bảng product_variants (Biến thể kích cỡ, phiên bản: M, L, Nóng, Lạnh)
CREATE TABLE IF NOT EXISTS product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(50),
    price DECIMAL(12, 0) NOT NULL DEFAULT 0,
    original_price DECIMAL(12, 0),
    stock INTEGER DEFAULT 0,
    options JSONB DEFAULT '{}',
    is_default BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 5. MODULE BÀN & ĐẶT CHỖ (SEATS & RESERVATIONS)
-- ==============================================================================

-- 5.1 Bảng seats (Bàn ghế, chỗ ngồi tại quán)
CREATE TABLE IF NOT EXISTS seats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cafe_id UUID NOT NULL REFERENCES cafes(id) ON DELETE CASCADE,
    seat_number VARCHAR(20) NOT NULL,
    zone VARCHAR(100) DEFAULT 'Standard',
    seat_type VARCHAR(20) DEFAULT 'table' CHECK (seat_type IN ('table', 'booth', 'vip', 'outdoor', 'private', 'bar')),
    capacity INTEGER DEFAULT 4,
    min_capacity INTEGER DEFAULT 1,
    max_capacity INTEGER DEFAULT 8,
    pos_x INTEGER,
    pos_y INTEGER,
    pos_width INTEGER,
    pos_height INTEGER,
    hourly_rate DECIMAL(10, 0),
    is_available BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'available' CHECK (status IN ('available', 'reserved', 'occupied', 'maintenance')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5.2 Bảng seat_reservations (Lịch đặt bàn trước của khách)
CREATE TABLE IF NOT EXISTS seat_reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reservation_code VARCHAR(20) UNIQUE,
    seat_id UUID NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id UUID,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    guest_count INTEGER DEFAULT 1,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'checked_in', 'completed', 'cancelled', 'no_show')),
    notes TEXT,
    checked_in_at TIMESTAMP WITH TIME ZONE,
    checked_out_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 6. MODULE GIỎ HÀNG & ĐƠN HÀNG (CARTS & ORDERS)
-- ==============================================================================

-- 6.1 Bảng carts (Giỏ hàng người dùng)
CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    cafe_id UUID REFERENCES cafes(id) ON DELETE SET NULL,
    subtotal DECIMAL(12, 0) DEFAULT 0,
    item_count INTEGER DEFAULT 0,
    promo_code VARCHAR(50),
    discount_amount DECIMAL(12, 0) DEFAULT 0,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6.2 Bảng cart_items (Chi tiết món trong giỏ hàng)
CREATE TABLE IF NOT EXISTS cart_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    options JSONB DEFAULT '{}',
    modifiers JSONB DEFAULT '[]',
    options_json TEXT DEFAULT '{}',
    modifiers_json TEXT DEFAULT '[]',
    unit_price DECIMAL(12, 0) NOT NULL,
    line_total DECIMAL(12, 0) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6.3 Bảng orders (Đơn hàng)
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    cafe_id UUID NOT NULL REFERENCES cafes(id),
    seat_id UUID REFERENCES seats(id) ON DELETE SET NULL,
    order_type VARCHAR(20) NOT NULL CHECK (order_type IN ('pickup', 'delivery', 'dine_in')),
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'preparing', 'ready', 'completed', 'cancelled')),
    payment_status VARCHAR(20) DEFAULT 'pending' CHECK (payment_status IN ('pending', 'paid', 'failed', 'refunded', 'partially_refunded')),
    payment_method VARCHAR(20),
    payment_id UUID,
    subtotal DECIMAL(12, 0) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12, 0) DEFAULT 0,
    promo_code VARCHAR(50),
    tax_amount DECIMAL(12, 0) DEFAULT 0,
    tax_rate DECIMAL(5, 4) DEFAULT 0.1,
    delivery_fee DECIMAL(12, 0) DEFAULT 0,
    total_amount DECIMAL(12, 0) NOT NULL,
    total_paid DECIMAL(12, 0) DEFAULT 0,
    delivery_address_id UUID REFERENCES user_addresses(id) ON DELETE SET NULL,
    delivery_address TEXT,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    points_earned INTEGER DEFAULT 0,
    points_redeemed INTEGER DEFAULT 0,
    table_id UUID REFERENCES seats(id) ON DELETE SET NULL,
    customer_note TEXT,
    staff_note TEXT,
    cancellation_reason TEXT,
    estimated_ready_time TIMESTAMP WITH TIME ZONE,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    preparing_at TIMESTAMP WITH TIME ZONE,
    ready_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB DEFAULT '{}'
);

-- 6.4 Bảng order_items (Món chi tiết trong từng đơn)
CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id),
    variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
    product_name VARCHAR(255) NOT NULL,
    product_snapshot JSONB,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(12, 0) NOT NULL,
    options JSONB DEFAULT '{}',
    modifiers JSONB DEFAULT '[]',
    options_json TEXT DEFAULT '{}',
    modifiers_json TEXT DEFAULT '[]',
    line_discount DECIMAL(12, 0) DEFAULT 0,
    line_total DECIMAL(12, 0) NOT NULL,
    item_status VARCHAR(20) DEFAULT 'pending' CHECK (item_status IN ('pending', 'preparing', 'ready', 'served')),
    notes TEXT,
    preparing_at TIMESTAMP WITH TIME ZONE,
    ready_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 7. MODULE THANH TOÁN & HÓA ĐƠN (PAYMENTS & INVOICES)
-- ==============================================================================

-- 7.1 Bảng payments (Giao dịch thanh toán đơn hàng)
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    payment_method VARCHAR(20) NOT NULL CHECK (payment_method IN ('vnpay', 'momo', 'zalopay', 'cash', 'wallet', 'card')),
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'processing', 'completed', 'failed', 'cancelled', 'refunded')),
    amount DECIMAL(12, 0) NOT NULL,
    currency VARCHAR(10) DEFAULT 'VND',
    payment_url TEXT,
    return_url TEXT,
    callback_data JSONB,
    bank_code VARCHAR(20),
    card_type VARCHAR(20),
    payment_gateway VARCHAR(50),
    gateway_response TEXT,
    provider_transaction_id VARCHAR(100),
    paid_at TIMESTAMP WITH TIME ZONE,
    expired_at TIMESTAMP WITH TIME ZONE,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7.2 Bảng payment_transactions (Lịch sử chi tiết cuộc gọi cổng thanh toán)
CREATE TABLE IF NOT EXISTS payment_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_id UUID NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('create', 'callback', 'cancel', 'refund', 'query')),
    status VARCHAR(20) NOT NULL,
    request_data JSONB,
    response_data JSONB,
    provider VARCHAR(20),
    provider_transaction_id VARCHAR(100),
    error_code VARCHAR(50),
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7.3 Bảng refunds (Hoàn tiền cho khách hàng)
CREATE TABLE IF NOT EXISTS refunds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_id UUID NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    amount DECIMAL(12, 0) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'processing', 'completed', 'failed')),
    reason TEXT,
    admin_id UUID REFERENCES users(id) ON DELETE SET NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    provider_refund_id VARCHAR(100),
    refund_transaction_id VARCHAR(100),
    failure_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 8. MODULE KHUYẾN MÃI, HỘI VIÊN & TÍCH ĐIỂM (MARKETING & LOYALTY)
-- ==============================================================================

-- 8.1 Bảng promotions (Mã giảm giá, khuyến mãi)
CREATE TABLE IF NOT EXISTS promotions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('percentage', 'fixed_amount', 'free_delivery', 'buy_x_get_y')),
    discount_value DECIMAL(12, 0),
    min_order_amount DECIMAL(12, 0),
    max_discount_amount DECIMAL(12, 0),
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    usage_limit INTEGER,
    used_count INTEGER DEFAULT 0,
    per_user_limit INTEGER DEFAULT 1,
    applicable_products JSONB DEFAULT '[]',
    applicable_categories JSONB DEFAULT '[]',
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'expired', 'scheduled')),
    is_public BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.2 Bảng membership_plans (Gói thành viên định kỳ)
CREATE TABLE IF NOT EXISTS membership_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    description TEXT,
    description_vi TEXT,
    price_monthly DECIMAL(12, 0),
    price_yearly DECIMAL(12, 0),
    billing_cycle VARCHAR(20) DEFAULT 'monthly' CHECK (billing_cycle IN ('monthly', 'yearly')),
    credits_monthly INTEGER,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    free_delivery BOOLEAN DEFAULT FALSE,
    priority_support BOOLEAN DEFAULT FALSE,
    benefits JSONB DEFAULT '[]',
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.3 Bảng subscriptions (Thuê bao gói thành viên của khách)
CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES membership_plans(id) ON DELETE RESTRICT,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'paused', 'cancelled', 'expired', 'trial')),
    billing_cycle VARCHAR(20) NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    next_billing_date TIMESTAMP WITH TIME ZONE,
    trial_end_date TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    auto_renew BOOLEAN DEFAULT TRUE,
    credits_remaining INTEGER DEFAULT 0,
    credits_total INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.4 Bảng loyalty_tiers (Hạng hội viên: Đồng, Bạc, Vàng, Bạch Kim)
CREATE TABLE IF NOT EXISTS loyalty_tiers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    name_vi VARCHAR(50),
    min_points INTEGER NOT NULL DEFAULT 0,
    max_points INTEGER,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    points_multiplier DECIMAL(3, 2) DEFAULT 1.0,
    color VARCHAR(20),
    icon VARCHAR(10),
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.5 Bảng rewards (Kho quà tặng đổi điểm)
CREATE TABLE IF NOT EXISTS rewards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) DEFAULT 'discount',
    reward_type VARCHAR(20) DEFAULT 'discount' CHECK (reward_type IN ('discount', 'free_item', 'free_delivery', 'upgrade')),
    discount_type VARCHAR(20),
    discount_value DECIMAL(12, 0),
    min_order_amount DECIMAL(12, 0),
    max_discount_amount DECIMAL(12, 0),
    free_item_id UUID REFERENCES products(id) ON DELETE SET NULL,
    points_cost INTEGER NOT NULL DEFAULT 0,
    points_required INTEGER DEFAULT 0,
    benefits TEXT,
    min_tier VARCHAR(20) DEFAULT 'bronze',
    max_redemptions_per_user INTEGER DEFAULT 1,
    quantity INTEGER DEFAULT 1,
    total_quantity INTEGER DEFAULT 100,
    remaining_quantity INTEGER DEFAULT 100,
    quantity_remaining INTEGER,
    image_url VARCHAR(500),
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    validity_days INTEGER DEFAULT 30,
    applicable_tiers VARCHAR(100) DEFAULT 'bronze,silver,gold,platinum',
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'expired')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.6 Bảng loyalty_accounts (Tài khoản tích điểm)
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    tier_id UUID REFERENCES loyalty_tiers(id) ON DELETE SET NULL,
    points_balance BIGINT NOT NULL DEFAULT 0,
    points_lifetime BIGINT NOT NULL DEFAULT 0,
    lifetime_points BIGINT DEFAULT 0,
    points_used BIGINT NOT NULL DEFAULT 0,
    points_to_next_tier INTEGER DEFAULT 0,
    next_tier VARCHAR(50),
    total_orders INTEGER DEFAULT 0,
    total_spent DECIMAL(15, 2) DEFAULT 0,
    birthday_bonus_claimed BOOLEAN DEFAULT FALSE,
    anniversary_bonus_claimed BOOLEAN DEFAULT FALSE,
    tier_upgraded_at TIMESTAMP WITH TIME ZONE,
    tier_expires_at TIMESTAMP WITH TIME ZONE,
    last_earn_at TIMESTAMP WITH TIME ZONE,
    last_redeem_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.7 Bảng loyalty_transactions (Lịch sử cộng/trừ điểm thưởng)
CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('earn', 'redeem', 'expire', 'adjust', 'bonus')),
    points BIGINT NOT NULL,
    balance_after BIGINT,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    reward_id UUID REFERENCES rewards(id) ON DELETE SET NULL,
    description TEXT,
    expires_at TIMESTAMP WITH TIME ZONE,
    expired_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.8 Bảng customers (Dữ liệu thống kê khách hàng thân thiết CRM)
CREATE TABLE IF NOT EXISTS customers (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    loyalty_tier_id UUID REFERENCES loyalty_tiers(id) ON DELETE SET NULL,
    loyalty_points BIGINT DEFAULT 0,
    lifetime_points BIGINT DEFAULT 0,
    total_orders INT DEFAULT 0,
    total_spent DECIMAL(14, 0) DEFAULT 0,
    avg_order_value DECIMAL(12, 0) DEFAULT 0,
    visit_count INT DEFAULT 0,
    last_visit_at TIMESTAMP WITH TIME ZONE,
    first_order_at TIMESTAMP WITH TIME ZONE,
    favorite_products UUID[],
    preferred_payment VARCHAR(50),
    preferred_cafe_id UUID REFERENCES cafes(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.9 Bảng reward_redemptions (Lịch sử đổi quà của khách)
CREATE TABLE IF NOT EXISTS reward_redemptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    reward_id UUID NOT NULL REFERENCES rewards(id) ON DELETE RESTRICT,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    points_spent BIGINT NOT NULL DEFAULT 0,
    code VARCHAR(50),
    qr_code VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'pending', 'used', 'expired', 'cancelled')),
    valid_from TIMESTAMP WITH TIME ZONE,
    valid_until TIMESTAMP WITH TIME ZONE,
    redeemed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    expired_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8.10 Bảng invoices (Hóa đơn tài chính điện tử)
CREATE TABLE IF NOT EXISTS invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    subscription_id UUID REFERENCES subscriptions(id) ON DELETE SET NULL,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('subscription', 'credit', 'order', 'refund')),
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'issued', 'paid', 'cancelled', 'refunded')),
    subtotal DECIMAL(12, 0) NOT NULL,
    tax_rate DECIMAL(5, 4) DEFAULT 0,
    tax_amount DECIMAL(12, 0) DEFAULT 0,
    total_amount DECIMAL(12, 0) NOT NULL,
    payment_method VARCHAR(20),
    payment_id UUID REFERENCES payments(id) ON DELETE SET NULL,
    paid_at TIMESTAMP WITH TIME ZONE,
    due_date TIMESTAMP WITH TIME ZONE,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_address TEXT,
    customer_tax_id VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 9. MODULE AI & TÍN DỤNG (AI CREDITS & SESSIONS)
-- ==============================================================================

-- 9.1 Bảng credit_plans (Gói bán tín dụng AI)
CREATE TABLE IF NOT EXISTS credit_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    name_vi VARCHAR(255),
    credits INTEGER NOT NULL,
    price DECIMAL(12, 0) NOT NULL,
    credits_per_month INTEGER,
    features JSONB DEFAULT '[]',
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9.2 Bảng user_credits (Ví tín dụng AI của từng user)
CREATE TABLE IF NOT EXISTS user_credits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0,
    lifetime_earned DECIMAL(15, 2) NOT NULL DEFAULT 0,
    lifetime_used DECIMAL(15, 2) NOT NULL DEFAULT 0,
    version INTEGER DEFAULT 1,
    last_transaction_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9.3 Bảng credit_transactions (Nhật ký biến động tín dụng AI)
CREATE TABLE IF NOT EXISTS credit_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('purchase', 'usage', 'used', 'refund', 'refunded', 'bonus', 'earned', 'expiration', 'transfer')),
    amount DECIMAL(15, 2) NOT NULL,
    balance_before DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    description TEXT,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9.4 Bảng ai_sessions (Phiên tương tác trợ lý AI: chat, công thức, gợi ý)
CREATE TABLE IF NOT EXISTS ai_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_type VARCHAR(30) NOT NULL DEFAULT 'chat' CHECK (session_type IN ('chat', 'recipe', 'recommendation', 'image', 'video')),
    title VARCHAR(255),
    metadata JSONB DEFAULT '{}',
    message_count INTEGER DEFAULT 0,
    total_tokens INTEGER DEFAULT 0,
    total_cost DECIMAL(10, 4) DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    last_message_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9.5 Bảng ai_messages (Lịch sử hội thoại từng tin nhắn với AI)
CREATE TABLE IF NOT EXISTS ai_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL REFERENCES ai_sessions(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant', 'system')),
    content TEXT NOT NULL,
    model VARCHAR(50),
    tokens_used INTEGER DEFAULT 0,
    input_tokens INTEGER DEFAULT 0,
    output_tokens INTEGER DEFAULT 0,
    cost DECIMAL(10, 4) DEFAULT 0,
    error_message TEXT,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9.6 Bảng ai_usage (Thống kê và tính cước lượng token tiêu thụ)
CREATE TABLE IF NOT EXISTS ai_usage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    session_id UUID REFERENCES ai_sessions(id) ON DELETE SET NULL,
    model_id VARCHAR(50),
    provider VARCHAR(50),
    endpoint VARCHAR(50) DEFAULT 'chat',
    input_tokens INTEGER DEFAULT 0,
    output_tokens INTEGER DEFAULT 0,
    total_tokens INTEGER DEFAULT 0,
    cost DECIMAL(10, 4) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- 10. MODULE HỆ THỐNG, THÔNG BÁO & VẬN HÀNH (OPERATIONS & NOTIFICATIONS)
-- ==============================================================================

-- 10.1 Bảng audit_logs (Nhật ký kiểm toán hệ thống)
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    old_data JSONB,
    new_data JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10.2 Bảng notification_templates (Mẫu thông báo)
CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    channel VARCHAR(20) NOT NULL CHECK (channel IN ('email', 'sms', 'push', 'in_app')),
    subject VARCHAR(255),
    body_template TEXT NOT NULL,
    variables JSONB DEFAULT '[]',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10.3 Bảng notifications (Thông báo gửi cho người dùng)
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    template_id UUID REFERENCES notification_templates(id) ON DELETE SET NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    data JSONB,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'sent', 'failed', 'read')),
    sent_at TIMESTAMP WITH TIME ZONE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10.4 Bảng device_tokens (Thiết bị nhận Push Notification FCM/APNS)
CREATE TABLE IF NOT EXISTS device_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL,
    platform VARCHAR(20) NOT NULL CHECK (platform IN ('ios', 'android', 'web')),
    device_info JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    last_used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, token)
);

-- 10.4.1 Bảng user_devices (Thiết bị và FCM token theo phiên đăng nhập người dùng)
CREATE TABLE IF NOT EXISTS user_devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_type VARCHAR(50) DEFAULT 'android',
    device_id VARCHAR(255),
    fcm_token VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    last_active_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_user_devices_user_id ON user_devices(user_id);
CREATE INDEX IF NOT EXISTS idx_user_devices_token ON user_devices(fcm_token);

-- 10.5 Bảng expense_categories (Danh mục chi phí hoạt động)
CREATE TABLE IF NOT EXISTS expense_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    name_vi VARCHAR(100),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10.6 Bảng translations (Đa ngôn ngữ i18n)
CREATE TABLE IF NOT EXISTS translations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    locale VARCHAR(10) NOT NULL,
    namespace VARCHAR(50) NOT NULL DEFAULT 'common',
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(locale, namespace, key)
);

-- ==============================================================================
-- 11. KHỞI TẠO CHỈ MỤC TỐI ƯU HÓA HIỆU NĂNG (INDEXES)
-- ==============================================================================

CREATE INDEX IF NOT EXISTS idx_users_company ON users(company_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_tier ON users(tier);

CREATE INDEX IF NOT EXISTS idx_user_profiles_phone ON user_profiles(phone);
CREATE INDEX IF NOT EXISTS idx_user_profiles_full_name ON user_profiles(full_name);

CREATE INDEX IF NOT EXISTS idx_products_company ON products(company_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_slug ON products(slug);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_featured ON products(is_featured);
CREATE INDEX IF NOT EXISTS idx_products_best_seller ON products(is_best_seller);

CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_company ON orders(company_id);
CREATE INDEX IF NOT EXISTS idx_orders_cafe ON orders(cafe_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_payment_status ON orders(payment_status);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id);

CREATE INDEX IF NOT EXISTS idx_payments_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_user ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_transaction ON payments(transaction_id);

CREATE INDEX IF NOT EXISTS idx_carts_user ON carts(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product ON cart_items(product_id);

CREATE INDEX IF NOT EXISTS idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_plan ON subscriptions(plan_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);

CREATE INDEX IF NOT EXISTS idx_loyalty_user ON loyalty_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_tier ON loyalty_accounts(tier_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_txn_user ON loyalty_transactions(user_id);

CREATE INDEX IF NOT EXISTS idx_seats_cafe ON seats(cafe_id);
CREATE INDEX IF NOT EXISTS idx_seat_reservations_seat ON seat_reservations(seat_id);
CREATE INDEX IF NOT EXISTS idx_seat_reservations_user ON seat_reservations(user_id);

CREATE INDEX IF NOT EXISTS idx_ai_sessions_user ON ai_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_messages_session ON ai_messages(session_id);
CREATE INDEX IF NOT EXISTS idx_ai_usage_user ON ai_usage(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_usage_session ON ai_usage(session_id);

CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_logs(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_created ON notifications(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_translations_locale ON translations(locale, namespace);

-- ==============================================================================
-- 12. STORED FUNCTIONS & TRIGGERS (HÀM & TRIGGER NỘI HỆ THỐNG)
-- ==============================================================================

-- 12.1 Hàm tự động cập nhật timestamp updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Áp dụng trigger updated_at cho tất cả bảng có trường updated_at
DROP TRIGGER IF EXISTS trg_companies_updated_at ON companies;
CREATE TRIGGER trg_companies_updated_at BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_cafes_updated_at ON cafes;
CREATE TRIGGER trg_cafes_updated_at BEFORE UPDATE ON cafes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_user_profiles_updated_at ON user_profiles;
CREATE TRIGGER trg_user_profiles_updated_at BEFORE UPDATE ON user_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_user_preferences_updated_at ON user_preferences;
CREATE TRIGGER trg_user_preferences_updated_at BEFORE UPDATE ON user_preferences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_staffs_updated_at ON staffs;
CREATE TRIGGER trg_staffs_updated_at BEFORE UPDATE ON staffs FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_units_updated_at ON units;
CREATE TRIGGER trg_units_updated_at BEFORE UPDATE ON units FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_suppliers_updated_at ON suppliers;
CREATE TRIGGER trg_suppliers_updated_at BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_ingredients_updated_at ON ingredients;
CREATE TRIGGER trg_ingredients_updated_at BEFORE UPDATE ON ingredients FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_categories_updated_at ON categories;
CREATE TRIGGER trg_categories_updated_at BEFORE UPDATE ON categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_products_updated_at ON products;
CREATE TRIGGER trg_products_updated_at BEFORE UPDATE ON products FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_seats_updated_at ON seats;
CREATE TRIGGER trg_seats_updated_at BEFORE UPDATE ON seats FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_seat_reservations_updated_at ON seat_reservations;
CREATE TRIGGER trg_seat_reservations_updated_at BEFORE UPDATE ON seat_reservations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_carts_updated_at ON carts;
CREATE TRIGGER trg_carts_updated_at BEFORE UPDATE ON carts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_orders_updated_at ON orders;
CREATE TRIGGER trg_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_payments_updated_at ON payments;
CREATE TRIGGER trg_payments_updated_at BEFORE UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_refunds_updated_at ON refunds;
CREATE TRIGGER trg_refunds_updated_at BEFORE UPDATE ON refunds FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_rewards_updated_at ON rewards;
CREATE TRIGGER trg_rewards_updated_at BEFORE UPDATE ON rewards FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_promotions_updated_at ON promotions;
CREATE TRIGGER trg_promotions_updated_at BEFORE UPDATE ON promotions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_membership_plans_updated_at ON membership_plans;
CREATE TRIGGER trg_membership_plans_updated_at BEFORE UPDATE ON membership_plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_subscriptions_updated_at ON subscriptions;
CREATE TRIGGER trg_subscriptions_updated_at BEFORE UPDATE ON subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_user_credits_updated_at ON user_credits;
CREATE TRIGGER trg_user_credits_updated_at BEFORE UPDATE ON user_credits FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_loyalty_accounts_updated_at ON loyalty_accounts;
CREATE TRIGGER trg_loyalty_accounts_updated_at BEFORE UPDATE ON loyalty_accounts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_customers_updated_at ON customers;
CREATE TRIGGER trg_customers_updated_at BEFORE UPDATE ON customers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_invoices_updated_at ON invoices;
CREATE TRIGGER trg_invoices_updated_at BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_ai_sessions_updated_at ON ai_sessions;
CREATE TRIGGER trg_ai_sessions_updated_at BEFORE UPDATE ON ai_sessions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_notification_templates_updated_at ON notification_templates;
CREATE TRIGGER trg_notification_templates_updated_at BEFORE UPDATE ON notification_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trg_translations_updated_at ON translations;
CREATE TRIGGER trg_translations_updated_at BEFORE UPDATE ON translations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 12.2 Hàm tự động sinh mã đơn hàng (Order Number: YYYYMMDD000001)
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TRIGGER AS $$
DECLARE
    v_prefix VARCHAR(10);
    v_seq_num INT;
BEGIN
    v_prefix := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');
    
    SELECT COALESCE(MAX(
        NULLIF(regexp_replace(SUBSTRING(order_number FROM 9), '[^0-9]', '', 'g'), '')::INT
    ), 0) + 1 INTO v_seq_num
    FROM orders
    WHERE order_number LIKE v_prefix || '%';
    
    NEW.order_number := v_prefix || LPAD(v_seq_num::TEXT, 6, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_generate_order_number ON orders;
CREATE TRIGGER trg_generate_order_number
    BEFORE INSERT ON orders
    FOR EACH ROW
    WHEN (NEW.order_number IS NULL OR NEW.order_number = '')
    EXECUTE FUNCTION generate_order_number();

-- 12.3 Hàm tự động sinh mã đặt bàn (Reservation Code: 8 ký tự ngẫu nhiên)
CREATE OR REPLACE FUNCTION generate_reservation_code()
RETURNS TRIGGER AS $$
DECLARE
    v_chars TEXT := 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    v_code VARCHAR(8) := '';
    i INT;
BEGIN
    FOR i IN 1..8 LOOP
        v_code := v_code || SUBSTRING(v_chars FROM floor(random() * 32 + 1)::INT FOR 1);
    END LOOP;
    
    NEW.reservation_code := v_code;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_generate_reservation_code ON seat_reservations;
CREATE TRIGGER trg_generate_reservation_code
    BEFORE INSERT ON seat_reservations
    FOR EACH ROW
    WHEN (NEW.reservation_code IS NULL OR NEW.reservation_code = '')
    EXECUTE FUNCTION generate_reservation_code();

-- 12.4 Hàm cập nhật thống kê phiên AI khi có tin nhắn mới
CREATE OR REPLACE FUNCTION update_ai_session_stats()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ai_sessions
    SET 
        message_count = message_count + 1,
        total_tokens = total_tokens + COALESCE(NEW.tokens_used, COALESCE(NEW.input_tokens, 0) + COALESCE(NEW.output_tokens, 0)),
        total_cost = total_cost + COALESCE(NEW.cost, 0),
        last_message_at = NEW.created_at,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.session_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_ai_session_stats ON ai_messages;
CREATE TRIGGER trg_update_ai_session_stats
    AFTER INSERT ON ai_messages
    FOR EACH ROW
    EXECUTE FUNCTION update_ai_session_stats();

-- 12.5 Hàm tự động ghi log sử dụng AI vào ai_usage
CREATE OR REPLACE FUNCTION record_ai_usage()
RETURNS TRIGGER AS $$
DECLARE
    v_user_id UUID;
BEGIN
    SELECT user_id INTO v_user_id FROM ai_sessions WHERE id = NEW.session_id;

    INSERT INTO ai_usage (
        user_id, session_id, model_id, provider,
        input_tokens, output_tokens, total_tokens, cost, endpoint
    ) VALUES (
        v_user_id, NEW.session_id, NEW.model, 'system',
        COALESCE(NEW.input_tokens, 0),
        COALESCE(NEW.output_tokens, 0),
        COALESCE(NEW.tokens_used, COALESCE(NEW.input_tokens, 0) + COALESCE(NEW.output_tokens, 0)),
        COALESCE(NEW.cost, 0), 'chat'
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_record_ai_usage ON ai_messages;
CREATE TRIGGER trg_record_ai_usage
    AFTER INSERT ON ai_messages
    FOR EACH ROW
    WHEN (NEW.role = 'assistant' AND NEW.error_message IS NULL)
    EXECUTE FUNCTION record_ai_usage();

-- 12.6 Hàm tự động tính toán lại tổng tiền đơn hàng khi món thay đổi
CREATE OR REPLACE FUNCTION update_order_totals()
RETURNS TRIGGER AS $$
DECLARE
    v_order_id UUID;
    v_subtotal DECIMAL(12, 0);
    v_tax_rate DECIMAL(5, 4);
    v_delivery_fee DECIMAL(12, 0);
    v_discount DECIMAL(12, 0);
BEGIN
    IF TG_OP = 'DELETE' THEN
        v_order_id := OLD.order_id;
    ELSE
        v_order_id := NEW.order_id;
    END IF;

    SELECT COALESCE(SUM(line_total), 0) INTO v_subtotal
    FROM order_items
    WHERE order_id = v_order_id;

    SELECT COALESCE(tax_rate, 0.1), COALESCE(delivery_fee, 0), COALESCE(discount_amount, 0) 
    INTO v_tax_rate, v_delivery_fee, v_discount
    FROM orders
    WHERE id = v_order_id;

    UPDATE orders
    SET 
        subtotal = v_subtotal,
        tax_amount = ROUND(v_subtotal * v_tax_rate),
        total_amount = GREATEST(0, v_subtotal + ROUND(v_subtotal * v_tax_rate) + v_delivery_fee - v_discount),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = v_order_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_order_items_totals ON order_items;
CREATE TRIGGER trg_order_items_totals
    AFTER INSERT OR UPDATE OR DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_totals();

-- 12.7 Stored Function: Khấu trừ tín dụng AI (deduct_credits) an toàn với FOR UPDATE
CREATE OR REPLACE FUNCTION deduct_credits(
    p_user_id UUID,
    p_amount DECIMAL,
    p_reference_type VARCHAR(50),
    p_reference_id UUID,
    p_description TEXT
)
RETURNS TABLE(success BOOLEAN, new_balance DECIMAL, error_message TEXT) AS $$
DECLARE
    v_balance DECIMAL;
    v_new_balance DECIMAL;
BEGIN
    SELECT balance INTO v_balance
    FROM user_credits
    WHERE user_id = p_user_id
    FOR UPDATE;
    
    IF v_balance IS NULL THEN
        RETURN QUERY SELECT FALSE, 0::DECIMAL, 'User credits record not found'::TEXT;
        RETURN;
    END IF;
    
    IF v_balance < p_amount THEN
        RETURN QUERY SELECT FALSE, v_balance, 'Insufficient credits'::TEXT;
        RETURN;
    END IF;
    
    v_new_balance := v_balance - p_amount;
    
    UPDATE user_credits
    SET 
        balance = v_new_balance,
        lifetime_used = lifetime_used + p_amount,
        last_transaction_at = CURRENT_TIMESTAMP,
        updated_at = CURRENT_TIMESTAMP,
        version = version + 1
    WHERE user_id = p_user_id;
    
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

-- 12.8 Stored Function: Hoàn trả tín dụng AI (refund_credits)
CREATE OR REPLACE FUNCTION refund_credits(
    p_user_id UUID,
    p_amount DECIMAL,
    p_reference_type VARCHAR(50),
    p_reference_id UUID,
    p_description TEXT
)
RETURNS TABLE(success BOOLEAN, new_balance DECIMAL, error_message TEXT) AS $$
DECLARE
    v_balance DECIMAL;
    v_new_balance DECIMAL;
BEGIN
    SELECT balance INTO v_balance
    FROM user_credits
    WHERE user_id = p_user_id
    FOR UPDATE;
    
    IF v_balance IS NULL THEN
        INSERT INTO user_credits (user_id, balance, lifetime_earned) VALUES (p_user_id, 0, 0);
        v_balance := 0;
    END IF;
    
    v_new_balance := v_balance + p_amount;
    
    UPDATE user_credits
    SET 
        balance = v_new_balance,
        lifetime_earned = lifetime_earned + p_amount,
        last_transaction_at = CURRENT_TIMESTAMP,
        updated_at = CURRENT_TIMESTAMP,
        version = version + 1
    WHERE user_id = p_user_id;
    
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


-- ==============================================================================
-- 12.9 HÀM & TRIGGER ĐỒNG BỘ HAI CHIỀU JSONB VÀ TEXT
-- Đảm bảo tương thích hoàn hảo giữa các Service đọc JSONB hoặc TEXT
-- ==============================================================================

CREATE OR REPLACE FUNCTION sync_json_fields()
RETURNS TRIGGER AS $$
BEGIN
    -- Đồng bộ cho cart_items và order_items
    IF TG_TABLE_NAME IN ('cart_items', 'order_items') THEN
        IF NEW.options IS NOT NULL AND (NEW.options_json IS NULL OR NEW.options_json = '' OR NEW.options_json = '{}') THEN
            NEW.options_json := NEW.options::TEXT;
        ELSIF NEW.options_json IS NOT NULL AND NEW.options_json != '' AND (NEW.options IS NULL OR NEW.options = '{}'::JSONB) THEN
            NEW.options := NEW.options_json::JSONB;
        END IF;

        IF NEW.modifiers IS NOT NULL AND (NEW.modifiers_json IS NULL OR NEW.modifiers_json = '' OR NEW.modifiers_json = '[]') THEN
            NEW.modifiers_json := NEW.modifiers::TEXT;
        ELSIF NEW.modifiers_json IS NOT NULL AND NEW.modifiers_json != '' AND (NEW.modifiers IS NULL OR NEW.modifiers = '[]'::JSONB) THEN
            NEW.modifiers := NEW.modifiers_json::JSONB;
        END IF;
    END IF;

    -- Đồng bộ cho products
    IF TG_TABLE_NAME = 'products' THEN
        IF NEW.images IS NOT NULL AND (NEW.images_json IS NULL OR NEW.images_json = '' OR NEW.images_json = '[]') THEN
            NEW.images_json := NEW.images::TEXT;
        ELSIF NEW.images_json IS NOT NULL AND NEW.images_json != '' AND (NEW.images IS NULL OR NEW.images = '[]'::JSONB) THEN
            NEW.images := NEW.images_json::JSONB;
        END IF;

        IF NEW.options IS NOT NULL AND (NEW.options_json IS NULL OR NEW.options_json = '' OR NEW.options_json = '[]') THEN
            NEW.options_json := NEW.options::TEXT;
        ELSIF NEW.options_json IS NOT NULL AND NEW.options_json != '' AND (NEW.options IS NULL OR NEW.options = '[]'::JSONB) THEN
            NEW.options := NEW.options_json::JSONB;
        END IF;

        IF NEW.modifiers IS NOT NULL AND (NEW.modifiers_json IS NULL OR NEW.modifiers_json = '' OR NEW.modifiers_json = '[]') THEN
            NEW.modifiers_json := NEW.modifiers::TEXT;
        ELSIF NEW.modifiers_json IS NOT NULL AND NEW.modifiers_json != '' AND (NEW.modifiers IS NULL OR NEW.modifiers = '[]'::JSONB) THEN
            NEW.modifiers := NEW.modifiers_json::JSONB;
        END IF;

        IF NEW.tags IS NOT NULL AND (NEW.tags_json IS NULL OR NEW.tags_json = '' OR NEW.tags_json = '[]') THEN
            NEW.tags_json := NEW.tags::TEXT;
        ELSIF NEW.tags_json IS NOT NULL AND NEW.tags_json != '' AND (NEW.tags IS NULL OR NEW.tags = '[]'::JSONB) THEN
            NEW.tags := NEW.tags_json::JSONB;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sync_cart_items_json ON cart_items;
CREATE TRIGGER trg_sync_cart_items_json
    BEFORE INSERT OR UPDATE ON cart_items
    FOR EACH ROW EXECUTE FUNCTION sync_json_fields();

DROP TRIGGER IF EXISTS trg_sync_order_items_json ON order_items;
CREATE TRIGGER trg_sync_order_items_json
    BEFORE INSERT OR UPDATE ON order_items
    FOR EACH ROW EXECUTE FUNCTION sync_json_fields();

DROP TRIGGER IF EXISTS trg_sync_products_json ON products;
CREATE TRIGGER trg_sync_products_json
    BEFORE INSERT OR UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION sync_json_fields();

COMMIT;
