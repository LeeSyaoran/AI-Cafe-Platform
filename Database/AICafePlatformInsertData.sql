-- ==============================================================================
-- AI CAFÉ PLATFORM - SEED & INITIAL DATA INSERTION
-- File: AICafePlatformInsertData.sql
-- Description: Chứa toàn bộ dữ liệu mẫu khởi tạo (DML: INSERT INTO)
--              cho hệ thống AI Café Platform (Đầy đủ cả 48 bảng).
--              Được sắp xếp chuẩn xác 100% theo đúng thứ tự phụ thuộc khóa ngoại.
-- ==============================================================================

ROLLBACK;
BEGIN;

-- ==============================================================================
-- 1. DOANH NGHIỆP & CÔNG TY CHỦ QUẢN (COMPANIES)
-- ==============================================================================
INSERT INTO companies (id, company_code, name, legal_name, tax_id, address, phone, email, description, status, is_active) VALUES
('00000000-0000-0000-0000-000000000001', 'AICA', 'AI Café Vietnam', 'Công Ty TNHH AI Café Việt Nam', '0123456789', '123 Nguyễn Huệ, Quận 1, TP.HCM', '02812345678', 'contact@aicafe.vn', 'Nền tảng đặt đồ uống thông minh kết hợp trợ lý AI thế hệ mới', 'active', TRUE),
('a0000000-0000-0000-0000-000000000001', 'AICAFE', 'AI Café Platform Corp', 'Công Ty Cổ Phần AI Café Platform', '0987654321', '456 Điện Biên Phủ, Quận 3, TP.HCM', '02887654321', 'support@aicafe.vn', 'Hệ sinh thái quán café công nghệ đa chi nhánh', 'active', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 2. ĐƠN VỊ TÍNH (UNITS)
-- ==============================================================================
INSERT INTO units (id, name, name_vi, abbreviation, unit_type, conversion_factor, is_active) VALUES
('unit-pieces', 'Pieces', 'Cái / Chiếc', 'pc', 'count', 1, TRUE),
('unit-kg', 'Kilograms', 'Kilôgam', 'kg', 'weight', 1000, TRUE),
('unit-g', 'Grams', 'Gam', 'g', 'weight', 1, TRUE),
('unit-l', 'Liters', 'Lít', 'L', 'volume', 1000, TRUE),
('unit-ml', 'Milliliters', 'Mililít', 'ml', 'volume', 1, TRUE),
('unit-cup', 'Cups', 'Cốc / Ly', 'cup', 'volume', 240, TRUE),
('unit-spoon', 'Tablespoons', 'Muỗng canh', 'tbsp', 'volume', 15, TRUE),
('unit-teaspoon', 'Teaspoons', 'Muỗng cà phê', 'tsp', 'volume', 5, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 3. NHẬT KÝ KIỂM TOÁN HỆ THỐNG (AUDIT_LOGS)
-- ==============================================================================
INSERT INTO audit_logs (id, user_id, action, entity_type, entity_id, new_data, ip_address, user_agent) VALUES
('al000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'CREATE', 'products', 'e0000000-0000-0000-0000-000000000015', '{"name": "AI Personalized Latte Art", "price": 65000}', '127.0.0.1', 'Mozilla/5.0 (Admin Dashboard)'),
('al000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'ASSIGN_ROLE', 'users', '00000000-0000-0000-0000-000000000002', '{"role": "staff"}', '127.0.0.1', 'Mozilla/5.0 (Admin Dashboard)')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 4. MẪU THÔNG BÁO (NOTIFICATION_TEMPLATES)
-- ==============================================================================
INSERT INTO notification_templates (id, code, name, channel, subject, body_template, is_active) VALUES
('00000000-0000-0000-0000-000000000001', 'ORDER_CREATED', 'Đơn hàng đã tạo', 'in_app', 'Đơn hàng mới', 'Đơn hàng #{order_number} của bạn đã được tiếp nhận và đang chờ xử lý.', TRUE),
('00000000-0000-0000-0000-000000000002', 'ORDER_READY', 'Đồ uống sẵn sàng', 'push', 'Món của bạn đã sẵn sàng!', 'Đơn hàng #{order_number} đã hoàn thành, mời bạn đến quầy nhận món.', TRUE),
('00000000-0000-0000-0000-000000000003', 'WELCOME_USER', 'Chào mừng thành viên', 'email', 'Chào mừng đến với AI Café Platform', 'Xin chào {user_name}, chúc bạn có trải nghiệm cà phê công nghệ tuyệt vời!', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 5. BẢNG DỊCH ĐA NGÔN NGỮ (TRANSLATIONS)
-- ==============================================================================
INSERT INTO translations (locale, namespace, key, value) VALUES
('en', 'common', 'welcome', 'Welcome to AI Café Platform'),
('vi', 'common', 'welcome', 'Chào mừng đến với AI Café Platform'),
('en', 'common', 'menu', 'Menu'),
('vi', 'common', 'menu', 'Thực đơn'),
('en', 'common', 'cart', 'Cart'),
('vi', 'common', 'cart', 'Giỏ hàng'),
('en', 'order', 'status.pending', 'Pending'),
('vi', 'order', 'status.pending', 'Đang chờ xử lý'),
('en', 'order', 'status.preparing', 'Preparing'),
('vi', 'order', 'status.preparing', 'Đang pha chế'),
('en', 'order', 'status.ready', 'Ready for Pickup'),
('vi', 'order', 'status.ready', 'Sẵn sàng nhận món'),
('en', 'order', 'status.completed', 'Completed'),
('vi', 'order', 'status.completed', 'Hoàn thành')
ON CONFLICT (locale, namespace, key) DO UPDATE SET value = EXCLUDED.value;

-- ==============================================================================
-- 6. KHU VỰC ĐỊA LÝ (REGIONS)
-- ==============================================================================
INSERT INTO regions (id, company_id, name, code, region_type, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'TP. Hồ Chí Minh', 'HCM', 'city', 1),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Quận 1', 'D1', 'district', 1),
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'TP. Hồ Chí Minh', 'HCM-CORP', 'city', 1),
('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Hà Nội', 'HN-CORP', 'city', 2)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 7. NGƯỜI DÙNG HỆ THỐNG (USERS)
-- ==============================================================================
INSERT INTO users (id, company_id, email, phone, password_hash, full_name, display_name, role, status, email_verified_at) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'admin@aicafe.vn', '0909000001', '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4/XQj8J8X4/X4Xe', 'Quản Trị Viên Hệ Thống', 'Admin', 'admin', 'active', CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'staff@aicafe.vn', '0909000002', '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4/XQj8J8X4/X4Xe', 'Nguyễn Barista', 'Barista Pro', 'staff', 'active', CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'customer@example.com', '0909000003', '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4/XQj8J8X4/X4Xe', 'Trần Thị Khách Hàng', 'Khách Thân Thiết', 'customer', 'active', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 8. CẢNH BÁO CHẤT DỊ ỨNG (ALLERGENS)
-- ==============================================================================
INSERT INTO allergens (id, name, name_vi, icon, color, description) VALUES
('f0000000-0000-0000-0000-000000000001', 'Dairy', 'Sữa & Chế phẩm sữa', '🥛', '#FF6B6B', 'Chứa sữa động vật hoặc lactose'),
('f0000000-0000-0000-0000-000000000002', 'Gluten', 'Gluten ngũ cốc', '🌾', '#FFE66D', 'Chứa lúa mì, lúa mạch'),
('f0000000-0000-0000-0000-000000000003', 'Nuts', 'Các loại hạt', '🥜', '#4ECDC4', 'Chứa đậu phộng hoặc hạt cây'),
('f0000000-0000-0000-0000-000000000004', 'Eggs', 'Trứng', '🥚', '#FFEAA7', 'Chứa trứng gia cầm'),
('f0000000-0000-0000-0000-000000000005', 'Soy', 'Đậu nành', '🫘', '#A29BFE', 'Chứa đậu nành hoặc đậu tương'),
('f0000000-0000-0000-0000-000000000006', 'Caffeine', 'Caffeine', '☕', '#6C5CE7', 'Hàm lượng caffeine tự nhiên cao'),
('f0000000-0000-0000-0000-000000000007', 'Alcohol', 'Cồn thực phẩm', '🍷', '#FD79A8', 'Chứa một lượng nhỏ cồn'),
('f0000000-0000-0000-0000-000000000008', 'Seafood', 'Hải sản', '🦐', '#00B894', 'Chứa thành phần nguồn gốc biển')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 9. NHÀ CUNG CẤP NGUYÊN LIỆU (SUPPLIERS)
-- ==============================================================================
INSERT INTO suppliers (id, company_id, name, code, contact_name, phone, email, address, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Highland Coffee Roastery & Supply', 'HCS', 'Nguyễn Văn Cung Ứng', '0901234567', 'contact@highlandsupply.vn', 'Khu Công Nghiệp Tân Bình, TP.HCM', TRUE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Fresh Dairy Farm & Foods', 'FDC', 'Trần Thị Nông Trại', '0902345678', 'sales@freshdairyfarm.vn', 'Đơn Dương, Tỉnh Lâm Đồng', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 10. DANH MỤC SẢN PHẨM (CATEGORIES)
-- ==============================================================================
INSERT INTO categories (id, company_id, name, name_vi, slug, icon, color, sort_order, is_featured, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Coffee', 'Cà Phê', 'ca-phe', 'coffee', '#6F4E37', 1, TRUE, TRUE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Tea & Milk Tea', 'Trà & Trà Sữa', 'tra-tra-sua', 'tea', '#98D8AA', 2, TRUE, TRUE),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Fresh Juice & Smoothie', 'Nước Ép & Sinh Tố', 'nuoc-ep-sinh-to', 'juice', '#FFB347', 3, TRUE, TRUE),
('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'Bakery & Pastry', 'Bánh & Snack', 'banh-snack', 'cake', '#DDA0DD', 4, FALSE, TRUE),
('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 'Desserts', 'Tráng Miệng', 'trang-mieng', 'dessert', '#FF69B4', 5, TRUE, TRUE),
('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000001', 'AI Specials & Combos', 'Đồ Uống Sáng Tạo AI & Combo', 'ai-specials-combo', 'sparkles', '#9B59B6', 6, TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 11. CHƯƠNG TRÌNH KHUYẾN MÃI (PROMOTIONS)
-- ==============================================================================
INSERT INTO promotions (id, company_id, code, name, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, usage_limit, is_public, is_active, status) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'WELCOME50', 'Chào mừng thành viên mới', 'Giảm trực tiếp 50.000đ cho đơn hàng đầu tiên', 'fixed_amount', 50000, 60000, 50000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '90 days', 5000, TRUE, TRUE, 'active'),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'AICAFE10', 'Ưu đãi cà phê công nghệ', 'Giảm 10% tổng đơn hàng khi đặt qua app', 'percentage', 10, 50000, 30000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', 1000, TRUE, TRUE, 'active')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 12. GÓI HỘI VIÊN (MEMBERSHIP_PLANS)
-- ==============================================================================
INSERT INTO membership_plans (id, company_id, code, name, name_vi, price_monthly, price_yearly, credits_monthly, discount_percent, free_delivery, is_active, is_featured) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'MEM_BASIC', 'Coffee Lover Pass', 'Gói Yêu Cà Phê', 0, 0, 50, 0, FALSE, TRUE, FALSE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'MEM_PRO', 'AI Builder Member', 'Gói Hội Viên AI Pro', 99000, 990000, 500, 5.0, TRUE, TRUE, TRUE),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'MEM_VIP', 'Executive VIP Club', 'Gói Doanh Nhân VIP', 299000, 2990000, 2000, 10.0, TRUE, TRUE, FALSE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 13. HẠNG HỘI VIÊN (LOYALTY_TIERS)
-- ==============================================================================
INSERT INTO loyalty_tiers (id, name, name_vi, min_points, max_points, discount_percent, points_multiplier, color, icon, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', 'Bronze', 'Đồng', 0, 999, 0, 1.0, '#CD7F32', '🥉', 1),
('00000000-0000-0000-0000-000000000002', 'Silver', 'Bạc', 1000, 4999, 2, 1.25, '#C0C0C0', '🥈', 2),
('00000000-0000-0000-0000-000000000003', 'Gold', 'Vàng', 5000, 19999, 5, 1.5, '#FFD700', '🥇', 3),
('00000000-0000-0000-0000-000000000004', 'Platinum', 'Bạch Kim', 20000, NULL, 10, 2.0, '#E5E4E2', '💎', 4)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 14. GÓI NẠP TÍN DỤNG (CREDIT_PLANS)
-- ==============================================================================
INSERT INTO credit_plans (id, name, name_vi, credits, price, credits_per_month, features, is_active, is_featured, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', 'Starter', 'Khởi đầu', 100, 29000, NULL, '["100 AI credits", "Model cơ bản", "Hỗ trợ qua Email"]', TRUE, FALSE, 1),
('00000000-0000-0000-0000-000000000002', 'Pro', 'Chuyên nghiệp', 1000, 199000, NULL, '["1000 AI credits", "Toàn bộ AI models", "Hỗ trợ ưu tiên", "Tùy chỉnh Prompt"]', TRUE, TRUE, 2),
('00000000-0000-0000-0000-000000000003', 'Enterprise', 'Doanh nghiệp', 5000, 790000, NULL, '["5000 AI credits", "Toàn bộ AI models", "Hỗ trợ 24/7", "Truy cập API tích hợp"]', TRUE, FALSE, 3)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 15. DANH MỤC CHI PHÍ (EXPENSE_CATEGORIES)
-- ==============================================================================
INSERT INTO expense_categories (id, name, name_vi, description, is_active, sort_order) VALUES
('00000000-0000-0000-0000-000000000001', 'Supplies', 'Vật tư & Bao bì', 'Chi phí cốc giấy, ống hút, túi mang đi', TRUE, 1),
('00000000-0000-0000-0000-000000000002', 'Utilities', 'Điện, Nước & Internet', 'Chi phí tiện ích cơ sở', TRUE, 2),
('00000000-0000-0000-0000-000000000003', 'Rent', 'Tiền thuê mặt bằng', 'Chi phí thuê địa điểm mở quán', TRUE, 3),
('00000000-0000-0000-0000-000000000004', 'Salaries', 'Lương nhân sự', 'Lương nhân viên pha chế, phục vụ, quản lý', TRUE, 4),
('00000000-0000-0000-0000-000000000005', 'Marketing', 'Quảng cáo & Tiếp thị', 'Chi phí chạy ads, sự kiện ra mắt', TRUE, 5),
('00000000-0000-0000-0000-000000000006', 'Maintenance', 'Bảo trì máy móc', 'Bảo dưỡng máy pha cà phê, máy xay, tủ lạnh', TRUE, 6),
('00000000-0000-0000-0000-000000000007', 'Food Ingredients', 'Nguyên vật liệu pha chế', 'Cà phê hạt, sữa tươi, trà, siro, hoa quả', TRUE, 7),
('00000000-0000-0000-0000-000000000008', 'Other', 'Chi phí khác', 'Các chi phí phát sinh ngoài danh mục', TRUE, 99)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 16. CHI NHÁNH QUÁN (CAFES)
-- ==============================================================================
INSERT INTO cafes (id, company_id, region_id, cafe_code, name, slug, description, address, province, district, phone, is_active, is_delivery_enabled, is_pickup_enabled, is_reservation_enabled, tax_rate) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'AICA-HCM-001', 'AI Café Nguyễn Huệ', 'ai-cafe-nguyen-hue', 'Chi nhánh Flagship hiện đại bậc nhất với Robot pha chế và Trợ lý AI', '123 Nguyễn Huệ, Quận 1, TP.HCM', 'TP. Hồ Chí Minh', 'Quận 1', '02812345678', TRUE, TRUE, TRUE, TRUE, 0.1),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'AICA-HCM-002', 'AI Café Đồng Khởi', 'ai-cafe-dong-khoi', 'Không gian yên tĩnh lý tưởng để làm việc, họp nhóm và sáng tạo công nghệ', '45 Đồng Khởi, Quận 1, TP.HCM', 'TP. Hồ Chí Minh', 'Quận 1', '02823456789', TRUE, TRUE, TRUE, TRUE, 0.1),
('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'AIC001', 'AI Café Quận 1 Central', 'ai-cafe-quan-1-central', 'Quán cà phê trung tâm với quầy pha chế thông minh', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1', 'TP. Hồ Chí Minh', 'Quận 1', '02812345601', TRUE, TRUE, TRUE, TRUE, 0.1),
('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'AIC002', 'AI Café Quận 3', 'ai-cafe-quan-3', 'Không gian xanh mát, sân vườn công nghệ', '456 Điện Biên Phủ, Phường 11, Quận 3', 'TP. Hồ Chí Minh', 'Quận 3', '02812345602', TRUE, TRUE, TRUE, FALSE, 0.1),
('c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'AIC003', 'AI Café Ba Đình Hà Nội', 'ai-cafe-ba-dinh', 'Quán cà phê AI đầu tiên tại thủ đô Hà Nội', '78 Hoàng Hoa Thám, Ba Đình, Hà Nội', 'Hà Nội', 'Ba Đình', '02412345603', TRUE, TRUE, FALSE, FALSE, 0.1)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 17. HỒ SƠ CHI TIẾT NGƯỜI DÙNG (USER_PROFILES)
-- ==============================================================================
INSERT INTO user_profiles (user_id, full_name, display_name, phone, address, city, country, language) VALUES
('00000000-0000-0000-0000-000000000001', 'Quản Trị Viên Hệ Thống', 'Admin', '0909000001', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1', 'TP. Hồ Chí Minh', 'Vietnam', 'vi'),
('00000000-0000-0000-0000-000000000002', 'Nguyễn Barista', 'Barista Pro', '0909000002', '45 Lê Duẩn, Quận 1', 'TP. Hồ Chí Minh', 'Vietnam', 'vi'),
('00000000-0000-0000-0000-000000000003', 'Trần Thị Khách Hàng', 'Khách Thân Thiết', '0909000003', '88 Hai Bà Trưng, Quận 3', 'TP. Hồ Chí Minh', 'Vietnam', 'vi')
ON CONFLICT (user_id) DO NOTHING;

-- ==============================================================================
-- 18. SỞ THÍCH CÁ NHÂN HÓA (USER_PREFERENCES)
-- ==============================================================================
INSERT INTO user_preferences (user_id, language, timezone, currency, ai_model_preference) VALUES
('00000000-0000-0000-0000-000000000001', 'vi', 'Asia/Ho_Chi_Minh', 'VND', 'gpt-4o'),
('00000000-0000-0000-0000-000000000002', 'vi', 'Asia/Ho_Chi_Minh', 'VND', 'gpt-4o-mini'),
('00000000-0000-0000-0000-000000000003', 'vi', 'Asia/Ho_Chi_Minh', 'VND', 'gpt-4o')
ON CONFLICT (user_id) DO NOTHING;

-- ==============================================================================
-- 19. SỔ ĐỊA CHỈ GIAO HÀNG (USER_ADDRESSES)
-- ==============================================================================
INSERT INTO user_addresses (id, user_id, label, address, province, district, ward, phone, receiver_name, is_default) VALUES
('b0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Nhà riêng', '88 Hai Bà Trưng, Phường Bến Nghé, Quận 1', 'Hồ Chí Minh', 'Quận 1', 'Phường Bến Nghé', '0909000003', 'Trần Thị Khách Hàng', TRUE),
('b0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 'Văn phòng', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1', 'Hồ Chí Minh', 'Quận 1', 'Phường Bến Nghé', '0909000003', 'Trần Thị Khách Hàng', FALSE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 20. PHIÊN ĐĂNG NHẬP (REFRESH TOKENS) (REFRESH_TOKENS)
-- ==============================================================================
INSERT INTO refresh_tokens (id, token_hash, user_id, device_info, ip_address, expires_at, revoked) VALUES
('r0000000-0000-0000-0000-000000000001', 'rt_hash_sample_cust_001_secure', '00000000-0000-0000-0000-000000000003', 'iPhone 15 Pro / iOS App', '14.161.20.10', CURRENT_TIMESTAMP + INTERVAL '30 days', FALSE),
('r0000000-0000-0000-0000-000000000002', 'rt_hash_sample_admin_001_secure', '00000000-0000-0000-0000-000000000001', 'Chrome Web / Admin Portal', '127.0.0.1', CURRENT_TIMESTAMP + INTERVAL '30 days', FALSE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 21. SỐ DƯ TÍN DỤNG NGƯỜI DÙNG (USER_CREDITS)
-- ==============================================================================
INSERT INTO user_credits (id, user_id, balance, lifetime_earned, lifetime_used) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 50000, 50000, 0),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 1000, 1000, 0)
ON CONFLICT (user_id) DO NOTHING;

-- ==============================================================================
-- 22. PHIÊN TRÒ CHUYỆN VỚI TRỢ LÝ AI (AI_SESSIONS)
-- ==============================================================================
INSERT INTO ai_sessions (id, user_id, session_type, title, message_count, total_tokens, total_cost, is_active) VALUES
('ai000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'chat', 'Gợi ý thức uống phù hợp tâm trạng', 2, 245, 0.005, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 23. THÔNG BÁO NGƯỜI DÙNG (NOTIFICATIONS)
-- ==============================================================================
INSERT INTO notifications (id, user_id, template_id, type, title, body, channel, status, sent_at) VALUES
('n0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'order', 'Đơn hàng đã hoàn thành', 'Đơn hàng #ORD20241001000001 của bạn đã hoàn thành tại bàn A1.', 'in_app', 'sent', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('n0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', 'promotion', 'Ưu đãi chào mừng thành viên mới', 'Nhận ngay voucher giảm 50.000đ cho đơn hàng đầu tiên qua app!', 'in_app', 'sent', CURRENT_TIMESTAMP - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 24. FCM / APNS DEVICE TOKENS (DEVICE_TOKENS)
-- ==============================================================================
INSERT INTO device_tokens (id, user_id, token, platform, device_info, is_active, last_used_at) VALUES
('d1000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'fcm_token_device_cust_001', 'ios', '{"model": "iPhone 15 Pro", "os": "iOS 18"}', TRUE, CURRENT_TIMESTAMP),
('d1000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'fcm_token_device_admin_001', 'web', '{"browser": "Chrome", "os": "macOS"}', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (user_id, token) DO NOTHING;

-- ==============================================================================
-- 25. THIẾT BỊ NGƯỜI DÙNG (USER_DEVICES)
-- ==============================================================================
INSERT INTO user_devices (id, user_id, device_type, device_id, fcm_token, active, last_active_at) VALUES
('d0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'ios', 'IPHONE15PRO_UUID_01', 'fcm_ios_token_cust_sample_01', TRUE, CURRENT_TIMESTAMP),
('d0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'web', 'WEB_CHROME_ADMIN_01', 'fcm_web_token_adm_sample_01', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 26. NGUYÊN VẬT LIỆU PHA CHẾ (INGREDIENTS)
-- ==============================================================================
INSERT INTO ingredients (id, company_id, name, name_vi, category, unit_id, cost_price, min_stock_level, max_stock_level, supplier_id, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Arabica Cầu Đất Coffee Beans', 'Hạt Cà Phê Arabica Cầu Đất', 'Coffee', 'unit-kg', 250000, 10, 200, '00000000-0000-0000-0000-000000000001', TRUE),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Pasteurized Whole Milk', 'Sữa Tươi Thanh Trùng DalatMilk', 'Dairy', 'unit-l', 35000, 20, 300, '00000000-0000-0000-0000-000000000002', TRUE),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Pure Cane Sugar', 'Đường Mía Tinh Luyện', 'Sweeteners', 'unit-kg', 18000, 10, 150, '00000000-0000-0000-0000-000000000001', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 27. SẢN PHẨM & ĐỒ UỐNG (PRODUCTS)
-- ==============================================================================
INSERT INTO products (id, company_id, category_id, name, name_vi, slug, sku, description, short_description, price, image_url, calories, preparation_time, is_active, is_featured, is_best_seller) VALUES
-- Cà Phê
('e0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Espresso Classic', 'Cà Phê Đen Nguyên Chất', 'ca-phe-den', 'CP001', 'Cà phê rang xay nguyên chất 100% Robusta thơm nồng, đậm vị truyền thống', 'Thức uống classic', 25000, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400', 5, 3, TRUE, FALSE, TRUE),
('e0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Vietnamese Iced Milk Coffee', 'Cà Phê Sữa Đá Sài Gòn', 'ca-phe-sua-da', 'CP002', 'Cà phê Việt Nam pha phin truyền thống hòa quyện cùng sữa đặc béo ngậy', 'Hương vị quen thuộc', 29000, 'https://images.unsplash.com/photo-1578314675249-a6910f80cc39?w=400', 80, 3, TRUE, TRUE, TRUE),
('e0000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Cappuccino Italiano', 'Cappuccino Bọt Mịn', 'cappuccino', 'CP003', 'Espresso với lớp bọt sữa đánh bông mịn chuẩn phong cách Italy', 'Italy authentic', 45000, 'https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400', 120, 4, TRUE, FALSE, FALSE),
('e0000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Caffè Latte', 'Latte Sữa Tươi Nóng', 'latte', 'CP004', 'Espresso nhẹ nhàng kết hợp sữa tươi thanh trùng nóng mềm mượt', 'Smooth & creamy', 45000, 'https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400', 150, 4, TRUE, FALSE, FALSE),
('e0000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Cold Brew Nitro', 'Cà Phê Cold Brew Ủ Lạnh', 'cold-brew', 'CP005', 'Cà phê ủ lạnh 18 tiếng cho hương vị thanh khiết, hậu ngọt tự nhiên', 'Ủ lạnh 18 tiếng', 50000, 'https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?w=400', 10, 2, TRUE, TRUE, FALSE),

-- Trà & Trà Sữa
('e0000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Darjeeling Black Tea', 'Trà Đen Darjeeling', 'tra-den', 'TR001', 'Trà đen nguyên lá Darjeeling thượng hạng giải nhiệt thanh mát', 'Classic tea', 25000, 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=400', 2, 2, TRUE, FALSE, FALSE),
('e0000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Royal Pearl Milk Tea', 'Trà Sữa Trân Châu Hoàng Gia', 'tra-sua-tran-chau', 'TR002', 'Trà sữa đậm vị lá trà tươi kết hợp trân châu đen dai giòn hoàng gia', 'Best seller', 35000, 'https://images.unsplash.com/photo-1558857563-b371033873b8?w=400', 250, 3, TRUE, TRUE, TRUE),
('e0000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Peach Lemongrass Tea', 'Trà Đào Cam Sả', 'tra-dao-cam-sa', 'TR003', 'Trà đào thanh ngọt tự nhiên kết hợp miếng đào giòn và hương sả thơm lừng', 'Refreshing drink', 35000, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 100, 3, TRUE, FALSE, TRUE),
('e0000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Uji Matcha Latte', 'Matcha Latte Nhật Bản', 'matcha-latte', 'TR004', 'Bột trà xanh Uji Kyoto hảo hạng pha với sữa tươi nguyên kem', 'Japanese style', 55000, 'https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=400', 180, 4, TRUE, TRUE, FALSE),

-- Nước Ép & Sinh Tố
('e0000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Fresh Orange Juice', 'Nước Ép Cam Tươi 100%', 'nuoc-ep-cam', 'NE001', 'Cam sành vắt tươi nguyên chất bổ sung vitamin C mỗi ngày', 'Fresh daily', 35000, 'https://images.unsplash.com/photo-1534353473418-4cfa6c56fd38?w=400', 90, 2, TRUE, FALSE, FALSE),
('e0000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Avocado Creamy Smoothie', 'Sinh Tố Bơ Sáp Dừa', 'sinh-to-bo', 'NE002', 'Bơ sáp Đắk Lắk xay cùng sữa đặc và nước cốt dừa thơm béo', 'Creamy delight', 40000, 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400', 200, 3, TRUE, TRUE, FALSE),
('e0000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Berry Blast Smoothie', 'Sinh Tố Dâu Tây Rừng', 'smoothie-dau', 'SM001', 'Dâu tây Đà Lạt xay với sữa chua Hy Lạp giàu protein', 'Berry blast', 45000, 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=400', 150, 3, TRUE, FALSE, FALSE),

-- Bánh & Tráng Miệng
('e0000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', 'French Butter Croissant', 'Bánh Sừng Bò Bơ Pháp', 'croissant-bo', 'BN001', 'Bánh Croissant vỏ ngàn lớp giòn rụm với bơ Pháp Elle & Vire', 'French style', 35000, 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400', 250, 3, TRUE, TRUE, TRUE),
('e0000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 'Classic Italian Tiramisu', 'Bánh Tiramisu Truyền Thống', 'tiramisu', 'DES001', 'Lớp kem Mascarpone béo thơm quyện rượu Kahlua và cà phê Espresso', 'Italian dessert', 65000, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400', 380, 2, TRUE, TRUE, TRUE),

-- Đồ Uống Sáng Tạo AI & Combo
('e0000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006', 'AI Personalized Latte Art', 'Latte Vẽ Hình Bằng AI', 'ai-latte-art', 'AISP001', 'Robot in hình chân dung hoặc nghệ thuật tùy chọn trực tiếp lên bọt sữa', 'AI innovation', 65000, 'https://images.unsplash.com/photo-1534432182912-63863115e106?w=400', 190, 5, TRUE, TRUE, TRUE),
('e0000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006', 'AI Mood Booster Blend', 'Cà Phê Pha Theo Cảm Xúc AI', 'ai-mood-blend', 'AISP002', 'Công thức đồ uống độc bản được gợi ý theo phân tích tâm trạng người dùng', 'Unique recipe', 75000, 'https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=400', 160, 5, TRUE, TRUE, FALSE),
('e0000000-0000-0000-0000-000000000017', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006', 'Morning Energizer Combo', 'Combo Năng Lượng Sáng', 'combo-sang', 'CM001', '1 Cà phê sữa đá sảng khoái + 1 Bánh mì bơ tỏi thơm lừng giòn tan', 'Bữa sáng hoàn hảo', 49000, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400', 260, 5, TRUE, TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 28. ĐĂNG KÝ GÓI HỘI VIÊN (SUBSCRIPTIONS)
-- ==============================================================================
INSERT INTO subscriptions (id, user_id, plan_id, status, billing_cycle, start_date, end_date, next_billing_date, auto_renew, credits_remaining, credits_total) VALUES
('s0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', 'active', 'monthly', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '25 days', CURRENT_TIMESTAMP + INTERVAL '25 days', TRUE, 500, 500)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 29. TÀI KHOẢN ĐIỂM TÍCH LŨY (LOYALTY_ACCOUNTS)
-- ==============================================================================
INSERT INTO loyalty_accounts (id, user_id, company_id, tier_id, points_balance, points_lifetime, lifetime_points, points_used, points_to_next_tier, next_tier, total_orders, total_spent) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 150, 150, 150, 0, 850, 'Silver', 3, 450000)
ON CONFLICT (user_id) DO NOTHING;

-- ==============================================================================
-- 30. NHÂN SỰ VÀ PHÂN QUYỀN CAFE (STAFFS)
-- ==============================================================================
INSERT INTO staffs (id, user_id, company_id, cafe_id, staff_code, role, department, hire_date, is_active) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'STF001', 'barista', 'Pha Chế', '2024-01-15', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 31. SƠ ĐỒ BÀN GHẾ (SEATS)
-- ==============================================================================
INSERT INTO seats (id, cafe_id, seat_number, zone, seat_type, capacity, pos_x, pos_y, hourly_rate, is_available, status) VALUES
('70000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'A1', 'VIP Lounge', 'booth', 4, 10, 10, 50000, TRUE, 'available'),
('70000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'A2', 'VIP Lounge', 'booth', 4, 10, 60, 50000, TRUE, 'available'),
('70000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'B1', 'Main Hall', 'table', 2, 100, 10, 0, TRUE, 'available'),
('70000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'B2', 'Main Hall', 'table', 2, 100, 60, 0, TRUE, 'available'),
('70000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 'B3', 'Main Hall', 'table', 2, 100, 110, 0, TRUE, 'available'),
('70000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000001', 'C1', 'Outdoor Terrace', 'outdoor', 4, 200, 10, 0, TRUE, 'available'),
('70000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000001', 'C2', 'Outdoor Terrace', 'outdoor', 4, 200, 60, 0, TRUE, 'available')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 32. GIỎ HÀNG (CARTS)
-- ==============================================================================
INSERT INTO carts (id, user_id, company_id, cafe_id, subtotal, item_count, promo_code, discount_amount, expires_at) VALUES
('ca000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 64000, 2, NULL, 0, CURRENT_TIMESTAMP + INTERVAL '24 hours')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 33. HỒ SƠ KHÁCH HÀNG THÂN THIẾT (CUSTOMERS)
-- ==============================================================================
INSERT INTO customers (user_id, loyalty_tier_id, loyalty_points, lifetime_points, total_orders, total_spent, visit_count) VALUES
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 150, 150, 3, 450000, 5)
ON CONFLICT (user_id) DO NOTHING;

-- ==============================================================================
-- 34. TIN NHẮN TRỢ LÝ AI (AI_MESSAGES)
-- ==============================================================================
INSERT INTO ai_messages (id, session_id, role, content, model, tokens_used, input_tokens, output_tokens, cost) VALUES
('am000000-0000-0000-0000-000000000001', 'ai000000-0000-0000-0000-000000000001', 'user', 'Hôm nay tôi thấy mệt mỏi, quán có gợi ý đồ uống gì thanh mát giúp tỉnh táo không?', 'gpt-4o', 45, 45, 0, 0),
('am000000-0000-0000-0000-000000000002', 'ai000000-0000-0000-0000-000000000001', 'assistant', 'Chào bạn! Quán gợi ý bạn dùng món Cold Brew Nitro hoặc Trà Đào Cam Sả. Cold Brew Nitro ủ lạnh 18 tiếng có hậu ngọt thanh mát giúp bạn tỉnh táo sảng khoái ngay!', 'gpt-4o', 200, 45, 155, 0.005)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 35. THỐNG KÊ TOKEN & CHI PHÍ AI (AI_USAGE)
-- ==============================================================================
INSERT INTO ai_usage (id, user_id, session_id, model_id, provider, endpoint, input_tokens, output_tokens, total_tokens, cost) VALUES
('au000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'ai000000-0000-0000-0000-000000000001', 'gpt-4o', 'openai', 'chat/completions', 45, 155, 200, 0.005)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 36. BIẾN THỂ SẢN PHẨM (SIZE/ICE/SUGAR) (PRODUCT_VARIANTS)
-- ==============================================================================
INSERT INTO product_variants (id, product_id, name, sku, price, original_price, stock, options, is_default, sort_order, is_active) VALUES
('v0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001', 'Size M (Vừa)', 'CP001-M', 25000, 25000, 500, '{"size": "M"}', TRUE, 1, TRUE),
('v0000000-0000-0000-0000-000000000002', 'e0000000-0000-0000-0000-000000000001', 'Size L (Lớn)', 'CP001-L', 30000, 30000, 500, '{"size": "L"}', FALSE, 2, TRUE),
('v0000000-0000-0000-0000-000000000003', 'e0000000-0000-0000-0000-000000000002', 'Size M (Vừa)', 'CP002-M', 29000, 29000, 500, '{"size": "M"}', TRUE, 1, TRUE),
('v0000000-0000-0000-0000-000000000004', 'e0000000-0000-0000-0000-000000000002', 'Size L (Lớn)', 'CP002-L', 35000, 35000, 500, '{"size": "L"}', FALSE, 2, TRUE),
('v0000000-0000-0000-0000-000000000005', 'e0000000-0000-0000-0000-000000000007', 'Size M (Vừa)', 'TR002-M', 35000, 35000, 500, '{"size": "M"}', TRUE, 1, TRUE),
('v0000000-0000-0000-0000-000000000006', 'e0000000-0000-0000-0000-000000000007', 'Size L (Lớn)', 'TR002-L', 42000, 42000, 500, '{"size": "L"}', FALSE, 2, TRUE)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 37. PHẦN THƯỞNG ĐỔI ĐIỂM (REWARDS)
-- ==============================================================================
INSERT INTO rewards (id, company_id, code, name, description, category, reward_type, discount_type, discount_value, min_order_amount, points_cost, points_required, total_quantity, remaining_quantity, quantity, quantity_remaining, validity_days, applicable_tiers, status) VALUES
('c0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'REW-DISC-10K', 'Voucher Giảm 10.000đ', 'Giảm trực tiếp 10.000đ cho đơn từ 40.000đ', 'discount', 'discount', 'fixed', 10000, 40000, 100, 100, 500, 500, 500, 500, 30, 'bronze,silver,gold,platinum', 'active'),
('c0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'REW-FREE-SHIP', 'Miễn Phí Giao Hàng', 'Freeship cho đơn hàng giao tận nơi', 'discount', 'free_delivery', 'fixed', 15000, 50000, 150, 150, 300, 300, 300, 300, 30, 'silver,gold,platinum', 'active'),
('c0000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'REW-FREE-CF', 'Tặng 1 Ly Cà Phê Sữa Đá', 'Đổi 1 ly cà phê sữa đá miễn phí', 'free_item', 'free_item', 'fixed', 29000, 0, 200, 200, 200, 200, 200, 200, 15, 'gold,platinum', 'active')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 38. ĐẶT BÀN TRƯỚC (SEAT_RESERVATIONS)
-- ==============================================================================
INSERT INTO seat_reservations (id, reservation_code, seat_id, user_id, order_id, start_time, end_time, guest_count, status, notes, checked_in_at, checked_out_at) VALUES
('sr000000-0000-0000-0000-000000000001', 'RESV0001', '70000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'o0000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour', 2, 'completed', 'Bàn tiếp khách đối tác', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 39. ĐƠN HÀNG (ORDERS)
-- ==============================================================================
INSERT INTO orders (id, order_number, user_id, company_id, cafe_id, seat_id, order_type, status, payment_status, payment_method, subtotal, discount_amount, tax_amount, total_amount, total_paid, customer_note, confirmed_at, completed_at) VALUES
('o0000000-0000-0000-0000-000000000001', 'ORD20241001000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', 'dine_in', 'completed', 'paid', 'vnpay', 93000, 0, 9300, 102300, 102300, 'Mang tại bàn A1 VIP Lounge', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '30 minutes'),
('o0000000-0000-0000-0000-000000000002', 'ORD20241002000002', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', NULL, 'delivery', 'preparing', 'paid', 'momo', 65000, 0, 6500, 86500, 86500, 'Giao trước sảnh toà nhà', CURRENT_TIMESTAMP - INTERVAL '1 hour', NULL)
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 40. CHI TIẾT MẶT HÀNG TRONG GIỎ (CART_ITEMS)
-- ==============================================================================
INSERT INTO cart_items (id, cart_id, product_id, variant_id, product_name, quantity, options, modifiers, options_json, modifiers_json, unit_price, line_total, notes) VALUES
('ci000000-0000-0000-0000-000000000001', 'ca000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000002', 'v0000000-0000-0000-0000-000000000003', 'Cà Phê Sữa Đá Sài Gòn', 1, '{"sugar": "50%", "ice": "100%"}', '[]', '{"sugar": "50%", "ice": "100%"}', '[]', 29000, 29000, 'Ít ngọt nhiều đá'),
('ci000000-0000-0000-0000-000000000002', 'ca000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000013', NULL, 'Bánh Sừng Bò Bơ Pháp', 1, '{}', '[]', '{}', '[]', 35000, 35000, 'Hâm nóng giòn')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 41. CHI TIẾT MÓN TRONG ĐƠN HÀNG (ORDER_ITEMS)
-- ==============================================================================
INSERT INTO order_items (id, order_id, product_id, variant_id, product_name, quantity, unit_price, line_total, item_status, notes, options, modifiers, options_json, modifiers_json) VALUES
('oi000000-0000-0000-0000-000000000001', 'o0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000002', 'v0000000-0000-0000-0000-000000000003', 'Cà Phê Sữa Đá Sài Gòn', 2, 29000, 58000, 'served', '1 ly ít đường', '{"size": "M"}', '[]', '{"size": "M"}', '[]'),
('oi000000-0000-0000-0000-000000000002', 'o0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000013', NULL, 'Bánh Sừng Bò Bơ Pháp', 1, 35000, 35000, 'served', NULL, '{}', '[]', '{}', '[]'),
('oi000000-0000-0000-0000-000000000003', 'o0000000-0000-0000-0000-000000000002', 'e0000000-0000-0000-0000-000000000015', NULL, 'Latte Vẽ Hình Bằng AI', 1, 65000, 65000, 'preparing', 'In hình logo AI', '{}', '[]', '{}', '[]')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 42. THANH TOÁN ĐƠN HÀNG (PAYMENTS)
-- ==============================================================================
INSERT INTO payments (id, order_id, user_id, company_id, transaction_id, payment_method, status, amount, currency, paid_at, note) VALUES
('p0000000-0000-0000-0000-000000000001', 'o0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'VNP14568901', 'vnpay', 'completed', 102300, 'VND', CURRENT_TIMESTAMP - INTERVAL '1 day', 'Thanh toán đơn #ORD20241001000001 qua VNPay'),
('p0000000-0000-0000-0000-000000000002', 'o0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'MOMO98765432', 'momo', 'completed', 86500, 'VND', CURRENT_TIMESTAMP - INTERVAL '1 hour', 'Thanh toán đơn #ORD20241002000002 qua MoMo')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 43. GIAO DỊCH TÍCH / TIÊU ĐIỂM (LOYALTY_TRANSACTIONS)
-- ==============================================================================
INSERT INTO loyalty_transactions (id, user_id, company_id, type, points, balance_after, order_id, description) VALUES
('lt000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'earn', 100, 150, 'o0000000-0000-0000-0000-000000000001', 'Tích 100 điểm từ đơn hàng #ORD20241001000001'),
('lt000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'bonus', 50, 150, NULL, 'Thưởng thành viên mới hoàn tất hồ sơ')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 44. LỊCH SỬ ĐỔI THƯỞNG (REWARD_REDEMPTIONS)
-- ==============================================================================
INSERT INTO reward_redemptions (id, user_id, company_id, reward_id, order_id, points_spent, code, qr_code, status, used_at) VALUES
('rr000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'o0000000-0000-0000-0000-000000000001', 100, 'REW-DISC-10K-A1B2', 'QR_REW_DISC_10K_A1B2', 'used', CURRENT_TIMESTAMP - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 45. GIAO DỊCH BIẾN ĐỘNG TÍN DỤNG (CREDIT_TRANSACTIONS)
-- ==============================================================================
INSERT INTO credit_transactions (id, user_id, type, amount, balance_before, balance_after, reference_type, description) VALUES
('ct000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'topup', 1000, 0, 1000, 'credit_plan', 'Khởi tạo số dư gói tín dụng AI ban đầu')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 46. GIAO DỊCH CỔNG THANH TOÁN (GATEWAY) (PAYMENT_TRANSACTIONS)
-- ==============================================================================
INSERT INTO payment_transactions (id, payment_id, transaction_type, status, provider, provider_transaction_id) VALUES
('pt000000-0000-0000-0000-000000000001', 'p0000000-0000-0000-0000-000000000001', 'callback', 'completed', 'vnpay', 'VNP_TRANS_14568901'),
('pt000000-0000-0000-0000-000000000002', 'p0000000-0000-0000-0000-000000000002', 'callback', 'completed', 'momo', 'MOMO_TRANS_98765432')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 47. HOÀN TIỀN ĐƠN HÀNG (REFUNDS)
-- ==============================================================================
INSERT INTO refunds (id, payment_id, order_id, user_id, amount, status, reason, admin_id, processed_at, provider_refund_id, refund_transaction_id) VALUES
('rf000000-0000-0000-0000-000000000001', 'p0000000-0000-0000-0000-000000000001', 'o0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 10000, 'completed', 'Hoàn tiền tích lũy ưu đãi sự kiện', '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP - INTERVAL '12 hours', 'REF_VNP_001', 'REF_TX_001')
ON CONFLICT (id) DO NOTHING;

-- ==============================================================================
-- 48. HÓA ĐƠN ĐIỆN TỬ (INVOICES)
-- ==============================================================================
INSERT INTO invoices (id, invoice_number, user_id, company_id, type, status, subtotal, tax_rate, tax_amount, total_amount, payment_method, payment_id, paid_at, customer_name, customer_email) VALUES
('inv00000-0000-0000-0000-000000000001', 'INV-2024-0001', '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'order', 'paid', 93000, 0.1, 9300, 102300, 'vnpay', 'p0000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP - INTERVAL '1 day', 'Trần Thị Khách Hàng', 'customer@example.com')
ON CONFLICT (id) DO NOTHING;

COMMIT;

-- ==============================================================================
-- KẾT THÚC INSERT DỮ LIỆU MẪU ĐẦY ĐỦ CHO TẤT CẢ 48 BẢNG
-- Chạy AICafePlatformSummaryCount.sql để kiểm tra số lượng dòng của từng bảng!
-- ==============================================================================
