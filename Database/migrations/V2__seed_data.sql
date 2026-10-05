-- ================================================
-- AI CAFÉ PLATFORM - SEED DATA
-- Version: 1.0.0
-- ================================================

-- ================================================
-- UNITS TABLE
-- ================================================
INSERT INTO units (id, name, name_vi, abbreviation, unit_type, conversion_factor, is_active) VALUES
('unit-pieces', 'Pieces', 'Cái', 'pc', 'count', 1, TRUE),
('unit-kg', 'Kilograms', 'Kilôgam', 'kg', 'weight', 1000, TRUE),
('unit-g', 'Grams', 'Gam', 'g', 'weight', 1, TRUE),
('unit-l', 'Liters', 'Lít', 'L', 'volume', 1000, TRUE),
('unit-ml', 'Milliliters', 'Mililít', 'ml', 'volume', 1, TRUE),
('unit-cup', 'Cups', 'Cốc', 'cup', 'volume', 240, TRUE),
('unit-spoon', 'Tablespoons', 'Muỗng canh', 'tbsp', 'volume', 15, TRUE),
('unit-teaspoon', 'Teaspoons', 'Muỗng cà phê', 'tsp', 'volume', 5, TRUE);

-- ================================================
-- ALLERGENS TABLE
-- ================================================
INSERT INTO allergens (company_id, name, name_vi, icon, color, description) VALUES
('00000000-0000-0000-0000-000000000001', 'Dairy', 'Sữa', '🥛', '#FF6B6B', 'Contains milk and dairy products'),
('00000000-0000-0000-0000-000000000001', 'Gluten', 'Gluten', '🌾', '#FFE66D', 'Contains wheat, barley, rye'),
('00000000-0000-0000-0000-000000000001', 'Nuts', 'Các loại hạt', '🥜', '#4ECDC4', 'Contains tree nuts or peanuts'),
('00000000-0000-0000-0000-000000000001', 'Eggs', 'Trứng', '🥚', '#FFEAA7', 'Contains eggs'),
('00000000-0000-0000-0000-000000000001', 'Soy', 'Đậu nành', '🫘', '#A29BFE', 'Contains soy products'),
('00000000-0000-0000-0000-000000000001', 'Caffeine', 'Caffeine', '☕', '#6C5CE7', 'Contains caffeine'),
('00000000-0000-0000-0000-000000000001', 'Alcohol', 'Rượu', '🍷', '#FD79A8', 'Contains alcohol'),
('00000000-0000-0000-0000-000000000001', 'Seafood', 'Hải sản', '🦐', '#00B894', 'Contains seafood');

-- ================================================
-- LOYALTY TIERS
-- ================================================
INSERT INTO loyalty_tiers (company_id, name, name_vi, min_points, max_points, discount_percent, points_multiplier, color, icon, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', 'Bronze', 'Đồng', 0, 999, 0, 1.0, '#CD7F32', '🥉', 1),
('00000000-0000-0000-0000-000000000001', 'Silver', 'Bạc', 1000, 4999, 2, 1.25, '#C0C0C0', '🥈', 2),
('00000000-0000-0000-0000-000000000001', 'Gold', 'Vàng', 5000, 19999, 5, 1.5, '#FFD700', '🥇', 3),
('00000000-0000-0000-0000-000000000001', 'Platinum', 'Bạch Kim', 20000, NULL, 10, 2.0, '#E5E4E2', '💎', 4);

-- ================================================
-- CREDIT PLANS
-- ================================================
INSERT INTO credit_plans (company_id, name, name_vi, credits, price, credits_per_month, features, is_active, is_featured, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', 'Starter', 'Khởi đầu', 100, 29000, NULL, '["100 AI credits", "Basic models", "Email support"]', TRUE, FALSE, 1),
('00000000-0000-0000-0000-000000000001', 'Pro', 'Chuyên nghiệp', 1000, 199000, NULL, '["1000 AI credits", "All models", "Priority support"]', TRUE, TRUE, 2),
('00000000-0000-0000-0000-000000000001', 'Enterprise', 'Doanh nghiệp', 5000, 790000, NULL, '["5000 AI credits", "All models", "24/7 support", "API access"]', TRUE, FALSE, 3);

-- ================================================
-- EXPENSE CATEGORIES
-- ================================================
INSERT INTO expense_categories (company_id, name, name_vi, description, is_active, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', 'Supplies', 'Vật tư', 'Office and cafe supplies', TRUE, 1),
('00000000-0000-0000-0000-000000000001', 'Utilities', 'Tiện ích', 'Electricity, water, internet', TRUE, 2),
('00000000-0000-0000-0000-000000000001', 'Rent', 'Thuê nhà', 'Cafe rent and lease', TRUE, 3),
('00000000-0000-0000-0000-000000000001', 'Salaries', 'Lương', 'Staff salaries', TRUE, 4),
('00000000-0000-0000-0000-000000000001', 'Marketing', 'Marketing', 'Advertising and promotions', TRUE, 5),
('00000000-0000-0000-0000-000000000001', 'Maintenance', 'Bảo trì', 'Equipment and furniture maintenance', TRUE, 6),
('00000000-0000-0000-0000-000000000001', 'Food Ingredients', 'Nguyên liệu', 'Food and beverage ingredients', TRUE, 7),
('00000000-0000-0000-0000-000000000001', 'Other', 'Khác', 'Other expenses', TRUE, 99);

-- ================================================
-- DEMO COMPANY, REGION, CAFE
-- ================================================
INSERT INTO companies (id, company_code, name, legal_name, tax_id, address, phone, email, status) VALUES
('00000000-0000-0000-0000-000000000001', 'AICA', 'AI Café Vietnam', 'Công Ty TNHH AI Café Việt Nam', '0123456789', '123 Nguyễn Huệ, Quận 1, TP.HCM', '02812345678', 'contact@aicafe.vn', 'active');

INSERT INTO regions (id, company_id, name, code, region_type, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Ho Chi Minh City', 'HCM', 'city', 1),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'District 1', 'D1', 'district', 1);

INSERT INTO cafes (id, company_id, region_id, cafe_code, name, slug, description, address, phone, is_active, is_delivery_enabled, is_pickup_enabled, is_reservation_enabled, tax_rate) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'AICA-HCM-001', 'AI Café Nguyễn Huệ', 'aica-nguyen-hue', 'flagship store', '123 Nguyễn Huệ, Quận 1, TP.HCM', '02812345678', TRUE, TRUE, TRUE, TRUE, 0.1),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'AICA-HCM-002', 'AI Café Đồng Khởi', 'aica-dong-khoi', 'Cozy corner cafe', '45 Đồng Khởi, Quận 1, TP.HCM', '02823456789', TRUE, TRUE, TRUE, TRUE, 0.1);

-- ================================================
-- CATEGORIES & PRODUCTS
-- ================================================
INSERT INTO categories (id, company_id, name, slug, icon, color, sort_order, is_featured, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Coffee', 'coffee', '☕', '#6F4E37', 1, TRUE, TRUE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Tea', 'tea', '🍵', '#98D8C8', 2, TRUE, TRUE),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Fresh Juice', 'fresh-juice', '🧃', '#F7DC6F', 3, TRUE, TRUE),
('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'Bakery', 'bakery', '🥐', '#F8B500', 4, FALSE, TRUE),
('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 'Desserts', 'desserts', '🍰', '#FF69B4', 5, TRUE, TRUE),
('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000001', 'AI Specials', 'ai-specials', '🤖', '#9B59B6', 0, TRUE, TRUE);

INSERT INTO products (id, company_id, category_id, name, name_vi, slug, sku, price, is_active, is_featured, is_best_seller, preparation_time, calories) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Espresso', 'Espresso', 'espresso', 'CFE001', 29000, TRUE, FALSE, FALSE, 3, 5),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Cappuccino', 'Cappuccino', 'cappuccino', 'CFE002', 45000, TRUE, TRUE, TRUE, 5, 120),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Latte', 'Latte', 'latte', 'CFE003', 45000, TRUE, TRUE, TRUE, 5, 190),
('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Americano', 'Americano', 'americano', 'CFE004', 35000, TRUE, FALSE, FALSE, 3, 10),
('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Cold Brew', 'Cold Brew', 'cold-brew', 'CFE005', 55000, TRUE, TRUE, FALSE, 3, 5),
('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Green Tea', 'Trà Xanh', 'green-tea', 'TEA001', 35000, TRUE, FALSE, FALSE, 4, 0),
('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Matcha Latte', 'Matcha Latte', 'matcha-latte', 'TEA002', 55000, TRUE, TRUE, TRUE, 5, 200),
('00000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Pearl Milk Tea', 'Trà Sữa Trân Châu', 'pearl-milk-tea', 'TEA003', 45000, TRUE, TRUE, TRUE, 5, 250),
('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Orange Juice', 'Nước Cam', 'orange-juice', 'JUC001', 45000, TRUE, TRUE, TRUE, 3, 112),
('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Smoothie Bowl', 'Smoothie Bowl', 'smoothie-bowl', 'JUC002', 75000, TRUE, FALSE, FALSE, 5, 320),
('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', 'Croissant', 'Bánh Croissant', 'croissant', 'BAK001', 35000, TRUE, TRUE, TRUE, 1, 231),
('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', 'Banana Bread', 'Bánh Mì Chuối', 'banana-bread', 'BAK002', 40000, TRUE, FALSE, FALSE, 1, 280),
('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 'Cheesecake', 'Bánh Phô Mai', 'cheesecake', 'DES001', 65000, TRUE, TRUE, TRUE, 2, 400),
('00000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 'Tiramisu', 'Tiramisu', 'tiramisu', 'DES002', 65000, TRUE, TRUE, TRUE, 2, 380),
('00000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006', 'AI Latte Art', 'Latte Nghệ Thuật AI', 'ai-latte-art', 'AISP001', 65000, TRUE, TRUE, FALSE, 5, 190),
('00000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006', 'Personalized Blend', 'Cà Phê Cá Nhân Hóa', 'personalized-blend', 'AISP002', 85000, TRUE, TRUE, FALSE, 8, 5);

-- ================================================
-- DEMO USERS
-- ================================================
INSERT INTO users (id, email, phone, password_hash, role, status, email_verified_at) VALUES
('00000000-0000-0000-0000-000000000001', 'admin@aicafe.vn', '0909000001', '$2b$10$abcdefghijklmnopqrstuv', 'admin', 'active', CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000002', 'staff@aicafe.vn', '0909000002', '$2b$10$abcdefghijklmnopqrstuv', 'staff', 'active', CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000003', 'customer@example.com', '0909000003', '$2b$10$abcdefghijklmnopqrstuv', 'customer', 'active', CURRENT_TIMESTAMP);

INSERT INTO user_profiles (user_id, full_name, display_name) VALUES
('00000000-0000-0000-0000-000000000001', 'Admin User', 'Admin'),
('00000000-0000-0000-0000-000000000002', 'Staff Member', 'Staff'),
('00000000-0000-0000-0000-000000000003', 'Demo Customer', 'Customer');

INSERT INTO staffs (id, user_id, company_id, cafe_id, staff_code, role, hire_date, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'STF001', 'barista', '2024-01-15', TRUE);

INSERT INTO user_credits (user_id, balance, lifetime_earned, lifetime_used) VALUES
('00000000-0000-0000-0000-000000000001', 5000, 5000, 0),
('00000000-0000-0000-0000-000000000003', 1000, 1000, 0);

INSERT INTO customers (user_id, loyalty_tier_id, loyalty_points, lifetime_points, total_orders, total_spent, visit_count) VALUES
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 150, 150, 3, 450000, 5);

-- ================================================
-- INGREDIENTS & SUPPLIERS
-- ================================================
INSERT INTO ingredients (id, company_id, name, name_vi, category, unit_id, cost_price, min_stock_level, max_stock_level, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Arabica Coffee Beans', 'Hạt Cà Phê Arabica', 'Coffee', 'unit-kg', 250000, 5, 100, TRUE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Milk', 'Sữa Tươi', 'Dairy', 'unit-l', 35000, 20, 200, TRUE),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Sugar', 'Đường', 'Sweeteners', 'unit-kg', 18000, 10, 100, TRUE);

INSERT INTO suppliers (id, company_id, name, code, contact_name, phone, email, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Highland Coffee Supply', 'HCS', 'Nguyễn Văn A', '0901234567', 'contact@highland.vn', TRUE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Fresh Dairy Co', 'FDC', 'Trần Thị B', '0902345678', 'sales@freshdairy.vn', TRUE);

-- ================================================
-- I18N TRANSLATIONS
-- ================================================
INSERT INTO translations (locale, namespace, key, value) VALUES
('en', 'common', 'welcome', 'Welcome to AI Café'),
('vi', 'common', 'welcome', 'Chào mừng đến với AI Café'),
('en', 'common', 'menu', 'Menu'),
('vi', 'common', 'menu', 'Thực đơn'),
('en', 'order', 'status.pending', 'Pending'),
('vi', 'order', 'status.pending', 'Đang chờ'),
('en', 'order', 'status.preparing', 'Preparing'),
('vi', 'order', 'status.preparing', 'Đang chuẩn bị'),
('en', 'order', 'status.ready', 'Ready'),
('vi', 'order', 'status.ready', 'Sẵn sàng');